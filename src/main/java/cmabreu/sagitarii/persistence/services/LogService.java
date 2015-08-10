package cmabreu.sagitarii.persistence.services;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmabreu.sagitarii.persistence.entity.LogEntry;
import cmabreu.sagitarii.persistence.exceptions.DatabaseConnectException;
import cmabreu.sagitarii.persistence.exceptions.InsertException;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.repository.LogRepository;


public class LogService { 
	private LogRepository rep;
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
	
	public LogService() throws DatabaseConnectException {
		this.rep = new LogRepository();
	}
	
	public void newTransaction() {
		rep.newTransaction();
	}
	
	public void insertLogEntry(LogEntry logEntry) throws InsertException {
		logger.debug("inserting Log " );
		rep.insertLogEntry( logEntry );
	}
	
	public LogEntry getLogEntry(int idLog) throws NotFoundException{
		return rep.getLogEntry(idLog);
	}

	public void insetLogEntryList( List<LogEntry> list ) throws Exception {
		for ( LogEntry logEntry : list ) {
			rep.newTransaction();
			insertLogEntry(logEntry);
		}
	}
	
	public List<LogEntry> getList() throws NotFoundException {
		logger.debug( "retrieving log list..." );  
		List<LogEntry> preList = rep.getList();
		logger.debug( "done." );  
		return preList;	
	}
	
}