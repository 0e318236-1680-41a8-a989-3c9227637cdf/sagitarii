package cmabreu.sagitarii.core.filetransfer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmabreu.sagitarii.misc.PathFinder;

public class FileSaver extends Thread {
	
	private Socket socket;
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
	private SaverStatus status = SaverStatus.TRANSFERRING;
	private int chunkBuffer;
	private String fileName;
	private long bytes;
	private long totalBytes;
	private String sessionSerial;
	private Date startDateTime;
	private int percent;
	private String targetTable;
	private String experimentSerial;
	private boolean stop = false;
	private String tag;
	private Server server;
	
	public String getTag() {
		return tag;
	}
	
	public SaverStatus getStatus() {
		return status;
	}
	
	public void setStatus(SaverStatus status) {
		this.status = status;
	}
	
	public String getExperimentSerial() {
		return experimentSerial;
	}
	
	public String getTargetTable() {
		return targetTable;
	}
	
	public int getPercent() {
		try {
			percent = Math.round( (bytes * 100 ) / totalBytes );
		} catch ( Exception e ) { percent = 0; }
		return percent;
	}
	
	public long getTotalBytes() {
		return totalBytes;
	}
	
	public Date getStartDateTime() {
		return startDateTime;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public long getBytes() {
		return bytes;
	}

	public String getSessionSerial() {
		return sessionSerial;
	}
	
	public FileSaver(Socket socket, int chunkBuffer, Server server ) {
		this.socket = socket;
		this.server = server;
		this.chunkBuffer = chunkBuffer;
		this.startDateTime = Calendar.getInstance().getTime();
	}

	/**
	 * Verify if the cache folder exists
	 * 
	 */
	private boolean checkSession( String sessionSerial ) throws Exception {
		Path cacheDirectory = new File(PathFinder.getInstance().getPath() + "/cache/" + sessionSerial + "/").toPath();
		return Files.exists( cacheDirectory, LinkOption.NOFOLLOW_LINKS);
	}
	
	/**
	 * Force this Saver to stop file transfer and stop thread.
	 * Status will be set to STOPPED.
	 * 
	 */
	public void stopProcess() {
		logger.debug("signaled to stop " + fileName + " when " + status );
		stop = true;
		status = SaverStatus.STOPPED;
	}
	
	private void saveFile() throws Exception {
		ObjectInputStream ois = new ObjectInputStream( socket.getInputStream() );
		byte[] buffer = new byte[chunkBuffer];

		Object o = ois.readUnshared();
		if (o instanceof String) {
			// File name
			fileName = o.toString();
			logger.debug("incomming upload thread for file " + fileName);
			
			// Read task serial number
			sessionSerial = (String)ois.readUnshared();
			if ( !checkSession( sessionSerial ) ) {
				throwException("session not opened yet");
			}
			
			logger.debug("receiving file " + fileName + " in session " + sessionSerial + "..." );
			
			// Read total file size
			totalBytes = (Long)ois.readUnshared();

			// Read target table
			targetTable = (String)ois.readUnshared();
			
			// Read owner experiment
			experimentSerial = (String)ois.readUnshared();
			
			String cacheDirectory = PathFinder.getInstance().getPath() + "/cache/" + sessionSerial + "/";
			String fullFileName = cacheDirectory + fileName;

			FileOutputStream fos = new FileOutputStream(fullFileName);

			// 2. Read file to the end.
			Integer bytesRead = 0;
			
			do {
				
				if ( stop ) {
					logger.debug("will stop transfer of " + fileName + " and close socket.");
					fos.close();
					ois.close();
					socket.close();
					server.closeTransaction( sessionSerial );
					throwException("Canceled by user request");
				} 
				
				try {
					o = ois.readUnshared();
					
					if ( !(o instanceof Integer) ) {
						fos.close();
						ois.close();
						throwException("invalid stream format: bytes read");
					}
	
					bytesRead = (Integer) o;
					bytes = bytes + bytesRead;
	
					o = ois.readUnshared();
					if ( !( o instanceof byte[] ) ) {
						fos.close();
						ois.close();
						throwException("invalid stream format: stream content");
					}
	
					buffer = ( byte[] ) o;
					// 3. Write data to output file.
					fos.write( buffer, 0, bytesRead );
					fos.flush();
					
				} catch ( Exception e ) {
					break;
				}

					
			} while ( bytesRead == chunkBuffer );

			logger.debug("done receiving " + fileName );
			fos.close();
			ois.close();
			
			status = SaverStatus.WAITCOMMIT;
		} else {
			throwException("invalid stream format: file name");
		}
		
		
		
	};

	@Override
	public void run() {
		tag = UUID.randomUUID().toString().replace("-", "");
		try {
			saveFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void throwException( String error ) throws Exception {
		logger.error(error);
		status = SaverStatus.ERROR;
		throw new Exception(error);
	}
	

}
