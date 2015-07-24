package cmabreu.sagitarii.persistence.repository;

import java.util.HashSet;
import java.util.Set;

import cmabreu.sagitarii.persistence.entity.TimeControl;
import cmabreu.sagitarii.persistence.exceptions.DatabaseConnectException;
import cmabreu.sagitarii.persistence.exceptions.DeleteException;
import cmabreu.sagitarii.persistence.exceptions.InsertException;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.exceptions.UpdateException;
import cmabreu.sagitarii.persistence.infra.DaoFactory;
import cmabreu.sagitarii.persistence.infra.IDao;

public class TimeControlRepository extends BasicRepository {

	public TimeControlRepository() throws DatabaseConnectException {
		super();
		logger.debug("init");
	}

	public Set<TimeControl> getList() throws NotFoundException {
		logger.debug("get list" );
		DaoFactory<TimeControl> df = new DaoFactory<TimeControl>();
		IDao<TimeControl> fm = df.getDao(this.session, TimeControl.class);
		Set<TimeControl> tControls = null;
		try {
			tControls = new HashSet<TimeControl>( fm.getList("select * from timecontrol ") );
		} catch ( Exception e ) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done: " + tControls.size() + " items.");
		return tControls;
	}


	public void updateTimeControl( TimeControl tControl ) throws UpdateException {
		logger.debug("update");
		DaoFactory<TimeControl> df = new DaoFactory<TimeControl>();
		IDao<TimeControl> fm = df.getDao(this.session, TimeControl.class);
		try {
			fm.updateDO(tControl);
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
	
	public TimeControl insertTimeControl(TimeControl tControl) throws InsertException {
		logger.debug("insert");
		DaoFactory<TimeControl> df = new DaoFactory<TimeControl>();
		IDao<TimeControl> fm = df.getDao(this.session, TimeControl.class);
		
		try {
			fm.insertDO(tControl);
			commit();
		} catch (InsertException e) {
			rollBack();
			closeSession();
			logger.error( e.getMessage() );
			throw e;
		}
		closeSession();
		logger.debug("done");
		return tControl;
	}
	

	public TimeControl getTimeControl(int idTimeControl) throws NotFoundException {
		logger.debug("get " + idTimeControl + "...");
		DaoFactory<TimeControl> df = new DaoFactory<TimeControl>();
		IDao<TimeControl> fm = df.getDao(this.session, TimeControl.class);
		TimeControl tControl = null;
		try {
			tControl = fm.getDO(idTimeControl);
		} catch ( Exception e ) {
			closeSession();		
			throw e;
		} 
		closeSession();		
		logger.debug("done: " + tControl.getHash() );
		return tControl;
	}
	

	public void deleteTimeControl(TimeControl tControl) throws DeleteException {
		logger.debug("delete" );
		DaoFactory<TimeControl> df = new DaoFactory<TimeControl>();
		IDao<TimeControl> fm = df.getDao(this.session, TimeControl.class);
		try {
			fm.deleteDO(tControl);
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
