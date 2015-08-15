package br.cefetrj.sagitarii.persistence.repository;

import java.util.HashSet;
import java.util.Set;

import br.cefetrj.sagitarii.persistence.entity.ActivationExecutor;
import br.cefetrj.sagitarii.persistence.exceptions.DatabaseConnectException;
import br.cefetrj.sagitarii.persistence.exceptions.DeleteException;
import br.cefetrj.sagitarii.persistence.exceptions.InsertException;
import br.cefetrj.sagitarii.persistence.exceptions.NotFoundException;
import br.cefetrj.sagitarii.persistence.exceptions.UpdateException;
import br.cefetrj.sagitarii.persistence.infra.DaoFactory;
import br.cefetrj.sagitarii.persistence.infra.IDao;

public class ExecutorRepository extends BasicRepository {

	public ExecutorRepository() throws DatabaseConnectException {
		super();
		logger.debug("init");
	}

	public Set<ActivationExecutor> getList() throws NotFoundException {
		logger.debug("get list" );
		DaoFactory<ActivationExecutor> df = new DaoFactory<ActivationExecutor>();
		IDao<ActivationExecutor> fm = df.getDao(this.session, ActivationExecutor.class);
		Set<ActivationExecutor> executors = null;
		try {
			executors = new HashSet<ActivationExecutor>( fm.getList("select * from executors") );
		} catch ( Exception e ) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done: " + executors.size() + " executors.");
		return executors;
	}

	public void updateActivationExecutor( ActivationExecutor executor ) throws UpdateException {
		logger.debug("update");
		DaoFactory<ActivationExecutor> df = new DaoFactory<ActivationExecutor>();
		IDao<ActivationExecutor> fm = df.getDao(this.session, ActivationExecutor.class);
		try {
			fm.updateDO(executor);
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
	
	public ActivationExecutor insereActivationExecutor(ActivationExecutor executor) throws InsertException {
		logger.debug("insert");
		DaoFactory<ActivationExecutor> df = new DaoFactory<ActivationExecutor>();
		IDao<ActivationExecutor> fm = df.getDao(this.session, ActivationExecutor.class);
		
		try {
			fm.insertDO(executor);
			commit();
		} catch (InsertException e) {
			rollBack();
			closeSession();
			logger.error( e.getMessage() );
			throw e;
		}
		closeSession();
		logger.debug("done");
		return executor;
	}
	

	public ActivationExecutor getActivationExecutor(String executorAlias) throws NotFoundException {
		logger.debug("retrieving executor by alias " + executorAlias + "..." );
		DaoFactory<ActivationExecutor> df = new DaoFactory<ActivationExecutor>();
		IDao<ActivationExecutor> fm = df.getDao(this.session, ActivationExecutor.class);
		ActivationExecutor executor = null;
		try {
			executor = fm.getList("select * from executors where executoralias = '" + executorAlias + "'").get(0);
		} catch ( Exception e ) {
			closeSession();		
			throw new NotFoundException("Cannot find executor "+executorAlias+". Please check its name.");
		} 
		logger.debug("done");
		closeSession();
		return executor;
	}

	
	public ActivationExecutor getActivationExecutor(int idActivationExecutor) throws NotFoundException {
		logger.debug("get " + idActivationExecutor + "...");
		DaoFactory<ActivationExecutor> df = new DaoFactory<ActivationExecutor>();
		IDao<ActivationExecutor> fm = df.getDao(this.session, ActivationExecutor.class);
		ActivationExecutor executor = null;
		try {
			executor = fm.getDO(idActivationExecutor);
		} catch ( Exception e ) {
			closeSession();		
			throw e;
		} 
		closeSession();		
		logger.debug("done: " + executor.getExecutorAlias() );
		return executor;
	}
	

	public void excluiActivationExecutor(ActivationExecutor executor) throws DeleteException {
		logger.debug("delete" );
		DaoFactory<ActivationExecutor> df = new DaoFactory<ActivationExecutor>();
		IDao<ActivationExecutor> fm = df.getDao(this.session, ActivationExecutor.class);
		try {
			fm.deleteDO(executor);
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
