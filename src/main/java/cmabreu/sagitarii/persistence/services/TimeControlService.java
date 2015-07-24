package cmabreu.sagitarii.persistence.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmabreu.sagitarii.core.statistics.Accumulator;
import cmabreu.sagitarii.persistence.entity.TimeControl;
import cmabreu.sagitarii.persistence.exceptions.DatabaseConnectException;
import cmabreu.sagitarii.persistence.exceptions.DeleteException;
import cmabreu.sagitarii.persistence.exceptions.InsertException;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.exceptions.UpdateException;
import cmabreu.sagitarii.persistence.repository.TimeControlRepository;

public class TimeControlService {
	private TimeControlRepository rep;
	private Logger logger = LogManager.getLogger( this.getClass().getName() );

	public TimeControlService() throws DatabaseConnectException {
		this.rep = new TimeControlRepository();
	}

	public void updateTimeControl(TimeControl tControl) throws UpdateException {
		TimeControl oldTimeControl;
		try {
			oldTimeControl = rep.getTimeControl( tControl.getIdTimeControl() );
			oldTimeControl.setAverageAge( tControl.getAverageAge() );
			oldTimeControl.setAverageMilis( tControl.getAverageMilis() );
			oldTimeControl.setCalculatedCount( tControl.getCalculatedCount() );
			oldTimeControl.setTotalAgeMilis( tControl.getTotalAgeMilis() );
		} catch ( Exception e) {
			throw new UpdateException( e.getMessage() );
		}
		
		rep.newTransaction();
		rep.updateTimeControl(oldTimeControl);
		
	}	
	
	
	public TimeControl getTimeControl(int idTimeControl) throws NotFoundException{
		return rep.getTimeControl(idTimeControl);
	}

	public void newTransaction() {
		if ( !rep.isOpen() ) {
			rep.newTransaction();
		}
	}
	
	public TimeControl insertTimeControl(TimeControl tControl) throws InsertException {
		TimeControl expRet = rep.insertTimeControl( tControl );
		return expRet ;
	}	

	
	public void deleteTimeControl( int idTimeControl ) throws DeleteException {
		try {
			TimeControl tControl = rep.getTimeControl(idTimeControl);
			rep.newTransaction();
			rep.deleteTimeControl(tControl);
		} catch (NotFoundException e) {
			logger.error( e.getMessage() );
			throw new DeleteException( e.getMessage() );
		}
	}

	public List<Accumulator> getList( ) throws NotFoundException {
		List<TimeControl> list = new ArrayList<TimeControl>( rep.getList( ) );
		List<Accumulator> listAc = new ArrayList<Accumulator>( );
		for ( TimeControl tc : list ) {
			Accumulator ac = new Accumulator( tc );
			listAc.add( ac );
		}
		return listAc;
	}

	
}
