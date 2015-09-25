package br.cefetrj.sagitarii.persistence.repository;

import java.util.List;

import br.cefetrj.sagitarii.persistence.entity.LogEntry;
import br.cefetrj.sagitarii.persistence.exceptions.DatabaseConnectException;
import br.cefetrj.sagitarii.persistence.exceptions.InsertException;
import br.cefetrj.sagitarii.persistence.exceptions.NotFoundException;
import br.cefetrj.sagitarii.persistence.infra.DaoFactory;
import br.cefetrj.sagitarii.persistence.infra.IDao;

public class LogRepository extends BasicRepository {

	public LogRepository() throws DatabaseConnectException {
		super();
		logger.debug("Repository init");
	}

	public List<LogEntry> getList() throws NotFoundException {
		logger.debug("Recuperando lista de logs..." );
		DaoFactory<LogEntry> df = new DaoFactory<LogEntry>();
		IDao<LogEntry> fm = df.getDao(this.session, LogEntry.class);
		List<LogEntry> logs = null;
		try {
			logs = fm.getList("select * from logs");
		} catch ( Exception e ) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("Concluido: " + logs.size() + " logs.");
		return logs;
	}
	
	public List<LogEntry> getList( String type) throws NotFoundException {
		logger.debug("Recuperando lista de logs..." );
		DaoFactory<LogEntry> df = new DaoFactory<LogEntry>();
		IDao<LogEntry> fm = df.getDao(this.session, LogEntry.class);
		List<LogEntry> logs = null;
		try {
			logs = fm.getList("select * from logs where type = '"+ type +"' order by date_time asc limit 500");
		} catch ( Exception e ) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("Concluido: " + logs.size() + " logs.");
		return logs;
	}

	public LogEntry insertLogEntry(LogEntry logEntry) throws InsertException {
		logger.debug("insert log...");
		DaoFactory<LogEntry> df = new DaoFactory<LogEntry>();
		IDao<LogEntry> fm = df.getDao(this.session, LogEntry.class);
		try {
			fm.insertDO(logEntry);
			commit();
		} catch (InsertException e) {
			logger.error( e.getMessage() );
			rollBack();
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done.");
		return logEntry;
	}

	public LogEntry getLogEntry(int idLogEntry) throws NotFoundException {
		logger.debug("retrieve LogEntry " + idLogEntry + "...");
		DaoFactory<LogEntry> df = new DaoFactory<LogEntry>();
		IDao<LogEntry> fm = df.getDao(this.session, LogEntry.class);
		LogEntry logEntry = null;
		try {
			logEntry = fm.getDO(idLogEntry);
		} catch ( Exception e ) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done: " + logEntry.getNode() );
		return logEntry;
	}
	
	
}
