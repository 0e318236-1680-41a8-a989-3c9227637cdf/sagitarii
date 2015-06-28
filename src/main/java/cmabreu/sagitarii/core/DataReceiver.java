package cmabreu.sagitarii.core;

import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmabreu.sagitarii.core.filetransfer.FileImporter;
import cmabreu.sagitarii.core.filetransfer.ReceivedFile;
import cmabreu.sagitarii.core.types.ActivityStatus;
import cmabreu.sagitarii.core.types.ActivityType;
import cmabreu.sagitarii.core.types.InstanceStatus;
import cmabreu.sagitarii.persistence.entity.Activity;
import cmabreu.sagitarii.persistence.entity.Instance;
import cmabreu.sagitarii.persistence.entity.Relation;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.services.ActivityService;
import cmabreu.sagitarii.persistence.services.InstanceService;
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

	private Instance newInstance( Activity activity ) throws Exception {
		InstanceService ps = new InstanceService();
		Instance instance = null;
		instance = new Instance();
		instance.setExecutorAlias( activity.getSerial() );
		instance.setType( ActivityType.LOADER );
		instance.setStatus( InstanceStatus.NEW_DATA );
		instance.setStartDateTime( Calendar.getInstance().getTime() );
		instance.setFinishDateTime( Calendar.getInstance().getTime() );
		ps.insertInstance(instance);
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
		Instance instance = newInstance( activity );
		logger.debug("passing thru original receive method...");
		
		ReceivedFile csvDataFile = new ReceivedFile();
		csvDataFile.setExperimentSerial( experimentSerial );
		
		return receive( contentLines, "API CALL", instance, activity, table, csvDataFile, null, true);
	}
	
	public String receive( List<String> contentLines, String macAddress, Instance instance, Activity activity, Relation table, 
			ReceivedFile csvDataFile, FileImporter importer, boolean initialLoad ) {

		try {
			logger.debug("receiving data from " + macAddress + " instance " + instance.getSerial() + " activity " + activity.getSerial() );
	
			ReceivedData rd = new ReceivedData(contentLines, macAddress, instance, activity, table, csvDataFile);
			
			RelationService relationService = new RelationService();
			relationService.importCSVData( rd, importer );
	
			if ( !initialLoad ) {
				logger.debug("node data");
				ClustersManager.getInstance().confirmReceiveData( rd );
			}		
			
			logger.debug("done. instance | activity:  " + rd.getInstance().getSerial() + "|" + rd.getActivity().getSerial() );
			return rd.getInstance().getSerial() + ";" + rd.getActivity().getSerial();
			
		} catch ( Exception e ) {
			logger.error( e.getMessage() );
			return e.getMessage();
		}
		
	}
	
	
	

}
