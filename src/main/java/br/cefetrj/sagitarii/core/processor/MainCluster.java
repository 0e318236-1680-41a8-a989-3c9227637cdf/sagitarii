package br.cefetrj.sagitarii.core.processor;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.cefetrj.sagitarii.core.Cluster;
import br.cefetrj.sagitarii.core.ClustersManager;
import br.cefetrj.sagitarii.core.MainLog;
import br.cefetrj.sagitarii.core.Sagitarii;
import br.cefetrj.sagitarii.core.config.Configurator;
import br.cefetrj.sagitarii.core.delivery.InstanceDeliveryControl;
import br.cefetrj.sagitarii.core.types.ClusterType;
import br.cefetrj.sagitarii.core.types.LogType;
import br.cefetrj.sagitarii.persistence.entity.Activity;
import br.cefetrj.sagitarii.persistence.entity.Instance;
import br.cefetrj.sagitarii.persistence.exceptions.NotFoundException;
import br.cefetrj.sagitarii.persistence.services.ActivityService;

public class MainCluster implements Runnable {
	private	Logger logger = LogManager.getLogger( this.getClass().getName() );
	private int maxAllowedTasks = 4;
	private int currentTaskCount = 0;
	private XMLParser parser;
	private String macAddress;
	private	List<String> console;
	private	List<String> execLog;

	public MainCluster( int maxAllowedTasks, String macAddress ) {
		this.maxAllowedTasks = maxAllowedTasks;
		this.parser = new XMLParser();
		this.macAddress = macAddress;
		this.console = new ArrayList<String>();
		this.execLog = new ArrayList<String>();
	}
	
	
    private double getProcessCpuLoad() {
    	double result = 0;
    	try {
    		result = Configurator.getInstance().getProcessCpuLoad();
    	} catch ( Exception e ) {
    		
    	}
    	return result;
    }   	
    

	@Override
	public void run() {
		long freeMemory = 0;
		long totalMemory = 0;
		try {
			freeMemory = Configurator.getInstance().getFreeMemory();
			totalMemory = Configurator.getInstance().getTotalMemory();
		} catch ( Exception e ) {  }
			
		Cluster cluster = ClustersManager.getInstance()
				.addOrUpdateCluster(ClusterType.MAIN, "0.0", "Local System", macAddress, 
						"Local Machine", "Sagitarii Server", getProcessCpuLoad(), 
						"Main Cluster", 8, maxAllowedTasks, 
						freeMemory, totalMemory, Math.round( (freeMemory * 100 ) / totalMemory ) );
    	
    	try {
			if ( cluster != null ) {
				cluster.setAsMainCluster();
				if ( currentTaskCount < maxAllowedTasks ) {
					Instance pipe = Sagitarii.getInstance().getNextJoinInstance();
					if ( pipe != null ) {

						String content = pipe.getContent().replace("##TAG_ID_INSTANCE##", String.valueOf( pipe.getIdInstance() ) );
						content = content.replace("%ID_PIP%", String.valueOf( pipe.getIdInstance() ) ); 
						pipe.setContent( content );
						
				    	currentTaskCount++;
						logger.debug("will process instance " + pipe.getSerial() );
						cluster.addInstance(pipe);
						List<Activation> acts = parser.parseActivations( pipe.getContent() );
						if ( acts.size() > 0 ) {
							Activation act = acts.get(0);
							ClustersManager.getInstance().acceptTask( pipe.getSerial(), macAddress);
							
							cluster.setMessage(LogType.MAIN_CLUSTER, "running query for executor " + act.getExecutor() );

							MainClusterQueryWrapper mcqw = new MainClusterQueryWrapper();
							try {
								mcqw.executeQuery( act );
							} catch ( Exception e ) {
								cluster.setMessage( LogType.MAIN_CLUSTER,"error " + e.getMessage() +  " when running query for executor " + act.getExecutor() );
							}

							cluster.setMessage( LogType.MAIN_CLUSTER, "done query for executor " + act.getExecutor() );
							
							console.clear();
							console.add( act.getCommand() );
							
							execLog.clear();
							
							String activityName = act.getActivitySerial();
							
							ActivityService as = new ActivityService();
							try {
								Activity activity = as.getActivity( act.getActivitySerial() );
								activityName = activity.getTag();
								cluster.setMessage( LogType.MAIN_CLUSTER,"finishing activity " + activity.getTag() + " (" + act.getExecutor() + ")" );
								cluster.setInstanceAsDone( pipe.getSerial(), activity, String.valueOf( mcqw.getStartTime() ),
										String.valueOf( mcqw.getFinishTime() ) );
							} catch ( NotFoundException nf ) {
								String errorString = "cannot finish instance " + pipe.getSerial() + ". Activity " + act.getActivitySerial() + " not found.";
								console.add( errorString );
								logger.error( errorString );
								cluster.setMessage(LogType.MAIN_CLUSTER, errorString );
							}
							
							InstanceDeliveryControl.getInstance().addUnit(pipe, macAddress);
							
							MainLog.getInstance().storeLog( activityName , act.getExperiment(), 
									act.getActivitySerial(), act.getExecutor(), "0", macAddress, console, execLog);
							
						}

					}
				}
			}

		} catch ( Exception e ) {
			cluster.setMessage(LogType.MAIN_CLUSTER, "critical error running Main Cluster: " + e.getMessage() );
		}
		
	}
	
	

}
