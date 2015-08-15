package br.cefetrj.sagitarii.persistence.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.cefetrj.sagitarii.persistence.entity.CustomQuery;
import br.cefetrj.sagitarii.persistence.entity.Experiment;
import br.cefetrj.sagitarii.persistence.exceptions.DatabaseConnectException;
import br.cefetrj.sagitarii.persistence.exceptions.DeleteException;
import br.cefetrj.sagitarii.persistence.exceptions.InsertException;
import br.cefetrj.sagitarii.persistence.exceptions.NotFoundException;
import br.cefetrj.sagitarii.persistence.exceptions.UpdateException;
import br.cefetrj.sagitarii.persistence.infra.DaoFactory;
import br.cefetrj.sagitarii.persistence.infra.IDao;

public class CustomQueryRepository extends BasicRepository {

	public CustomQueryRepository() throws DatabaseConnectException {
		super();
		logger.debug("init");
	}

	public Set<CustomQuery> getList( int idExperiment ) throws NotFoundException {
		logger.debug("get list" );
		DaoFactory<CustomQuery> df = new DaoFactory<CustomQuery>();
		IDao<CustomQuery> fm = df.getDao(this.session, CustomQuery.class);
		Set<CustomQuery> customQuerys = null;
		try {
			customQuerys = new HashSet<CustomQuery>( fm.getList("select * from queries where id_experiment = " + idExperiment ) );
		} catch ( Exception e ) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done: " + customQuerys.size() + " queries.");
		return customQuerys;
	}

	
	public CustomQuery getCustomQueryByName( String customQueryName ) throws NotFoundException {
		logger.debug("get customQuery by name " + customQueryName );
		DaoFactory<CustomQuery> df = new DaoFactory<CustomQuery>();
		IDao<CustomQuery> fm = df.getDao(this.session, CustomQuery.class);
		List<CustomQuery> customQuerys = null;
		try {
			customQuerys = fm.getList("select * from queries where name = '" + customQueryName + "'" );
		} catch ( Exception e ) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done");
		return customQuerys.get(0);
	}

	
	public void updateCustomQuery( CustomQuery customQuery ) throws UpdateException {
		logger.debug("update");
		DaoFactory<CustomQuery> df = new DaoFactory<CustomQuery>();
		IDao<CustomQuery> fm = df.getDao(this.session, CustomQuery.class);
		try {
			fm.updateDO(customQuery);
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
	
	public CustomQuery insertCustomQuery(CustomQuery customQuery, int idExperiment) throws InsertException {
		logger.debug("insert");
		DaoFactory<CustomQuery> df = new DaoFactory<CustomQuery>();
		IDao<CustomQuery> fm = df.getDao(this.session, CustomQuery.class);

		try {
			DaoFactory<Experiment> ef = new DaoFactory<Experiment>();
			IDao<Experiment> em = ef.getDao(this.session, Experiment.class);
			Experiment experiment = em.getDO( idExperiment );
			customQuery.setExperiment(experiment);
		} catch ( Exception e ) {
			closeSession();
			logger.error( e.getMessage() );
			throw new InsertException( e.getMessage() );
		}
		
		try {
			fm.insertDO(customQuery);
			commit();
		} catch (InsertException e) {
			rollBack();
			closeSession();
			logger.error( e.getMessage() );
			throw e;
		}
		closeSession();
		logger.debug("done");
		return customQuery;
	}
	

	public CustomQuery getCustomQuery(int idCustomQuery) throws NotFoundException {
		logger.debug("get " + idCustomQuery + "...");
		DaoFactory<CustomQuery> df = new DaoFactory<CustomQuery>();
		IDao<CustomQuery> fm = df.getDao(this.session, CustomQuery.class);
		CustomQuery customQuery = null;
		try {
			customQuery = fm.getDO(idCustomQuery);
		} catch ( Exception e ) {
			closeSession();		
			throw e;
		} 
		closeSession();		
		logger.debug("done: " + customQuery.getName() );
		return customQuery;
	}
	

	public void deleteCustomQuery(CustomQuery customQuery) throws DeleteException {
		logger.debug("delete" );
		DaoFactory<CustomQuery> df = new DaoFactory<CustomQuery>();
		IDao<CustomQuery> fm = df.getDao(this.session, CustomQuery.class);
		try {
			fm.deleteDO(customQuery);
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
