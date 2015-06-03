package cmabreu.sagitarii.persistence.repository;

import java.util.HashSet;
import java.util.Set;

import cmabreu.sagitarii.persistence.entity.Consumption;
import cmabreu.sagitarii.persistence.exceptions.DatabaseConnectException;
import cmabreu.sagitarii.persistence.exceptions.DeleteException;
import cmabreu.sagitarii.persistence.exceptions.InsertException;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.infra.DaoFactory;
import cmabreu.sagitarii.persistence.infra.IDao;

public class ConsumptionRepository extends BasicRepository {

	public ConsumptionRepository() throws DatabaseConnectException {
		super();
		logger.debug("init");
	}

	public Set<Consumption> getList(int idInstance) throws NotFoundException {
		logger.debug("get list" );
		DaoFactory<Consumption> df = new DaoFactory<Consumption>();
		IDao<Consumption> fm = df.getDao(this.session, Consumption.class);
		Set<Consumption> consumptions = null;
		try {
			consumptions = new HashSet<Consumption>( fm.getList("select * from consumptions where id_instance = " + idInstance) );
		} catch ( Exception e ) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done: " + consumptions.size() + " consumptions.");
		return consumptions;
	}

	public Consumption insertConsumption(Consumption consumption) throws InsertException {
		logger.debug("insert");
		DaoFactory<Consumption> df = new DaoFactory<Consumption>();
		IDao<Consumption> fm = df.getDao(this.session, Consumption.class);
		
		try {
			fm.insertDO(consumption);
			commit();
		} catch (InsertException e) {
			rollBack();
			closeSession();
			logger.error( e.getMessage() );
			throw e;
		}
		closeSession();
		logger.debug("done");
		return consumption;
	}
	

	public Consumption getConsumption(int idConsumption) throws NotFoundException {
		logger.debug("get " + idConsumption + "...");
		DaoFactory<Consumption> df = new DaoFactory<Consumption>();
		IDao<Consumption> fm = df.getDao(this.session, Consumption.class);
		Consumption consumption = null;
		try {
			consumption = fm.getDO(idConsumption);
		} catch ( Exception e ) {
			closeSession();		
			throw e;
		} 
		closeSession();		
		logger.debug("done" );
		return consumption;
	}
	

	public void deleteConsumption(Consumption consumption) throws DeleteException {
		logger.debug("delete" );
		DaoFactory<Consumption> df = new DaoFactory<Consumption>();
		IDao<Consumption> fm = df.getDao(this.session, Consumption.class);
		try {
			fm.deleteDO(consumption);
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
