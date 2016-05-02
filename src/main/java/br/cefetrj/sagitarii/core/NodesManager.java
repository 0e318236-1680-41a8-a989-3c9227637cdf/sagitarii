package br.cefetrj.sagitarii.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.cefetrj.sagitarii.core.config.Configurator;
import br.cefetrj.sagitarii.core.delivery.InstanceDeliveryControl;
import br.cefetrj.sagitarii.core.types.InstanceStatus;
import br.cefetrj.sagitarii.core.types.LogType;
import br.cefetrj.sagitarii.core.types.NodeStatus;
import br.cefetrj.sagitarii.core.types.NodeType;
import br.cefetrj.sagitarii.misc.PathFinder;
import br.cefetrj.sagitarii.misc.ZipUtil;
import br.cefetrj.sagitarii.misc.json.NodeTasks;
import br.cefetrj.sagitarii.persistence.entity.Instance;

import com.google.gson.Gson;

public class NodesManager {
	private List<Node> nodeList;
	private static NodesManager cm;
	private Logger logger = LogManager.getLogger( this.getClass().getName() );

	public static NodesManager getInstance() {
		if ( cm == null ) {
			cm = new NodesManager();
		}
		return cm;
	}
	
	// If a node is IDLE then ask the IDC for lost instances it may own
	private void checkLazy() {
		for ( Node node : getNodeList() ) {
			if ( node.getStatus() != NodeStatus.IDLE ) continue;
			InstanceDeliveryControl.getInstance().claimInstance( node );
		}
	}
	
	public void saveMetrics() {
		try {
			String path = PathFinder.getInstance().getPath() + "/metrics/";
			File newFile = new File(path);
			newFile.mkdirs();
			for ( Node cluster : getNodeList() ) {
				cluster.saveMetricImages( path );
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	
	public int getCores() {
		int cores = 0;
		for ( Node clu : getNodeList()  ) {
			if ( clu.getStatus() == NodeStatus.ACTIVE ) {
				cores = cores + clu.getAvailableProcessors();
			}
		}
		return cores;
	}
	
	public void clearNodeLog( String macAddress ) {
		logger.debug( "will clear log for node " + macAddress );
		Node clu = getNode(macAddress);
		if ( clu != null ) {
			clu.clearLog();
		}
	}

	public void clearNodeTasks( String macAddress ) {
		logger.debug( "will clear tasks for node " + macAddress );
		Node clu = getNode(macAddress);
		if ( clu != null ) {
			clu.clearTasks();
		}
	}

	public void acceptTask( String instanceId, String macAddress) {
		logger.debug( "node " + macAddress + " accepted task in instance " + instanceId );
		Node clu = getNode(macAddress);
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
			Node node = getNode( tasks.getNodeId() );
			
			if ( node != null ) {
				node.setLastAnnounce( Calendar.getInstance().getTime() );
				node.setCpuLoad( tasks.getCpuLoad() );
				node.setTotalMemory( tasks.getTotalMemory() );
				node.setFreeMemory( tasks.getFreeMemory() );
				node.setFreeDiskSpace( tasks.getFreeDiskSpace() );
				node.setTotalDiskSpace( tasks.getTotalDiskSpace() );
				node.setTasks( tasks.getData() );
				node.setMemoryPercent( tasks.getMemoryPercent() );
				node.setMaxAllowedTasks( tasks.getMaximunLimit() );
				node.updateStatus(  ( node.getStatus() == NodeStatus.ACTIVE ) );
			}
			
		} catch ( Exception e ) {
			logger.error( e.getMessage() );
		}
	}

	public void receiveNodeLog( String activitySerial, String message, String macAddress) {
		logger.debug( "node " + macAddress + " report (" + activitySerial + "): " + message );
		Node clu = getNode(macAddress);
		clu.setMessage( LogType.NODE, message, activitySerial );
	}

	
	public boolean hasClusters() {
		boolean hasAliveNodes = false;
		for ( Node clu : getNodeList() ) {
			if ( !clu.isDead() ) {
				hasAliveNodes = true;
			}
		}
		return hasAliveNodes;
	}
	
	public void refuseTask( String instanceId, String macAddress ) {
		logger.error( "node " + macAddress + " refused task in instance " + instanceId );
		Node clu = getNode(macAddress);
		clu.setMessage(LogType.SYSTEM, "Cannot run task in instance " + instanceId, "" );
		clu.resubmitInstanceToBuffer( instanceId );
	}

	// Given an instance, who is running it?
	public synchronized Node tryToFindInstance( String instanceSerial ) {
		for ( Node cl : getNodeList() ) {
			if ( cl.isRunning(instanceSerial) ) return cl;
		}
		return null;
	}
	
	public void confirmReceiveData( ReceivedData rd ) throws Exception {
		String instanceSerial = rd.getInstance().getSerial();
		logger.debug( "receiving instance "+ instanceSerial +" close command from node " + rd.getMacAddress() );
		
		Node cl = tryToFindInstance( instanceSerial ); 
		
		if ( cl != null ) {
			if ( !rd.getMacAddress().equals( cl.getmacAddress() ) ) {
				logger.debug("Wrong running node. Must be " + rd.getMacAddress() + " but find in " + cl.getmacAddress() );
			}
			try {
				cl.confirmReceiveData( rd );
			} catch ( Exception e ) {
				e.printStackTrace();
				logger.error("activity " + rd.getInstance().getSerial() + ": " + e.getMessage() );
				cl.setMessage( LogType.SYSTEM, e.getMessage(), rd.getInstance().getSerial() );
				throw e;
			}
		} else {
			logger.error("Cannot find who is running instance " + instanceSerial + ". Must be node " + rd.getMacAddress() );
		}
	}
	
	public void finishInstance( ReceivedData rd ) {
		Node cluster = getNode( rd.getMacAddress() );
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
		for ( Node clu : getNodeList() ) {
			clu.reloadWrappers();
		}
	}
	
	public void cleanWorkspaces() throws Exception {
		if ( Sagitarii.getInstance().getRunningExperiments().size() > 0 ) {
			throw new Exception("Cannot clean nodes workspaces when experiments are running.");
		} else { 
			for ( Node clu : getNodeList() ) {
				clu.cleanWorkspace();
			}
		}
	}
	
	public void quit(String macAddress) {
		Node node = getNode(macAddress);
		if ( node != null ) {
			node.quit();
		}
	}

	public void restart(String macAddress) {
		Node node = getNode(macAddress);
		if ( node != null ) {
			node.restart();
		}
	}

	private String fillInstanceID( Instance instance ) {
		String content = instance.getContent();
		try {
			content = instance.getContent().replace("%ID_PIP%", String.valueOf( instance.getIdInstance() ) );
		} catch ( Exception e ) {
			logger.error("Error setting Instance ID to instance content tag.");
		}
		return content.replace("##TAG_ID_INSTANCE##", String.valueOf( instance.getIdInstance() ) );
	}
	
	
	private synchronized String getNextInstance( Node cluster, int packageSize, String nodeType ) {
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
		Node node = getNode(macAddress);
		if ( (node != null)  ) {
			// if it is allowed to receive new tasks...
			if ( !node.signaled() ) {
				resposta = getNextInstance( node, packageSize, nodeType );
				//logger.debug("task package sent to node " + macAddress + " ("+nodeType+")");
			} else {
				logger.warn("node " + macAddress + " not allowed to run tasks for now");
				// if not...
				/*
				if ( cluster.isAskingForInstance() ) {
					resposta = "INFORM#" + cluster.getLostInstance();
				}
				*/
				
				if ( node.isReloadWrappersSignal() ) {
					resposta = "RELOAD_WRAPPERS";
				}
				if ( node.isQuitSignal() ) {
					resposta = "COMM_QUIT";
				}
				if ( node.isCleanWorkspaceSignal() ) {
					resposta = "COMM_CLEAN_WORKSPACE";
				}
				if ( node.isRestartSignal() ) {
					resposta = "COMM_RESTART";
				}
				// Clear signal: NExt time will receive tasks
				node.clearSignals();
			}
			
		}
		if ( resposta.length() == 0 ) {
			//logger.warn("empty instance sent to node " + macAddress + ". System idle.");
		} else {
			logger.debug("task sent to node " + macAddress );
		}
		return resposta;
	}
		
	public List<Node> getNodeList() {
		return new ArrayList<Node>( nodeList );
	}
	

	private NodesManager() {
		nodeList = new ArrayList<Node>();
	}
	
	public Node getNode(String macAddress) {
		for ( Node clu : getNodeList()  ) {
			if ( clu.getmacAddress().equalsIgnoreCase( macAddress ) ) {
				return clu;
			}
		}
		return null;
	}


	public void updateNodesStatus() {
		for ( Node clu : nodeList  ) {
			clu.updateStatus( false );
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
    
	public Node addOrUpdateNode(NodeType type, String javaVersion, String soFamily, String macAddress, 
			String ipAddress, String machineName, Double cpuLoad, String soName, 
			int availableProcessors, int maxAllowedTasks, long freeMemory, long totalMemory) {
		Node retorno = null;
		
		Node clu = getNode(macAddress);
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
			clu.updateStatus( ( clu.getStatus() == NodeStatus.IDLE ) );
			retorno = clu;
		} else {
			Node c1 = new Node(type, javaVersion,soFamily,macAddress,ipAddress,machineName,
					cpuLoad,soName,availableProcessors,maxAllowedTasks,freeMemory,totalMemory);
			nodeList.add( c1 );
			retorno = c1;
		}
		
		return retorno;
	}
	
}
