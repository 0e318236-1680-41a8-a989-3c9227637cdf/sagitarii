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

	public MainCluster( int maxAllowedTasks, String macAddress ) {
		this.maxAllowedTasks = maxAllowedTasks;
		this.parser = new XMLParser();
		this.macAddress = macAddress;
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

    	try {

    		Cluster cluster = ClustersManager.getInstance()
    				.addOrUpdateCluster(ClusterType.MAIN, "0.0", "Local System", macAddress, 
    						"Local Machine", "Sagitarii Server", getProcessCpuLoad(), 
    						"Main Cluster", 8, maxAllowedTasks, 
    						freeMemory, totalMemory );
    		
    		if ( cluster != null ) {
				cluster.setAsMainCluster();
				
				logger.debug("get new Instance from buffer");
				
				Instance pipe = Sagitarii.getInstance().getNextJoinInstance( macAddress );
				if ( pipe != null ) {

					InstanceDeliveryControl.getInstance().addUnit(pipe, macAddress);
					
					String content = pipe.getContent().replace("##TAG_ID_INSTANCE##", String.valueOf( pipe.getIdInstance() ) );
					content = content.replace("%ID_PIP%", String.valueOf( pipe.getIdInstance() ) ); 
					pipe.setContent( content );
					
			    	currentTaskCount++;
					logger.debug("will process instance " + pipe.getSerial() );
					cluster.addInstance(pipe);
					List<Activation> acts = parser.parseActivations( pipe.getContent() );
					if ( acts.size() > 0 ) {
						logger.debug("instance " + pipe.getSerial() + " contains " + acts.size() + " activities.");
						Activation act = acts.get(0);
						ClustersManager.getInstance().acceptTask( pipe.getSerial(), macAddress);
						
						cluster.setMessage(LogType.MAIN_CLUSTER, "running query for executor " + act.getExecutor(), act.getActivitySerial() );

						List<String> console = new ArrayList<String>();
						List<String> execLog = new ArrayList<String>();
						console.add( act.getCommand() );

						// Run query
						MainClusterQueryWrapper mcqw = new MainClusterQueryWrapper();
						try {
							mcqw.executeQuery( act );
							execLog.add("SQL executed.");
							cluster.setMessage( LogType.MAIN_CLUSTER, "done query for executor " + act.getExecutor(), act.getActivitySerial() );
							logger.debug("done query for executor " + act.getExecutor());
						} catch ( Exception e ) {
							cluster.setMessage( LogType.MAIN_CLUSTER,"error " + e.getMessage() +  " when running query for executor " + act.getExecutor(), act.getActivitySerial() );
							String errorString = "cannot finish instance " + pipe.getSerial() + ". Activity " + act.getActivitySerial() + " not found.";
							logger.error( errorString );
							for ( StackTraceElement ste : e.getStackTrace() ) {
								execLog.add( ste.getClassName() );
							}
						}
						
						// Done. Finish it !
						String activityName = act.getActivitySerial();
						ActivityService as = new ActivityService();
						try {
							Activity activity = as.getActivity( act.getActivitySerial() );
							activityName = activity.getTag();
							cluster.setInstanceAsDone( pipe.getSerial(), activity, String.valueOf( mcqw.getStartTime() ),
									String.valueOf( mcqw.getFinishTime() ) );
							execLog.add("Instance finished.");
						} catch ( NotFoundException nf ) {
							String errorString = "cannot finish instance " + pipe.getSerial() + ". Activity " + act.getActivitySerial() + " not found.";
							execLog.add( errorString );
							logger.error( errorString );
							cluster.setMessage(LogType.MAIN_CLUSTER, errorString, act.getActivitySerial() );
						}
						
						InstanceDeliveryControl.getInstance().addUnit(pipe, macAddress);
						
						MainLog.getInstance().storeLog( activityName , act.getExperiment(), 
								act.getActivitySerial(), act.getExecutor(), "0", macAddress, console, execLog);
						
					} else {
						logger.error("empty Instance " + pipe.getSerial() );
					}
					logger.debug("instance " + pipe.getSerial() + " processed.");
				} else {
					logger.debug("SELECT Instances not found in output buffer");
				}
			} else {
				logger.debug("Main Cluster not found");
			}

		} catch ( Exception e ) {
			 logger.error( "critical error running Main Cluster: " + e.getMessage() );
		}
		
	}
	
	

}
