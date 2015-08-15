package br.cefetrj.sagitarii.persistence.repository;

import java.util.List;

import br.cefetrj.sagitarii.persistence.entity.Workflow;
import br.cefetrj.sagitarii.persistence.exceptions.DatabaseConnectException;
import br.cefetrj.sagitarii.persistence.exceptions.DeleteException;
import br.cefetrj.sagitarii.persistence.exceptions.InsertException;
import br.cefetrj.sagitarii.persistence.exceptions.NotFoundException;
import br.cefetrj.sagitarii.persistence.exceptions.UpdateException;
import br.cefetrj.sagitarii.persistence.infra.DaoFactory;
import br.cefetrj.sagitarii.persistence.infra.IDao;

public class WorkflowRepository extends BasicRepository {

	public WorkflowRepository() throws DatabaseConnectException {
		super();
		logger.debug("Repository init");
	}

	public List<Workflow> getList() throws NotFoundException {
		logger.debug("Recuperando lista de workflows..." );
		DaoFactory<Workflow> df = new DaoFactory<Workflow>();
		IDao<Workflow> fm = df.getDao(this.session, Workflow.class);
		List<Workflow> workflows = null;
		try {
			workflows = fm.getList("select * from workflows");
		} catch ( Exception e ) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("Concluido: " + workflows.size() + " workflows.");
		return workflows;
	}


	public Workflow getPendent() throws NotFoundException {
		logger.debug("Recuperando workflow pendente..." );
		DaoFactory<Workflow> df = new DaoFactory<Workflow>();
		IDao<Workflow> fm = df.getDao(this.session, Workflow.class);
		
		Workflow workflow = null;
		try {
			workflow = fm.getList("select * from workflows where status = 'RUNNING'").get(0);
		} catch ( Exception e ) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done: " + workflow.getTag() );
		return workflow;
	}
	
	
	public void updateWorkflow( Workflow workflow ) throws UpdateException {
		logger.debug("update");
		DaoFactory<Workflow> df = new DaoFactory<Workflow>();
		IDao<Workflow> fm = df.getDao(this.session, Workflow.class);
		try {
			fm.updateDO(workflow);
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
	
	/**
	 * Insere um Workflow no banco de dados
	 * 
	 */
	public Workflow insertWorkflow(Workflow workflow) throws InsertException {
		logger.debug("insert workflow...");
		DaoFactory<Workflow> df = new DaoFactory<Workflow>();
		IDao<Workflow> fm = df.getDao(this.session, Workflow.class);
		try {
			fm.insertDO(workflow);
			commit();
		} catch (InsertException e) {
			logger.error( e.getMessage() );
			rollBack();
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done.");
		return workflow;
	}
	
	
	public Workflow getWorkflow(String tag) throws NotFoundException {
		logger.debug("retrieve workflow by tag " + tag + "..." );
		DaoFactory<Workflow> df = new DaoFactory<Workflow>();
		IDao<Workflow> fm = df.getDao(this.session, Workflow.class);
		Workflow workflow = null;
		try {
			workflow = fm.getList("select * from workflows where tag = '" + tag + "'").get(0);
		} catch ( Exception e ) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done.");
		return workflow;
	}
	
	
	public Workflow getWorkflow(int idWorkflow) throws NotFoundException {
		logger.debug("retrieve Workflow " + idWorkflow + "...");
		DaoFactory<Workflow> df = new DaoFactory<Workflow>();
		IDao<Workflow> fm = df.getDao(this.session, Workflow.class);
		Workflow workflow = null;
		try {
			workflow = fm.getDO(idWorkflow);
		} catch ( Exception e ) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done: " + workflow.getTag() );
		return workflow;
	}
	

	public void deleteWorkflow(Workflow workflow) throws DeleteException {
		logger.debug("delete workflow." );
		DaoFactory<Workflow> df = new DaoFactory<Workflow>();
		IDao<Workflow> fm = df.getDao(this.session, Workflow.class);
		try {
			fm.deleteDO(workflow);
			commit();
		} catch (DeleteException e) {
			rollBack();
			closeSession();
			throw e;			
		}
		logger.debug("done.");
		closeSession();
	}	
	
}
