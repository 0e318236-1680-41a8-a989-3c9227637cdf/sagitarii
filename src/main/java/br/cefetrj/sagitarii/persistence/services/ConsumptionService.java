package br.cefetrj.sagitarii.persistence.services;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.cefetrj.sagitarii.persistence.entity.Consumption;
import br.cefetrj.sagitarii.persistence.exceptions.DatabaseConnectException;
import br.cefetrj.sagitarii.persistence.exceptions.DeleteException;
import br.cefetrj.sagitarii.persistence.exceptions.InsertException;
import br.cefetrj.sagitarii.persistence.exceptions.NotFoundException;
import br.cefetrj.sagitarii.persistence.repository.ConsumptionRepository;

public class ConsumptionService {
	private ConsumptionRepository rep;
	private Logger logger = LogManager.getLogger( this.getClass().getName() );

	public ConsumptionService() throws DatabaseConnectException {
		this.rep = new ConsumptionRepository();
	}

	public Consumption getConsumption(int idConsumption) throws NotFoundException{
		return rep.getConsumption(idConsumption);
	}

	public void newTransaction() {
		if ( !rep.isOpen() ) {
			rep.newTransaction();
		}
	}
	
	public Consumption insertConsumption(Consumption consumption) throws InsertException {
		Consumption expRet = rep.insertConsumption( consumption );
		return expRet ;
	}	
	
	public void deleteConsumption( int idConsumption ) throws DeleteException {
		try {
			Consumption consumption = rep.getConsumption(idConsumption);
			rep.newTransaction();
			rep.deleteConsumption(consumption);
		} catch (NotFoundException e) {
			logger.error( e.getMessage() );
			throw new DeleteException( e.getMessage() );
		}
	}

	public Set<Consumption> getList( int idInstance ) throws NotFoundException {
		return rep.getList( idInstance );
	}
	
}
