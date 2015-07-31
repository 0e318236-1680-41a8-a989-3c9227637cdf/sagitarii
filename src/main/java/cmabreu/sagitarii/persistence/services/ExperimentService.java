package cmabreu.sagitarii.persistence.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmabreu.sagitarii.core.FragmentInstancer;
import cmabreu.sagitarii.core.Genesis;
import cmabreu.sagitarii.core.Sagitarii;
import cmabreu.sagitarii.core.types.ExperimentStatus;
import cmabreu.sagitarii.misc.FragmentComparator;
import cmabreu.sagitarii.persistence.entity.Activity;
import cmabreu.sagitarii.persistence.entity.Experiment;
import cmabreu.sagitarii.persistence.entity.Fragment;
import cmabreu.sagitarii.persistence.entity.Relation;
import cmabreu.sagitarii.persistence.entity.User;
import cmabreu.sagitarii.persistence.entity.Workflow;
import cmabreu.sagitarii.persistence.exceptions.DatabaseConnectException;
import cmabreu.sagitarii.persistence.exceptions.DeleteException;
import cmabreu.sagitarii.persistence.exceptions.InsertException;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.exceptions.UpdateException;
import cmabreu.sagitarii.persistence.repository.ExperimentRepository;

public class ExperimentService {
	private ExperimentRepository rep;
	private Logger logger = LogManager.getLogger( this.getClass().getName() );

	public ExperimentService() throws DatabaseConnectException {
		this.rep = new ExperimentRepository();
	}


	public Experiment cloneExperiment( int idExperiment, User loggedUser )  throws Exception {
		
		Experiment source = previewExperiment( idExperiment );
		
		if ( source.getStatus() != ExperimentStatus.STOPPED  ) {
			throw new Exception("Only STOPPED experiments can be cloned");
		}
		
		List<Activity> activities = new ArrayList<Activity>();
		
		newTransaction();
		Experiment newExperiment = generateExperiment( source, loggedUser );
		
		
		for ( Fragment frag : source.getFragments()  ) {
			activities.addAll( frag.getActivities() );
		}

		RelationService rs = new RelationService();
		for ( Activity act : activities  ) {
			if ( act.getPreviousActivities().size() == 0 ) {
				for ( Relation rel : act.getInputRelations() ) {
					String inputTable = rel.getName();
					rs.newTransaction();
					rs.copy( inputTable, source.getIdExperiment(), newExperiment.getIdExperiment() );
				}
			}
		}
		return newExperiment;
		
	}
	
	/**
	 * Gera as atividades e os fragmentos para um experimento mas não salva no banco de dados.
	 */
	public Experiment previewExperiment( int idExperiment ) throws Exception {
		Experiment exp = getExperiment(idExperiment);
		if ( exp.getStatus() == ExperimentStatus.STOPPED ) {
			return new Genesis().generate( exp );
		} else {
			return exp;
		}
		
	}

	public void close() {
		rep.closeSession();
	}

	
	/**
	 * Gera as atividades e os fragmentos para um experimento, 
	 * ordena os fragmentos, coloca o experimento com status = RUNNING 
	 * e grava tudo no banco de dados.
	 * 
	 */
	public Experiment runExperiment( int idExperiment ) throws Exception {
	
		logger.debug( "Generating Activities to run Experiment " + idExperiment );  
		Experiment experiment;
		try {
			experiment = rep.getExperiment( idExperiment );
		} catch (NotFoundException e) {
			throw new UpdateException( e.getMessage() );
		}
		
		if ( experiment.getStatus() == ExperimentStatus.FINISHED ) {
			throw new Exception("This experiment is finished.");
		}

		if ( experiment.getStatus() == ExperimentStatus.RUNNING ) {
			throw new Exception("This experiment is already running.");
		}
		
		if ( experiment.getStatus() == ExperimentStatus.STARTING ) {
			throw new Exception("This experiment is already starting. Be patient.");
		}
		
		logger.debug("setting experiment status to STARTING");
		experiment.setStatus( ExperimentStatus.STARTING );
		rep.newTransaction();
		rep.updateExperiment(experiment);

		logger.debug("Genesis is converting JSON specifications and fragmenting...");
		// Gerar atividades basado na especificação JSON
		Genesis ag = new Genesis();
		ag.generate(experiment);

		// Fragmentar baseado nos tipos de atividades e dados disponíveis.
		int acts = ag.getActivities().size();
		int frgs = experiment.getFragments().size();

		logger.debug("fragmenting is done. storing now.");
		new FragmentService().insertFragmentList( experiment.getFragments() );
		
		experiment.setStatus( ExperimentStatus.RUNNING );
		experiment.setLastExecutionDate( Calendar.getInstance().getTime() );

		// Gerar instances do primeiro fragmento que pode ser executado.
		logger.debug("creating instances");
		FragmentInstancer fp = new FragmentInstancer( experiment );
		fp.generate();
		
		int pips = fp.getInstances().size();
		
		logger.debug( acts + " activities generated." );
		logger.debug( frgs + " fragments generated." );
		logger.debug( pips + " instances generated." );

		logger.debug("saving experiment");
		rep.newTransaction();
		rep.updateExperiment(experiment);

		Sagitarii.getInstance().addRunningExperiment(experiment);
		
		logger.debug( "Experiment " + experiment.getTagExec() + " is now running with " + acts + " activities, " + frgs + " fragments and " + pips + " instances.");
		return experiment;
	}

	
	public void updateExperiment(Experiment experiment) throws UpdateException {
		Experiment oldExperiment;
		try {
			oldExperiment = rep.getExperiment( experiment.getIdExperiment() );
		} catch (NotFoundException e) {
			throw new UpdateException( e.getMessage() );
		}
		oldExperiment.setStatus( experiment.getStatus() );
		oldExperiment.setAlterationDate( Calendar.getInstance().getTime() );
		oldExperiment.setLastExecutionDate( experiment.getLastExecutionDate() );
		oldExperiment.setFinishDateTime( experiment.getFinishDateTime() );
		oldExperiment.setActivitiesSpecs( experiment.getActivitiesSpecs() );
		oldExperiment.setImagePreviewData( experiment.getImagePreviewData() );
		
		rep.newTransaction();
		rep.updateExperiment(oldExperiment);
	}	
	
	/**
	 * Apos carregar um experimento do banco de dados, completar com os fragmentos e as
	 * respectivas atividades.

	 */
	private Experiment fillExperiment( Experiment experiment ) {
		try {
			FragmentService fs = new FragmentService();
			List<Fragment> frags = fs.getList( experiment.getIdExperiment() );
			FragmentComparator fc = new FragmentComparator();
			Collections.sort( frags, fc );
			experiment.setFragments( frags );
		} catch ( Exception e ) {
			//logger.error( e.getMessage() );
		}
		return experiment;
	}
	
	
	public Experiment getExperiment(String tag) throws NotFoundException{
		return fillExperiment( rep.getExperiment(tag) );
		
	}

	public Experiment getExperiment(int idExperiment) throws NotFoundException{
		return fillExperiment( rep.getExperiment(idExperiment) );
	}
	
	public void newTransaction() {
		rep.newTransaction();
	}
	
	private Experiment insertExperiment(Experiment experiment) throws InsertException {
		Experiment expRet = rep.insertExperiment( experiment );
		return expRet ;
	}	
	
	/** 
	 * Gera um experimento baseado nos dados de um workflow.
	 */
	public Experiment generateExperiment( int idWorkflow, User owner ) throws InsertException {
		Experiment ex = new Experiment();
		try {
			Workflow workflow = new WorkflowService().getWorkflow(idWorkflow);
			ex.setWorkflow(workflow);
			ex.setActivitiesSpecs( workflow.getActivitiesSpecs() );
			ex.setImagePreviewData(workflow.getImagePreviewData() );
			ex.setOwner(owner);
			
			ex = insertExperiment(ex);
		} catch ( Exception e ) {
			throw new InsertException( e.getMessage() );
		}
		return ex;
	}

	/**
	 * Copy an Experiment
	 */
	public Experiment generateExperiment( Experiment source, User owner ) throws InsertException {
		Experiment ex = new Experiment();
		try {
			ex.setWorkflow( source.getWorkflow() );
			ex.setActivitiesSpecs( source.getActivitiesSpecs() );
			ex.setImagePreviewData( source.getImagePreviewData() );
			ex.setOwner( owner );
			ex = insertExperiment(ex);
		} catch ( Exception e ) {
			throw new InsertException( e.getMessage() );
		}
		return ex;
	}
	
	public void deleteExperiment( int idExperiment ) throws DeleteException {
		logger.debug( "deleting experiment " + idExperiment );
		
		Sagitarii.getInstance().stopProcessing();
		
		try {

			Experiment experiment = fillExperiment ( rep.getExperiment(idExperiment) );

			
			if ( experiment.getStatus() == ExperimentStatus.RUNNING ) {
				logger.error( "deletion of running experiment " + idExperiment + " not allowed." );
				throw new DeleteException("You cannot delete a running experiment.");
			}


			RelationService rs = new RelationService();
			List<Relation> tables = rs.getList();
			String sql = "";
			for ( Relation table : tables ) {
				logger.debug("removing all data from user table '" + table.getName() + "' if any exists..." );
				sql = "delete from " + table.getName() + " where id_experiment = " + idExperiment;
				rs.executeQuery( sql );
				logger.debug("done.");
			}


			logger.debug("removing custom queries...");
			sql = "delete from queries where id_experiment = " + idExperiment;
			rs.executeQuery( sql );
			logger.debug("done.");
			
			
			logger.debug("removing files...");
			sql = "delete from files where id_experiment = " + idExperiment;
			rs.executeQuery( sql );
			logger.debug("done.");
			
			logger.debug("removing fragments and activities...");
			
			for ( Fragment frag : experiment.getFragments() ) {
				
				logger.debug("removing activity dependencies...");
				sql = "delete from dependencies where id_master in ( select id_activity from activities where id_fragment = " + frag.getIdFragment() + ")";
				rs.executeQuery( sql );
				sql = "delete from dependencies where id_slave in ( select id_activity from activities where id_fragment = " + frag.getIdFragment() + ")";
				rs.executeQuery( sql );
				
				logger.debug("removing activity relationship...");
				sql = "delete from relationship where id_activity in ( select id_activity from activities where id_fragment = " + frag.getIdFragment() + ")";
				rs.executeQuery( sql );
				
				
				logger.debug("removing activities...");
				sql = "delete from activities where id_fragment = " + frag.getIdFragment() ;
				rs.executeQuery( sql );

				
				logger.debug("removing consumptions...");
				sql = "delete from consumptions where id_instance in ( select id_instance from instances where id_fragment = " + frag.getIdFragment() + ")";
				rs.executeQuery( sql );

				
				logger.debug("removing instances...");
				sql = "delete from instances where id_fragment = " + frag.getIdFragment();
				rs.executeQuery( sql );
				
				logger.debug("removing fragment " + frag.getSerial() + "...");
				sql = "delete from fragments where id_fragment = " + frag.getIdFragment() ;
				rs.executeQuery( sql );
			}
			logger.debug("done removing fragments. will delete experiment...");
			rep.newTransaction();
			rep.deleteExperiment(experiment);
			Sagitarii.getInstance().removeExperiment( experiment );
			Sagitarii.getInstance().resumeProcessing();
		} catch ( Exception e ) {
			e.printStackTrace();
			logger.error( e.getMessage() );
			Sagitarii.getInstance().resumeProcessing();
			throw new DeleteException( e.getMessage() );
		}
		logger.debug("experiment deleted.");
	}

	public List<Experiment> getRunning() throws NotFoundException {
		logger.debug("retrieve running experiments");
		List<Experiment> running = rep.getRunning();
		try {
			FragmentService fs = new FragmentService();
			for ( Experiment exp : running ) {
				exp.setFragments( fs.getList( exp.getIdExperiment() ) );
			}
		} catch (DatabaseConnectException e) {
			throw new NotFoundException( e.getMessage() );
		}
		logger.debug("done");
		return running;
	}
	
	public Set<Experiment> getList() throws NotFoundException {
		Set<Experiment> preList = rep.getList();
		return preList;	
	}

	public Set<Experiment> getList( User user ) throws NotFoundException {
		Set<Experiment> preList = rep.getList( user );
		return preList;	
	}

}
