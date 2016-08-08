package br.cefetrj.sagitarii.persistence.services;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.cefetrj.sagitarii.core.Genesis;
import br.cefetrj.sagitarii.persistence.entity.Activity;
import br.cefetrj.sagitarii.persistence.entity.Fragment;
import br.cefetrj.sagitarii.persistence.exceptions.DatabaseConnectException;
import br.cefetrj.sagitarii.persistence.exceptions.DeleteException;
import br.cefetrj.sagitarii.persistence.exceptions.InsertException;
import br.cefetrj.sagitarii.persistence.exceptions.NotFoundException;
import br.cefetrj.sagitarii.persistence.exceptions.UpdateException;
import br.cefetrj.sagitarii.persistence.repository.ActivityRepository;

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

	public Activity getActivityByTag(String tag) throws NotFoundException{
		Activity activity = rep.getActivityByTag( tag );
		return activity;
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
