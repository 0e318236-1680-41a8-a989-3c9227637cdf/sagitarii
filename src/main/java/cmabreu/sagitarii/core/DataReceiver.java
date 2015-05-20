package cmabreu.sagitarii.core;

import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmabreu.sagitarii.core.sockets.FileImporter;
import cmabreu.sagitarii.core.types.ActivityStatus;
import cmabreu.sagitarii.core.types.ActivityType;
import cmabreu.sagitarii.core.types.PipelineStatus;
import cmabreu.sagitarii.persistence.entity.Activity;
import cmabreu.sagitarii.persistence.entity.Experiment;
import cmabreu.sagitarii.persistence.entity.Pipeline;
import cmabreu.sagitarii.persistence.entity.Relation;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.services.ActivityService;
import cmabreu.sagitarii.persistence.services.ExperimentService;
import cmabreu.sagitarii.persistence.services.PipelineService;
import cmabreu.sagitarii.persistence.services.RelationService;

public class DataReceiver {
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
	
	
	private Relation getRelation( String tableName ) throws Exception {
		RelationService relationService = new RelationService();
		Relation table = null;
		try {
			table = relationService.getTable( tableName );
		} catch ( NotFoundException e ) {
			throw new Exception("Table '" + tableName + "' not found in database.");
		}
		return table;
	}

	private Pipeline newInstance( Activity activity ) throws Exception {
		PipelineService ps = new PipelineService();
		Pipeline instance = null;
		instance = new Pipeline();
		instance.setExecutorAlias( activity.getSerial() );
		instance.setType( ActivityType.LOADER );
		instance.setStatus( PipelineStatus.NEW_DATA );
		instance.setStartDateTime( Calendar.getInstance().getTime() );
		instance.setFinishDateTime( Calendar.getInstance().getTime() );
		ps.insertPipeline(instance);
		logger.debug("new instance generated: " + instance.getSerial() );
		return instance;
	}
	
	private Activity newActivity( Relation table ) throws Exception {
		ActivityService as = new ActivityService();
		Activity activity = null;
		activity = new Activity();
		activity.setDescription( table.getName() );
		activity.setTag( "DATA_LOADER" );
		activity.setType( ActivityType.LOADER );
		activity.setExecutorAlias("DATA_LOADER");
		activity.setStatus( ActivityStatus.FINISHED );
		activity.setOutputRelation(table);
		
		as.newTransaction();
		as.insertActivity(activity);
		logger.debug("new activity generated: " + activity.getSerial() );
		return activity;
	}

	
	public String receive( List<String> contentLines, String tableName,	String experimentSerial) throws Exception {
		logger.debug("receiving initial load data from API call to table " + tableName + " and experiment " + experimentSerial);
		Relation table = getRelation( tableName );
		Activity activity = newActivity( table );
		Pipeline instance = newInstance( activity );
		logger.debug("passing thru original receive method...");
		return receive( contentLines, "API CALL", instance, activity, table, experimentSerial, null, true);
	}
	
	public String receive( List<String> contentLines, String macAddress, Pipeline instance, Activity activity, Relation table, 
			String experimentSerial, FileImporter importer, boolean initialLoad ) {

		try {
			logger.debug("receiving data from " + macAddress + " pipeline " + instance.getSerial() + " activity " + activity.getSerial() );
	
			ReceivedData rd = new ReceivedData(contentLines, macAddress, instance, activity, table, experimentSerial);
			ExperimentService es = new ExperimentService();
			Experiment ex = es.getExperiment( rd.getExperimentSerial() );
			
			RelationService relationService = new RelationService();
			relationService.importCSVData( rd, ex, importer );
	
			if ( !initialLoad ) {
				logger.debug("node data");
				ClustersManager.getInstance().confirmReceiveData( rd );
			}		
			
			logger.debug("done. pipeline | activity:  " + rd.getPipeline().getSerial() + "|" + rd.getActivity().getSerial() );
			return rd.getPipeline().getSerial() + ";" + rd.getActivity().getSerial();
			
		} catch ( Exception e ) {
			logger.error( e.getMessage() );
			return e.getMessage();
		}
		
	}
	
	
	

}
