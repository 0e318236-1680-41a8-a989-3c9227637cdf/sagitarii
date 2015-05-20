package cmabreu.sagitarii.core.sockets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmabreu.sagitarii.misc.PathFinder;

public class FileReceiverManager {
	private static FileReceiverManager instance;
	private Server server;
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
	
	
	public List<TransferSession> getTransferSessions() throws Exception {
		List<TransferSession> sessions = new ArrayList<TransferSession>();
		for ( String sessionSerial : getSessions()  ) {
			TransferSession session = new TransferSession( sessionSerial );
			session.setSavers( getSaversInTransfer( sessionSerial ) );
			session.setImporters( getImportersBySession( sessionSerial ) );
			sessions.add( session );
		}
		return sessions;
	}
	
	public static FileReceiverManager getInstance() throws Exception {
		if ( instance == null ) {
			instance = new FileReceiverManager();
		}
		return instance;
	}
	
	public List<FileImporter> getImporters() {
		return server.getImporters();
	}
	
	public List<FileImporter> getImportersBySession( String sessionSerial ) {
		List<FileImporter> importers = new ArrayList<FileImporter>();
		for ( FileImporter importer : getImporters() ) {
			if ( importer.getSessionSerial().equals( sessionSerial ) ) {
				importers.add( importer );
			}
		}
		return importers;
	}
	
	public boolean workingOnExperiment( String experimentSerial ) {
		int importers = getImportersByExperiment( experimentSerial ).size();
		int savers = getSaversByExperiment( experimentSerial ).size();
		logger.debug("check open sessions for experiment " + experimentSerial + ": savers: " + savers + " importers: " + importers);
		return ( ( importers > 0 ) && ( savers > 0) );
	}
	
	public List<FileImporter> getImportersByExperiment( String experimentSerial ) {
		List<FileImporter> importers = new ArrayList<FileImporter>();
		for ( FileImporter importer : getImporters() ) {
			if ( importer.getMainCsvFile().getExperimentSerial().equals( experimentSerial ) ) {
				importers.add( importer );
			}
		}
		return importers;
	}
	
	/**
	 * Get all active Savers (open sessions)
	 * 
	 * @return a list of FileSaver objects
	 */
	public List<FileSaver> getSavers() {
		return server.getSavers();
	}

	/**
	 * Start the File Receiver Server
	 * @param serverPort the port to listen (default 3333)
	 * @param chunkBuffer the size of chunk data. Warning: This value must be the same found in Teapot configuration.
	 *  
	 */
	public void startServer( int serverPort, int chunkBuffer ) {
		logger.debug("start server port " + serverPort + " buffer " + chunkBuffer );
		if ( server == null ) {
			server = new Server( serverPort, chunkBuffer );
			server.setName("Sagitarii Data Receiver Server (port " + serverPort + ")");
			server.start();
		}
	}
	
	/**
	 * Start a transaction in Sagitarii File Cache to receive data from Node
	 * @return "ok" or "error"
	 * @throws Exception if error.
	 */
	public String beginTransaction() throws Exception {
		return server.beginTransaction();
	}
	
	/**
	 * Stop all Savers from this session and clear the cache.
	 * This will delete all files received.
	 * @param sessionSerial the session that will be forced to stop
	 * 
	 */
	public void forceStopAndCancel( String sessionSerial ) throws Exception {
		logger.debug("will stop session " + sessionSerial + " at user request");
		logger.debug("pausing server...");
		logger.debug("notifying Savers...");
		for ( FileSaver saver : getSavers() ) {
			if ( saver.getSessionSerial().equals(sessionSerial) ) {
				saver.stopProcess();
			}
		}
		logger.debug("notifying Importers...");
		for ( FileImporter importer : getImporters() ) {
			if ( importer.getSessionSerial().equals( sessionSerial ) ) {
				importer.stopProcess();
			}
		}
		Thread.sleep(1000);
		logger.debug("closing session...");
		server.closeTransaction( sessionSerial );
		logger.debug("resuming server...");
		logger.debug("done closing session " + sessionSerial);
	}

	
	/**
	 * Return all savers from a given session serial number
	 * 
	 * @param sessionSerial a session serial number
	 * @return a list of FileSavers from that session
	 * 
	 */
	public List<FileSaver> getSaversBySession( String sessionSerial ) {
		List<FileSaver> savers = new ArrayList<FileSaver>();
		for ( FileSaver saver : getSavers() ) {
			if ( saver.getSessionSerial().equals( sessionSerial ) ) {
				savers.add( saver );
			}
		}
		return savers;
	}

	/**
	 * Return all savers in transfer process
	 * @return a list of active savers
	 */
	public List<FileSaver> getSaversInTransfer() {
		List<FileSaver> savers = new ArrayList<FileSaver>();
		for ( FileSaver saver : getSavers() ) {
			if ( saver.getStatus() == SaverStatus.TRANSFERRING ) {
				savers.add( saver );
			}
		}
		return savers;
	}
	
	/**
	 * Returns all savers in progress for a given session
	 * 
	 * @param sessionSerial the session ID
	 * @return a list of savers in progress for that session
	 */
	public List<FileSaver> getSaversInTransfer( String sessionSerial ) {
		List<FileSaver> savers = new ArrayList<FileSaver>();
		for ( FileSaver saver : getSavers() ) {
			try {
				if ( (saver.getStatus() == SaverStatus.TRANSFERRING) && (saver.getSessionSerial().equals( sessionSerial ) ) ) {
					savers.add( saver );
				}
			} catch ( Exception e ) {
				// incomplete saver...
			}
		}
		return savers;
	}

	
	/**
	 * Return all savers from a given experiment Execution Tag (serial)
	 * @param experimentSerial 
	 * @return a list of FileSavers from that experiment
	 * 
	 */
	public List<FileSaver> getSaversByExperiment( String experimentSerial ) {
		List<FileSaver> savers = new ArrayList<FileSaver>();
		for ( FileSaver saver : getSavers() ) {
			if ( (saver.getStatus() == SaverStatus.TRANSFERRING) && (saver.getSessionSerial().equals( experimentSerial ) ) ) {
				savers.add( saver );
			}
		}
		return savers;
	}

	/**
	 * Return all savers that is importing data to a given table name
	 * 
	 * @param tableName a table name
	 * @return a list of savers that is importing data to this table
	 *  
	 */
	public List<FileSaver> getSaversByTargetTable( String tableName ) {
		List<FileSaver> savers = new ArrayList<FileSaver>();
		for ( FileSaver saver : getSavers() ) {
			if ( saver.getTargetTable().equals( tableName ) ) {
				if ( saver.getStatus() == SaverStatus.TRANSFERRING ) {
					savers.add( saver );
				}
			}
		}
		return savers;
	}

	/**
	 * Commit a session.
	 * Send all files to the files table and import CSV data to target table.
	 * 
	 * @param sessionSerial the session to commit. Must be idle (no active transfers). 
	 * @throws in case of error
	 */
	public void commit( String sessionSerial ) throws Exception {
		server.commit( sessionSerial );
	}
	
	
	public String getSaversPercentAsJson( String sessionSerial ) {
		List<FileSaver> savers;
		if ( (sessionSerial != null) && (!sessionSerial.equals(""))  ) {
			savers = getSaversBySession( sessionSerial );
		} else {
			savers = getSaversInTransfer();
		}
		
		String prefix = "";
		StringBuilder sb = new StringBuilder();
		sb.append( "[" );
		for ( FileSaver saver : savers ) {
			String tag = saver.getTag();
			String percent = String.valueOf( saver.getPercent() );
			String total = String.valueOf( saver.getTotalBytes() );
			String done = String.valueOf( saver.getBytes() );
			String data = "{\"total\":\""+total+"\",\"done\":\""+done+"\",\"percent\":\""+percent+"\",\"tag\":\""+tag+"\"}";
			sb.append( prefix + data);
			prefix = ",";
		}
		sb.append("]");
		return sb.toString();
	}

	public String getImportersPercentAsJson( String sessionSerial ) {
		List<FileImporter> importers = getImportersBySession( sessionSerial );
		String prefix = "";
		StringBuilder sb = new StringBuilder();
		sb.append( "[" );
		for ( FileImporter importer : importers ) {
			String tag = importer.getTag();
			String percent = String.valueOf( importer.getPercent() );
			String total = String.valueOf( importer.getImportedLines() );
			String done = String.valueOf( importer.getInsertedLines() );
			String targetTable = "";
			try {
				targetTable = importer.getMainCsvFile().getTargetTable();
			} catch ( Exception e ) {	}
			String status = importer.getStatus();
			String data = "{\"status\":\""+status+"\",\"targetTable\":\""+targetTable+"\",\"total\":\""+total+"\",\"done\":\""+done+"\",\"percent\":\""+percent+"\",\"tag\":\""+tag+"\"}";
			sb.append( prefix + data);
			prefix = ",";
		}
		sb.append("]");
		return sb.toString();
	}
	
	public List<String> getSessions() throws Exception {
		List<String> sessions = new ArrayList<String>();
		File filesFolder = new File( PathFinder.getInstance().getPath() + "/cache/" );
	    for (final File fileEntry : filesFolder.listFiles() ) {
	        if ( fileEntry.isDirectory() ) {
	        	sessions.add( fileEntry.getName() );
	        }
	    }
		return sessions;
	}
	
	private FileReceiverManager() throws Exception {
		logger.debug("start");
		String fileCacheDirectory = PathFinder.getInstance().getPath() + "/cache/";
		File cache = new File( fileCacheDirectory );
		// Clear old cache
		FileUtils.deleteDirectory( cache );
		// Create a new one
		cache.mkdirs();
	}
	
	
	
}
