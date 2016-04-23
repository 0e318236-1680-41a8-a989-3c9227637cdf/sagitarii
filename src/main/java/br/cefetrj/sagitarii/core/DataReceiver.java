package br.cefetrj.sagitarii.core;

import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.cefetrj.sagitarii.core.filetransfer.FileImporter;
import br.cefetrj.sagitarii.core.filetransfer.ReceivedFile;
import br.cefetrj.sagitarii.core.types.ActivityStatus;
import br.cefetrj.sagitarii.core.types.ActivityType;
import br.cefetrj.sagitarii.core.types.InstanceStatus;
import br.cefetrj.sagitarii.persistence.entity.Activity;
import br.cefetrj.sagitarii.persistence.entity.Instance;
import br.cefetrj.sagitarii.persistence.entity.Relation;
import br.cefetrj.sagitarii.persistence.exceptions.NotFoundException;
import br.cefetrj.sagitarii.persistence.services.ActivityService;
import br.cefetrj.sagitarii.persistence.services.InstanceService;
import br.cefetrj.sagitarii.persistence.services.RelationService;

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
		//instance.setExecutorAlias( activity.getSerial() );
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

		ReceivedData rd = new ReceivedData(contentLines, macAddress, instance, activity, table, csvDataFile);
		String result = "";
		try {
			logger.debug("receiving data from " + macAddress + " instance " + instance.getSerial() + " activity " + activity.getSerial() );
	
			try {
				RelationService relationService = new RelationService();
				relationService.importCSVData( rd, importer );
			} catch ( Exception e ) {
				logger.error("error importing CSV data to database: " + e.getMessage() );
			}
			
			logger.debug("done. instance | activity:  " + rd.getInstance().getSerial() + "|" + rd.getActivity().getSerial() );
			result = rd.getInstance().getSerial() + ";" + rd.getActivity().getSerial();
			
		} catch ( Exception e ) {
			logger.error( "critical error receiving data: " + e.getMessage() );
			result = e.getMessage();
		}

		if ( !initialLoad ) {
			logger.debug("closing instance " + instance.getSerial() );
			try {
				NodesManager.getInstance().confirmReceiveData( rd );
				logger.debug("instance " + instance.getSerial() + " close.");
			} catch ( Exception e ) {
				logger.debug("cannot close instance " + instance.getSerial() + ". this may cause trouble...");
			}
		}		
		
		return result;
	}
	
	
	

}
