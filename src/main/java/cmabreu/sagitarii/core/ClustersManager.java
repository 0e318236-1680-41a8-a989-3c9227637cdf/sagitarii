package cmabreu.sagitarii.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmabreu.sagitarii.core.delivery.InstanceDeliveryControl;
import cmabreu.sagitarii.core.types.PipelineStatus;
import cmabreu.sagitarii.persistence.entity.Pipeline;

public class ClustersManager {
	private List<Cluster> clusterList;
	private static ClustersManager cm;
	private int lastQuant = 0;
	private Logger logger = LogManager.getLogger( this.getClass().getName() );

	public static ClustersManager getInstance() {
		if ( cm == null ) {
			cm = new ClustersManager();
		}
		return cm;
	}
	
	public void acceptTask( String pipelineId, String macAddress) {
		logger.debug( "node " + macAddress + " accepted task in instance " + pipelineId );
		Cluster clu = cm.getCluster(macAddress);
		for ( Pipeline instance : clu.getRunningInstances() ) {
			if ( instance.getSerial().equalsIgnoreCase( pipelineId ) ) {
				instance.setStatus( PipelineStatus.RUNNING );
				break;
			}
		}
		
	}


	public void setErrorLog( String errorLog, String macAddress) {
		logger.debug( "node " + macAddress + " report error: " + errorLog );
		Cluster clu = cm.getCluster(macAddress);
		clu.setLastError( errorLog );
	}

	
	public boolean hasClusters() {
		boolean hasAliveNodes = false;
		boolean result = false;
		for ( Cluster clu : clusterList ) {
			if ( !clu.isMainCluster() && !clu.isDead() ) {
				hasAliveNodes = true;
			}
			if ( !clu.isMainCluster() ) {
				result = true;
			}
		}
		return ( result && hasAliveNodes );
	}
	
	public void refuseTask( String pipelineId, String macAddress ) {
		logger.debug( "node " + macAddress + " refused task in instance " + pipelineId );
		Cluster clu = cm.getCluster(macAddress);
		clu.setLastError("Cannot run task in instance " + pipelineId );
		clu.cancelAndRemovePipeline( pipelineId );
	}

	
	public void confirmReceiveData( ReceivedData rd ) throws Exception {
		Cluster cluster = cm.getCluster( rd.getMacAddress() );
		if ( cluster != null ) {
			logger.debug( "receiving instance "+ rd.getInstance().getSerial() +" data from cluster " + rd.getMacAddress() );
			try {
				cluster.confirmReceiveData( rd );
			} catch ( Exception e ) {
				e.printStackTrace();
				logger.error("activity " + rd.getInstance().getSerial() + ": " + e.getMessage() );
				cluster.setLastError( "activity " + rd.getInstance().getSerial() + ": " + e.getMessage()  );
				throw e;
			}
		}
	}

	public void reloadWrappers() throws Exception {
		if ( Sagitarii.getInstance().isRunning() ) {
			for ( Cluster clu : cm.getClusterList() ) {
				if ( !clu.isMainCluster() ) {
					clu.reloadWrappers();
				}
			}
		} else {
			throw new Exception("Cannot reload wrappers when experiments are running.");
		}
	}
	
	public void cleanWorkspaces() throws Exception {
		if ( Sagitarii.getInstance().getRunningExperiments().size() > 0 ) {
			throw new Exception("Cannot clean nodes workspaces when experiments are running.");
		} else { 
			for ( Cluster clu : cm.getClusterList() ) {
				if ( !clu.isMainCluster() ) {
					clu.cleanWorkspace();
				}
			}
		}
	}
	
	public void quit(String macAddress) {
		Cluster cluster = cm.getCluster(macAddress);
		if ( !cluster.isMainCluster() ) {
			cluster.quit();
		}
	}

	public void restart(String macAddress) {
		Cluster cluster = cm.getCluster(macAddress);
		if ( !cluster.isMainCluster() ) {
			cluster.restart();
		}
	}

	/**
	 * Troca a TAG ##TAG_ID_PIPELINE## pelo ID do pipeline.
	 * Isto é necessário pois não se possuía o ID do pipeline quando o XML foi
	 * gerado (antes de gravar no banco) e é necessário enviar este ID ao Nó
	 * para facilitar o encontro do mesmo pipeline quando a tarefa for concluída.
	 * (O nó não precisa do ID, ele vai devolver ao Sagitarii junto com os dados).
	 * 
	 * @param pipeline
	 * @return
	 */
	private String fillPipelineID( Pipeline pipeline ) {
		return pipeline.getContent().replace("##TAG_ID_PIPELINE##", String.valueOf( pipeline.getIdPipeline() ) );
	}
	
	
	private synchronized String getNextInstance( Cluster cluster ) {
		String resposta = "";
		String macAddress = cluster.getMacAddress();
		Pipeline instance = Sagitarii.getInstance().getNextInstance();
		if ( instance != null ) {
			logger.debug( "sending instance "+ instance.getType() + " (" + instance.getExecutorAlias() + ") "+ instance.getSerial() +" data to node " + macAddress );
			instance.setStatus( PipelineStatus.WAITING );
			cluster.addPipeline(instance);
			resposta = fillPipelineID ( instance );
			instance.setContent( resposta );
			InstanceDeliveryControl.getInstance().addUnit(instance, macAddress);
		} 
		return resposta;
	}
	
	public  String getTask(String macAddress) {
		logger.debug("node " + macAddress + " requesting task");
		String resposta = "";
		Cluster cluster = cm.getCluster(macAddress);
		if ( (cluster != null)  ) {
			// if it is allowed to receive new tasks...
			if ( ( !cluster.isReloadWrappersSignal() ) && ( !cluster.isQuitSignal() ) && ( !cluster.isRestartSignal() ) && ( !cluster.isCleanWorkspaceSignal() ) ) {
				resposta = getNextInstance( cluster );
			} else {
				logger.warn("node " + macAddress + " not allowed to run tasks for now");
				// if not...
				if ( !cluster.isMainCluster() ) {
					if ( cluster.isReloadWrappersSignal() ) {
						resposta = "RELOAD_WRAPPERS";
					}
					if ( cluster.isQuitSignal() ) {
						resposta = "COMM_QUIT";
					}
					if ( cluster.isCleanWorkspaceSignal() ) {
						resposta = "COMM_CLEAN_WORKSPACE";
					}
					if ( cluster.isRestartSignal() ) {
						resposta = "COMM_RESTART";
					}
					cluster.clearSignals();
				}
			}
			
		}
		if ( resposta.length() == 0 ) {
			logger.warn("empty instance sent to node " + macAddress + ". System idle.");
		} else {
			logger.debug("task sent to node " + macAddress );
		}
		return resposta;
	}
	
	public boolean haveNewCluster() {
		if ( clusterList.size() != lastQuant ) {
			lastQuant = clusterList.size();
			return true;
		}
		return false;
	}
	
	public List<Cluster> getClusterList() {
		return new ArrayList<Cluster>( clusterList );
	}
	
	/**
	 * A classe não pode ser instanciada por outras.
	 */
	private ClustersManager() {
		clusterList = new ArrayList<Cluster>();
	}
	
	private Cluster getCluster(String macAddress) {
		for ( Cluster clu : getClusterList()  ) {
			if ( clu.getMacAddress().equalsIgnoreCase( macAddress ) ) {
				return clu;
			}
		}
		return null;
	}


	public void updateClustersStatus() {
		for ( Cluster clu : clusterList  ) {
			clu.updateStatus();
		}
	}

	
	public Cluster addOrUpdateCluster(String javaVersion, String soFamily, String macAddress, 
			String ipAddress, String machineName, Double cpuLoad, String soName, 
			int availableProcessors, int maxAllowedTasks, long freeMemory, long totalMemory) {
		Cluster retorno = null;
		Cluster clu = cm.getCluster(macAddress);
		if ( clu != null ) {
			clu.setMachineName( machineName );
			clu.setIpAddress( ipAddress );
			clu.setAvailableProcessors( availableProcessors );
			clu.setSoName( soName );
			clu.setLastAnnounce( Calendar.getInstance().getTime() );
			clu.setCpuLoad( cpuLoad );
			clu.setMaxAllowedTasks( maxAllowedTasks );
			clu.setTotalMemory(totalMemory);
			clu.setFreeMemory(freeMemory);
			clu.updateStatus();
			retorno = clu;
		} else {
			Cluster c1 = new Cluster(javaVersion,soFamily,macAddress,ipAddress,machineName,
					cpuLoad,soName,availableProcessors,maxAllowedTasks,freeMemory,totalMemory);
			clusterList.add( c1 );
			retorno = c1;
		}
		return retorno;
	}
	
}
