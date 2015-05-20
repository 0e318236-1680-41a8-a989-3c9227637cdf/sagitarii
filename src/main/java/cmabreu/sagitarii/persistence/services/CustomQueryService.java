package cmabreu.sagitarii.persistence.services;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmabreu.sagitarii.persistence.entity.CustomQuery;
import cmabreu.sagitarii.persistence.exceptions.DatabaseConnectException;
import cmabreu.sagitarii.persistence.exceptions.DeleteException;
import cmabreu.sagitarii.persistence.exceptions.InsertException;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.exceptions.UpdateException;
import cmabreu.sagitarii.persistence.repository.CustomQueryRepository;

public class CustomQueryService {
	private CustomQueryRepository rep;
	private Logger logger = LogManager.getLogger( this.getClass().getName() );

	public CustomQueryService() throws DatabaseConnectException {
		this.rep = new CustomQueryRepository();
	}

	public void updateCustomQuery(CustomQuery customQuery) throws UpdateException {
		CustomQuery oldCustomQuery;
		try {
			oldCustomQuery = rep.getCustomQuery( customQuery.getIdCustomQuery() );
		} catch (NotFoundException e) {
			throw new UpdateException( e.getMessage() );
		}
		oldCustomQuery.setName( customQuery.getName() );
		oldCustomQuery.setQuery( customQuery.getQuery() );
		
		rep.newTransaction();
		rep.updateCustomQuery(oldCustomQuery);
	}	

	public CustomQuery getCustomQuery(int idCustomQuery) throws NotFoundException{
		return rep.getCustomQuery(idCustomQuery);
	}

	public void newTransaction() {
		if ( !rep.isOpen() ) {
			rep.newTransaction();
		}
	}
	
	public CustomQuery insertCustomQuery(CustomQuery customQuery, int idExperiment) throws InsertException {
		CustomQuery expRet = rep.insertCustomQuery( customQuery, idExperiment );
		return expRet ;
	}	
	
	public void deleteCustomQuery( int idCustomQuery ) throws DeleteException {
		try {
			CustomQuery customQuery = rep.getCustomQuery(idCustomQuery);
			rep.newTransaction();
			rep.deleteCustomQuery(customQuery);
		} catch (NotFoundException e) {
			logger.error( e.getMessage() );
			throw new DeleteException( e.getMessage() );
		}
	}

	public Set<CustomQuery> getList( int idExperiment ) throws NotFoundException {
		return rep.getList( idExperiment );
	}
	
}
