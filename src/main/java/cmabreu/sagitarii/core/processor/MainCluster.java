package cmabreu.sagitarii.core.processor;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmabreu.sagitarii.core.Cluster;
import cmabreu.sagitarii.core.ClustersManager;
import cmabreu.sagitarii.core.Sagitarii;
import cmabreu.sagitarii.persistence.entity.Activity;
import cmabreu.sagitarii.persistence.entity.Pipeline;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.services.ActivityService;

/**
 * O Pseudo Cluster executará tarefas locais, como acesso a dados.
 * Atividades do tipo QUERY.
 * 
 * @author Carlos Magno Abreu
 *
 */
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
	
	
	@Override
	public void run() {
		Cluster cluster = ClustersManager.getInstance().addOrUpdateCluster("0.0", "Local System", macAddress, "Local Machine", "Sagitarii Server", 0.0, "Main Cluster", 8, maxAllowedTasks );
		try {
			if ( cluster != null ) {
				cluster.setAsMainCluster();
				if ( currentTaskCount < maxAllowedTasks ) {
					Pipeline pipe = Sagitarii.getInstance().getNextJoinPipeline();
					if ( pipe != null ) {

						String content = pipe.getContent().replace("##TAG_ID_PIPELINE##", String.valueOf( pipe.getIdPipeline() ) );
						content = content.replace("%ID_PIP%", String.valueOf( pipe.getIdPipeline() ) ); 
						pipe.setContent( content );
						
						currentTaskCount++;
						logger.debug("will process pipeline " + pipe.getSerial() );
						cluster.addPipeline(pipe);
						List<Activation> acts = parser.parseActivations( pipe.getContent() );
						if ( acts.size() > 0 ) {
							Activation act = acts.get(0);
							ClustersManager.getInstance().acceptTask( pipe.getSerial(), macAddress);
							
							MainClusterQueryWrapper mcqw = new MainClusterQueryWrapper();
							mcqw.executeQuery( act );
							
							ActivityService as = new ActivityService();
							try {
								Activity activity = as.getActivity( act.getActivitySerial() );
								cluster.setPipelineAsDone( pipe.getSerial(), activity );
							} catch ( NotFoundException nf ) {	
								logger.error("cannot finish pipeline " + pipe.getSerial() + ". Activity " + act.getActivitySerial() + " not found." );
							}
							
						}

					}
				}
			}

		} catch ( Exception e ) {
			cluster.setLastError( e.getMessage() );
			e.printStackTrace();
		}
		
	}
	
	

}
