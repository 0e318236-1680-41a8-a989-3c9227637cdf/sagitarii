package cmabreu.sagitarii.persistence.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cmabreu.sagitarii.persistence.entity.Experiment;
import cmabreu.sagitarii.persistence.entity.User;
import cmabreu.sagitarii.persistence.exceptions.DatabaseConnectException;
import cmabreu.sagitarii.persistence.exceptions.DeleteException;
import cmabreu.sagitarii.persistence.exceptions.InsertException;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.exceptions.UpdateException;
import cmabreu.sagitarii.persistence.infra.DaoFactory;
import cmabreu.sagitarii.persistence.infra.IDao;

public class ExperimentRepository extends BasicRepository {

	public ExperimentRepository() throws DatabaseConnectException {
		super();
		logger.debug("init");
	}


	public Set<Experiment> getList() throws NotFoundException {
		return getList( null );
	}
	
	public Set<Experiment> getList( User user ) throws NotFoundException {
		logger.debug("get list" );
		DaoFactory<Experiment> df = new DaoFactory<Experiment>();
		IDao<Experiment> fm = df.getDao(this.session, Experiment.class);
		Set<Experiment> experiments = null;
		try {
			if ( user == null ) {
				experiments = new HashSet<Experiment>( fm.getList("select * from experiments") );
			} else {
				experiments = new HashSet<Experiment>( fm.getList("select * from experiments where id_user = " + user.getIdUser() ) );
			}
		} catch ( Exception e ) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done: " + experiments.size() + " experiments.");
		return experiments;
	}


	public List<Experiment> getRunning() throws NotFoundException {
		logger.debug("retrieve pendent" );
		DaoFactory<Experiment> df = new DaoFactory<Experiment>();
		IDao<Experiment> fm = df.getDao(this.session, Experiment.class);
		List<Experiment> running = null;
		try {
			running = fm.getList("select * from experiments where status = 'RUNNING' or status = 'PAUSED' ");
		} catch ( Exception e ) {
			closeSession();		
			throw e;
		} 
		closeSession();		
		logger.debug("done");
		return running;
	}
	
	
	public void updateExperiment( Experiment experiment ) throws UpdateException {
		logger.debug("update");
		DaoFactory<Experiment> df = new DaoFactory<Experiment>();
		IDao<Experiment> fm = df.getDao(this.session, Experiment.class);
		try {
			fm.updateDO(experiment);
			commit();
		} catch (UpdateException e) {
			logger.error( e.getMessage() );
			rollBack();
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done");
	}
	
	public Experiment insertExperiment(Experiment experiment) throws InsertException {
		logger.debug("insert");
		DaoFactory<Experiment> df = new DaoFactory<Experiment>();
		IDao<Experiment> fm = df.getDao(this.session, Experiment.class);
		
		try {
			fm.insertDO(experiment);
			commit();
		} catch (InsertException e) {
			rollBack();
			closeSession();
			logger.error( e.getMessage() );
			throw e;
		}
		closeSession();
		logger.debug("done");
		return experiment;
	}
	
	
	public Experiment getExperiment(String tag) throws NotFoundException {
		logger.debug("retrieving experiment by TAG " + tag + "..." );
		DaoFactory<Experiment> df = new DaoFactory<Experiment>();
		IDao<Experiment> fm = df.getDao(this.session, Experiment.class);
		Experiment experiment = null;
		try {
			experiment = fm.getList("select * from experiments where tagExec = '" + tag + "'").get(0);
		} catch ( Exception e ) {
			closeSession();		
			throw e;
		} 
		logger.debug("done");
		closeSession();
		return experiment;
	}
	
	
	public Experiment getExperiment(int idExperiment) throws NotFoundException {
		logger.debug("retrieving experiment " + idExperiment + "...");
		DaoFactory<Experiment> df = new DaoFactory<Experiment>();
		IDao<Experiment> fm = df.getDao(this.session, Experiment.class);
		Experiment experiment = null;
		try {
			experiment = fm.getDO(idExperiment);
		} catch ( Exception e ) {
			closeSession();		
			throw e;
		} 
		closeSession();		
		logger.debug("done: " + experiment.getTagExec() );
		return experiment;
	}
	

	public void deleteExperiment(Experiment experiment) throws DeleteException {
		logger.debug("delete" );
		DaoFactory<Experiment> df = new DaoFactory<Experiment>();
		IDao<Experiment> fm = df.getDao(this.session, Experiment.class);
		try {
			fm.deleteDO(experiment);
			commit();
		} catch (DeleteException e) {
			rollBack();
			closeSession();
			logger.error( e.getMessage() );
			throw e;			
		}
		logger.debug("done");
		closeSession();
	}	
	
}
