package br.cefetrj.sagitarii.core.filetransfer;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.cefetrj.sagitarii.metrics.MetricController;
import br.cefetrj.sagitarii.metrics.MetricType;
import br.cefetrj.sagitarii.misc.PathFinder;

public class Server extends Thread {
	private int chunkBuffer;
	private ServerSocket serverSocket;
	private boolean canStop = false;
	private List<FileSaver> savers;
	private int serverPort;
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
	private List<FileImporter> importers;
	
	public void pauseServer( long millis ) throws Exception {
		Thread.sleep(millis);
	}

	public List<FileImporter> getImporters() {
		return new ArrayList<FileImporter>( importers );
	}
	
	public String beginTransaction() throws Exception {
		String sessionSerial = UUID.randomUUID().toString().replace("-", "");
		String cacheDirectory = PathFinder.getInstance().getPath() + "/cache/" + sessionSerial + "/";
		new File(cacheDirectory).mkdirs();
		logger.debug("starting session " + sessionSerial);

		MetricController.getInstance().hit( "Session Open", MetricType.FILE );

		return sessionSerial;
	}

	
	public void closeTransaction( String sessionSerial ) throws Exception {
		String directory = PathFinder.getInstance().getPath() + "/cache/" + sessionSerial;
		File cache = new File( directory );
		FileUtils.deleteDirectory( cache );
		MetricController.getInstance().hit( "Session Close", MetricType.FILE );
	}	

	private boolean checkSession( String sessionSerial ) throws Exception {
		Path cacheDirectory = new File(PathFinder.getInstance().getPath() + "/cache/" + sessionSerial + "/").toPath();
		return Files.exists( cacheDirectory, LinkOption.NOFOLLOW_LINKS);
	}
	
	public String commit( String sessionSerial ) throws Exception {
		logger.debug("will commit session " + sessionSerial );
		
		if ( !checkSession(sessionSerial) ) {
			throw new Exception("Session not opened.");
		}
		
		FileImporter fi = new FileImporter( sessionSerial, this );
		fi.setName("Sagitarii session importer: " + sessionSerial );
		fi.start();
		importers.add(fi);
		return "ok";
	}
	
	public Server( int serverPort, int chunkBuffer ) {
		this.chunkBuffer = chunkBuffer;
		this.serverPort = serverPort;
		this.importers = new ArrayList<FileImporter>();
	}
	
	@Override
	public void run() {
		savers = new ArrayList<FileSaver>();
				
		logger.debug("start");
		
		try {

			try {
				serverSocket = new ServerSocket(serverPort);
				while ( !canStop ) {
					Socket s = serverSocket.accept();
					logger.debug("starting new saver thread");
					FileSaver saver = new FileSaver( s, chunkBuffer, this );

					MetricController.getInstance().hit( "Files Received", MetricType.FILE );
					
					savers.add( saver );
					saver.setName("Sagitarii file receiver");
					saver.start();
				}
				
			} catch (Exception e) {
				//
			}


		} catch ( Exception e ) {

		}

	}

	public void clean() {
		List<FileSaver> toRemove = new ArrayList<FileSaver>();
		List<FileImporter> importersToRemove = new ArrayList<FileImporter>();
		
		logger.debug("clean up");
		
		logger.debug("checking savers...");
		for ( FileSaver saver : savers ) {
			try {
				if ( (saver.getStatus() != SaverStatus.TRANSFERRING) && ( saver.getStatus() != SaverStatus.WAITCOMMIT )
						&& ( saver.getStatus() != SaverStatus.COMMITING ) ) {
					toRemove.add( saver ); 
				}
			} catch ( Exception e ) {
				//
			}
		}
		logger.debug("will clean " + toRemove.size() + " savers");
		savers.removeAll( toRemove );
		
		logger.debug("checking importers...");
		for ( FileImporter importer : getImporters() ) {
			logger.debug(" > " + importer.getName() + " " + importer.getStatus() );
			if ( importer.getStatus().equals("DONE") ) {
				importersToRemove.add( importer );
			}
		}
		logger.debug("will clean " + importersToRemove.size() + " importers");
		importers.removeAll( importersToRemove );
		
		logger.debug("done");
		
	}
	
	public List<FileSaver> getSavers() {
		return new ArrayList<FileSaver>( savers );
	}
	
	public void stopServer() {
		canStop = true;
		logger.debug("stop");
		
		for ( FileSaver saver :  savers ) {
			saver.stopProcess();
		}
		
		try {
			serverSocket.close();
		} catch ( Exception ignored ) { }
		
	}


}