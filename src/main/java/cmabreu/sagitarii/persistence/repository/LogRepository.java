package cmabreu.sagitarii.persistence.repository;

import java.util.List;

import cmabreu.sagitarii.persistence.entity.LogEntry;
import cmabreu.sagitarii.persistence.exceptions.DatabaseConnectException;
import cmabreu.sagitarii.persistence.exceptions.InsertException;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.infra.DaoFactory;
import cmabreu.sagitarii.persistence.infra.IDao;

public class LogRepository extends BasicRepository {

	public LogRepository() throws DatabaseConnectException {
		super();
		logger.debug("Repository init");
	}

	public List<LogEntry> getList() throws NotFoundException {
		logger.debug("Recuperando lista de logEntrys..." );
		DaoFactory<LogEntry> df = new DaoFactory<LogEntry>();
		IDao<LogEntry> fm = df.getDao(this.session, LogEntry.class);
		List<LogEntry> logEntrys = null;
		try {
			logEntrys = fm.getList("select * from logEntrys");
		} catch ( Exception e ) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("Concluido: " + logEntrys.size() + " logEntrys.");
		return logEntrys;
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
