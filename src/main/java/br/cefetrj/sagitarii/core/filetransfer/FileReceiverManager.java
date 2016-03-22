package br.cefetrj.sagitarii.core.filetransfer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.cefetrj.sagitarii.misc.PathFinder;

public class FileReceiverManager {
	private static FileReceiverManager instance;
	private Server server;
	private String status = "WORKING";
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
	
	public void stopServer() {
		server.stopServer();
	}
	
	public List<TransferSession> getTransferSessions() throws Exception {
		List<TransferSession> sessions = new ArrayList<TransferSession>();
		for ( String sessionSerial : getSessions()  ) {
			TransferSession session = new TransferSession( sessionSerial );
			session.setImporter( getImporterBySession( sessionSerial ) );
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
	
	public FileImporter getImporterBySession( String sessionSerial ) {
		for ( FileImporter importer : getImporters() ) {
			if ( importer.getSessionSerial().equals( sessionSerial ) ) {
				return importer;
			}
		}
		return null;
	}
	
	public boolean workingOnExperiment( String experimentSerial ) {
		int importers = getImportersByExperiment( experimentSerial ).size();
		logger.debug("check open sessions for experiment " + experimentSerial + " importers: " + importers);
		return ( importers > 0 ) ;
	}
	
	public List<FileImporter> getImportersByExperiment( String experimentSerial ) {
		List<FileImporter> importers = new ArrayList<FileImporter>();
		for ( FileImporter importer : getImporters() ) {
			try {
				if ( importer.getMainCsvFile().getExperimentSerial().equals( experimentSerial ) ) {
					importers.add( importer );
				}
			} catch ( Exception e ) {
				logger.error( "Error checking Importers for Experiment " + experimentSerial + ": " + e.getMessage() );
			}
		}
		return importers;
	}
	
	/**
	 * Start the File Receiver Server
	 */
	public void startServer( int serverPort, int chunkBuffer ) {
		logger.debug("start server port " + serverPort + " buffer " + chunkBuffer );
		if ( server == null ) {
			try {
				server = new Server( serverPort, chunkBuffer );
			} catch ( Exception e ) {
				logger.error("Error while starting FTP server: " + e.getMessage() );
			}
		}
		logger.debug("done");
	}
	
	/**
	 * Start a transaction in Sagitarii File Cache to receive data from Node
	 */
	public String beginTransaction() throws Exception {
		return server.beginTransaction();
	}
	
	/**
	 * Stop all Savers from this session and clear the cache.
	 * This will delete all files received.
	 */
	public void forceStopAndCancel( String sessionSerial ) throws Exception {
		logger.debug("will stop session " + sessionSerial + " at user request");
		logger.debug("pausing server...");

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
	 * Commit a session.
	 * Send all files to the files table and import CSV data to target table.
	 */
	public void commit( String sessionSerial ) throws Exception {
		server.commit( sessionSerial );
	}
	
	
	public String getImportersPercentAsJson( String sessionSerial ) {
		FileImporter importer = getImporterBySession( sessionSerial );
		String prefix = "";
		StringBuilder sb = new StringBuilder();
		sb.append( "[" );
		if ( importer != null ) {
			String tag = importer.getTag();
			String percent = String.valueOf( importer.getPercent() );
			String total = String.valueOf( importer.getImportedLines() );
			String done = String.valueOf( importer.getInsertedLines() );
			String targetTable = "";
			status = importer.getStatus();
			try {
				targetTable = importer.getMainCsvFile().getTargetTable();
			} catch ( Exception e ) {	}
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
		String fileCacheDirectory = PathFinder.getInstance().getPath() + "/cache/";
		File cache = new File( fileCacheDirectory );
		// Clear old cache
		FileUtils.deleteDirectory( cache );
		// Create a new one
		cache.mkdirs();
	}
	
	
	
}
