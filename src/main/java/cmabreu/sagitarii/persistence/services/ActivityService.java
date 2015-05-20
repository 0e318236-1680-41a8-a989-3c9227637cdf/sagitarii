package cmabreu.sagitarii.persistence.services;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmabreu.sagitarii.core.Genesis;
import cmabreu.sagitarii.persistence.entity.Activity;
import cmabreu.sagitarii.persistence.entity.Fragment;
import cmabreu.sagitarii.persistence.exceptions.DatabaseConnectException;
import cmabreu.sagitarii.persistence.exceptions.DeleteException;
import cmabreu.sagitarii.persistence.exceptions.InsertException;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.exceptions.UpdateException;
import cmabreu.sagitarii.persistence.repository.ActivityRepository;

public class ActivityService { 
	private ActivityRepository rep;
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
	
	public List<Fragment> fillFragments( List<Fragment> fragments ) throws NotFoundException {
		return rep.fillFragments( fragments );
	}
	
	public ActivityService() throws DatabaseConnectException {
		this.rep = new ActivityRepository();
	}
	
	public void newTransaction() {
		rep.newTransaction();
	}
	
	public int insertActivity(Activity activity) throws InsertException {
		logger.debug("inserting Activity: " + activity.getTag() );
		rep.insertActivity( activity );
		return 0;
	}
	
	public Activity getActivity(String serial) throws NotFoundException{
		Activity activity = rep.getActivity( serial );
		return activity;
	}

	
	public Activity getActivity(int idActivity) throws NotFoundException{
		Activity activity = rep.getActivity(idActivity);
		return activity;
	}
	
	public void deleteActivity( int idActivity ) throws DeleteException {
		logger.debug( "delete Activity " + idActivity );  
		try {
			Activity activity = rep.getActivity(idActivity);
			rep.newTransaction();
			rep.deleteActivity(activity);
		} catch (NotFoundException e) {
			logger.error( e.getMessage() );  
			throw new DeleteException( e.getMessage() );
		}
		logger.debug( "done." );  
	}

	
	/**
	 * Atualiza uma atividade
	 * 
	 * @param activity a atividade a ser atualizada
	 * @throws UpdateException
	 */
	public void updateActivity(Activity activity) throws UpdateException {
		Activity oldActivityDB;
		try {
			logger.debug( "retrieving Activity " + activity.getIdActivity() );  
			oldActivityDB = rep.getActivity( activity.getIdActivity() );
		} catch (NotFoundException e) {
			logger.debug( e.getMessage() );
			throw new UpdateException( e.getMessage() );
		}
		oldActivityDB.setInputRelations( activity.getInputRelations() );
		oldActivityDB.setOutputRelation( activity.getOutputRelation() );
		oldActivityDB.setDescription( activity.getDescription() );
		oldActivityDB.setType( activity.getType() );
		oldActivityDB.setStatus( activity.getStatus() );

		/* SALVAR AS ATIVDADES DEPENDENTES */
		
		logger.debug( "updating activity " + activity.getIdActivity() );  
		rep.newTransaction();
		rep.updateActivity(oldActivityDB);
		logger.debug( "done." );  
	}	
	
	
	public void insertActivityList( Genesis newList ) throws InsertException {
		insertActivity( newList.getRoot() );
	}
	
}
