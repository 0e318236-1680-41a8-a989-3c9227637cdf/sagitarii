package br.cefetrj.sagitarii.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.cefetrj.sagitarii.core.config.Configurator;
import br.cefetrj.sagitarii.core.delivery.InstanceDeliveryControl;
import br.cefetrj.sagitarii.core.types.ClusterStatus;
import br.cefetrj.sagitarii.core.types.ClusterType;
import br.cefetrj.sagitarii.core.types.InstanceStatus;
import br.cefetrj.sagitarii.core.types.LogType;
import br.cefetrj.sagitarii.misc.PathFinder;
import br.cefetrj.sagitarii.misc.ZipUtil;
import br.cefetrj.sagitarii.misc.json.NodeTasks;
import br.cefetrj.sagitarii.persistence.entity.Instance;

import com.google.gson.Gson;

public class NodesManager {
	private List<Cluster> clusterList;
	private static NodesManager cm;
	private int lastQuant = 0;
	private Logger logger = LogManager.getLogger( this.getClass().getName() );

	public static NodesManager getInstance() {
		if ( cm == null ) {
			cm = new NodesManager();
		}
		return cm;
	}
	
	private void checkLazy() {
		for ( Cluster cluster : getClusterList() ) {
			if ( cluster.getStatus() != ClusterStatus.IDLE ) continue;
			InstanceDeliveryControl.getInstance().claimInstance( cluster );
		}
	}
	
	public void saveMetrics() {
		try {
			String path = PathFinder.getInstance().getPath() + "/metrics/";
			File newFile = new File(path);
			newFile.mkdirs();
			for ( Cluster cluster : getClusterList() ) {
				cluster.saveMetricImages( path );
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	
	public int getCores() {
		int cores = 0;
		for ( Cluster clu : getClusterList()  ) {
			if ( ( !clu.isMainCluster() ) && ( clu.getStatus() == ClusterStatus.ACTIVE ) ) {
				cores = cores + clu.getAvailableProcessors();
			}
		}
		return cores;
	}
	
	public void clearNodeLog( String macAddress ) {
		logger.debug( "will clear log for node " + macAddress );
		Cluster clu = getCluster(macAddress);
		if ( clu != null ) {
			clu.clearLog();
		}
	}

	public void clearNodeTasks( String macAddress ) {
		logger.debug( "will clear tasks for node " + macAddress );
		Cluster clu = getCluster(macAddress);
		if ( clu != null ) {
			clu.clearTasks();
		}
	}

	public void acceptTask( String instanceId, String macAddress) {
		logger.debug( "node " + macAddress + " accepted task in instance " + instanceId );
		Cluster clu = getCluster(macAddress);
		for ( Instance instance : clu.getRunningInstances() ) {
			if ( instance.getSerial().equalsIgnoreCase( instanceId ) ) {
				instance.setStatus( InstanceStatus.RUNNING );
				break;
			}
		}
	}

	public void receiveNodeTasks( String data ) {
		if ( data == null ) {
			return;
		}
		try {
			Gson gson = new Gson();
			NodeTasks tasks = gson.fromJson( data, NodeTasks.class );
			Cluster cluster = getCluster( tasks.getNodeId() );
			
			if ( cluster != null ) {
				cluster.setLastAnnounce( Calendar.getInstance().getTime() );
				cluster.setCpuLoad( tasks.getCpuLoad() );
				cluster.setTotalMemory( tasks.getTotalMemory() );
				cluster.setFreeMemory( tasks.getFreeMemory() );
				cluster.setFreeDiskSpace( tasks.getFreeDiskSpace() );
				cluster.setTotalDiskSpace( tasks.getTotalDiskSpace() );
				cluster.setTasks( tasks.getData() );
				cluster.setMemoryPercent( tasks.getMemoryPercent() );
				cluster.setMaxAllowedTasks( tasks.getMaximunLimit() );
				cluster.updateStatus();
			}
			
		} catch ( Exception e ) {
			logger.error( e.getMessage() );
		}
	}

	public void receiveNodeLog( String activitySerial, String message, String macAddress) {
		logger.debug( "node " + macAddress + " report (" + activitySerial + "): " + message );
		Cluster clu = getCluster(macAddress);
		clu.setMessage( LogType.NODE, message, activitySerial );
	}

	
	public boolean hasClusters() {
		boolean hasAliveNodes = false;
		boolean result = false;
		for ( Cluster clu : getClusterList() ) {
			if ( !clu.isMainCluster() && !clu.isDead() ) {
				hasAliveNodes = true;
			}
			if ( !clu.isMainCluster() ) {
				result = true;
			}
		}
		return ( result && hasAliveNodes );
	}
	
	public void refuseTask( String instanceId, String macAddress ) {
		logger.debug( "node " + macAddress + " refused task in instance " + instanceId );
		Cluster clu = getCluster(macAddress);
		clu.setMessage(LogType.SYSTEM, "Cannot run task in instance " + instanceId, "" );
		clu.resubmitInstanceToBuffer( instanceId );
	}

	public synchronized Cluster tryToFindInstance( String instanceSerial ) {
		for ( Cluster cl : getClusterList() ) {
			if ( cl.isRunning(instanceSerial) ) return cl;
		}
		return null;
	}
	
	public void confirmReceiveData( ReceivedData rd ) throws Exception {
		String instanceSerial = rd.getInstance().getSerial();
		logger.debug( "receiving instance "+ instanceSerial +" close command from node " + rd.getMacAddress() );
		
		Cluster cl = tryToFindInstance( instanceSerial ); //getCluster( rd.getMacAddress() );
		
		if ( cl != null ) {
			if ( !rd.getMacAddress().equals( cl.getmacAddress() ) ) {
				logger.debug("Wrong running node. Must be " + rd.getMacAddress() + " but find in " + cl.getmacAddress() );
			}
			try {
				cl.confirmReceiveData( rd );
			} catch ( Exception e ) {
				logger.error("activity " + rd.getInstance().getSerial() + ": " + e.getMessage() );
				cl.setMessage( LogType.SYSTEM, e.getMessage(), rd.getInstance().getSerial() );
				throw e;
			}
		} else {
			logger.error("Cannot find who is running instance " + instanceSerial + ". Must be node " + rd.getMacAddress() );
		}
	}
	
	public void finishInstance( ReceivedData rd ) {
		Cluster cluster = getCluster( rd.getMacAddress() );
		if ( cluster != null ) {
			logger.debug( "finishing instance "+ rd.getInstance().getSerial() +" from cluster " + rd.getMacAddress() );
			try {
				cluster.finishInstance( rd );
			} catch ( Exception e ) {
				logger.error("activity " + rd.getInstance().getSerial() + ": " + e.getMessage() );
				cluster.setMessage( LogType.SYSTEM, e.getMessage(), rd.getInstance().getSerial()  );
				throw e;
			}
		}
	}

	public void reloadWrappers() {
		for ( Cluster clu : getClusterList() ) {
			if ( !clu.isMainCluster() ) {
				clu.reloadWrappers();
			}
		}
	}
	
	public void cleanWorkspaces() throws Exception {
		if ( Sagitarii.getInstance().getRunningExperiments().size() > 0 ) {
			throw new Exception("Cannot clean nodes workspaces when experiments are running.");
		} else { 
			for ( Cluster clu : getClusterList() ) {
				if ( !clu.isMainCluster() ) {
					clu.cleanWorkspace();
				}
			}
		}
	}
	
	public void quit(String macAddress) {
		Cluster cluster = getCluster(macAddress);
		if ( !cluster.isMainCluster() ) {
			cluster.quit();
		}
	}

	public void restart(String macAddress) {
		Cluster cluster = getCluster(macAddress);
		if ( !cluster.isMainCluster() ) {
			cluster.restart();
		}
	}

	/*
	public void inform(String macAddress, String instanceSerial, boolean fromUser ) {
		logger.debug("Sagitarii needs to know about instance " + instanceSerial + " running on node " + macAddress );
		Cluster cluster = getCluster(macAddress);
		if ( cluster != null ) {
			logger.debug("node " + macAddress + " found as connected. asking...");
			cluster.inform( instanceSerial, fromUser );
		} else {
			logger.debug("cluster " + macAddress + " not connected.");
		}
		logger.debug("inform check done.");
	}
	
	public void informReport( String macAddress, String status, String instanceSerial ) {
		logger.debug("Teapot node " + macAddress + " informs instance " + instanceSerial + " status as " + status );
		Cluster cluster = getCluster(macAddress);
		if ( cluster != null ) {
			cluster.informReport( instanceSerial, status );
		} else {
			logger.error("cluster " + macAddress + " not connected");
		}
	}
	*/
	
	private String fillInstanceID( Instance instance ) {
		String content = instance.getContent();
		try {
			content = instance.getContent().replace("%ID_PIP%", String.valueOf( instance.getIdInstance() ) );
		} catch ( Exception e ) {
			logger.error("Error setting Instance ID to instance content tag.");
		}
		return content.replace("##TAG_ID_INSTANCE##", String.valueOf( instance.getIdInstance() ) );
	}
	
	
	private synchronized String getNextInstance( Cluster cluster, int packageSize, String nodeType ) {
		String resposta = "";
		String macAddress = cluster.getmacAddress();
		if ( packageSize < 1 ) { packageSize = 1; }
		try {
			if ( !Configurator.getInstance().useDynamicLoadBalancer() ) {
				logger.debug( "not using Dynamic Load Balancer. Sending 1 Instance per packet.");
				packageSize = 1;
			}
		} catch ( Exception e ) { }
		
		List<String> instancePack = new ArrayList<String>();
		//logger.debug( "node " + macAddress + " ("+nodeType+") needs a package size of " + packageSize + " instance(s).");
		for ( int x=0; x < packageSize; x++) {
			
			Instance instance = null;
			if ( nodeType.equals("TEAPOT") ) {
				instance = Sagitarii.getInstance().getNextInstance( macAddress );
			}
			if ( nodeType.equals("NUNKI") ) {
				instance = Sagitarii.getInstance().getNextJoinInstance( macAddress );
			}
			
			if ( instance != null ) {
				logger.debug( " > sending instance "+ instance.getSerial() + " data to node " + macAddress + " ("+nodeType+")" );
				
				instance.setStartDateTime( Calendar.getInstance().getTime() );
				instance.setStatus( InstanceStatus.WAITING );
				cluster.addInstance(instance);
				resposta = fillInstanceID ( instance );
				instance.setContent( resposta );

				byte[] respCompressed = ZipUtil.compress( resposta );
				String respHex = ZipUtil.toHexString( respCompressed );
				instancePack.add( respHex );
				
				InstanceDeliveryControl.getInstance().addUnit(instance, macAddress);
				logger.debug( " > instance "+ instance.getSerial() + " compressed and control tags replaced." );
			} 
		}
		if ( instancePack.size() > 0 ) {
			return instancePack.toString();
		} else {
			return "";
		}
	}
	
	public  String getTask(String macAddress, int packageSize, String nodeType) {
		//logger.debug("node " + macAddress + " ("+nodeType+") requesting task");
		String resposta = "";
		Cluster cluster = getCluster(macAddress);
		if ( (cluster != null)  ) {
			// if it is allowed to receive new tasks...
			if ( !cluster.signaled() ) {
				resposta = getNextInstance( cluster, packageSize, nodeType );
				//logger.debug("task package sent to node " + macAddress + " ("+nodeType+")");
			} else {
				logger.warn("node " + macAddress + " not allowed to run tasks for now");
				// if not...
				if ( !cluster.isMainCluster() ) {
					
					/*
					if ( cluster.isAskingForInstance() ) {
						resposta = "INFORM#" + cluster.getLostInstance();
					}
					*/
					
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
					// Clear signal: NExt time will receive tasks
					cluster.clearSignals();
				}
			}
			
		}
		if ( resposta.length() == 0 ) {
			//logger.warn("empty instance sent to node " + macAddress + ". System idle.");
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
	

	private NodesManager() {
		clusterList = new ArrayList<Cluster>();
	}
	
	public Cluster getCluster(String macAddress) {
		for ( Cluster clu : getClusterList()  ) {
			if ( clu.getmacAddress().equalsIgnoreCase( macAddress ) ) {
				return clu;
			}
		}
		return null;
	}


	public void updateClustersStatus() {
		for ( Cluster clu : clusterList  ) {
			clu.updateStatus();
		}
		checkLazy();
	}

    private double getMemoryPercent( long freeMemory, long totalMemory) {
    	try {
    		return Math.round( (freeMemory * 100 ) / totalMemory );    	
    	} catch ( Exception e ) {
    		return 0;
    	}
    }
    
	public Cluster addOrUpdateCluster(ClusterType type, String javaVersion, String soFamily, String macAddress, 
			String ipAddress, String machineName, Double cpuLoad, String soName, 
			int availableProcessors, int maxAllowedTasks, long freeMemory, long totalMemory) {
		Cluster retorno = null;
		
		Cluster clu = getCluster(macAddress);
		if ( clu != null ) {
			clu.setMachineName( machineName );
			clu.setIpAddress( ipAddress );
			clu.setAvailableProcessors( availableProcessors );
			clu.setSoName( soName );
			clu.setLastAnnounce( Calendar.getInstance().getTime() );
			clu.setCpuLoad( cpuLoad );
			clu.setTotalMemory(totalMemory);
			clu.setFreeMemory(freeMemory);
			clu.setMemoryPercent( getMemoryPercent( freeMemory, totalMemory ) );
			clu.setMaxAllowedTasks( maxAllowedTasks );
			clu.updateStatus();
			retorno = clu;
		} else {
			Cluster c1 = new Cluster(type, javaVersion,soFamily,macAddress,ipAddress,machineName,
					cpuLoad,soName,availableProcessors,maxAllowedTasks,freeMemory,totalMemory);
			clusterList.add( c1 );
			retorno = c1;
		}
		
		return retorno;
	}
	
}
