package br.cefetrj.sagitarii.teapot.comm.uploadstrategies;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import br.cefetrj.sagitarii.teapot.Logger;

public class FTPUploadStrategy implements IUploadStrategy {
	private Logger logger;
	private String storageAddress;
	private int storagePort;
	private int fileSenderDelay;
	
    private String user = "cache";
    private String password = "cache";	
	
	public FTPUploadStrategy(Logger logger, String storageAddress, int storagePort, String user, String password, int fileSenderDelay) {
		this.logger = logger;
		this.storageAddress = storageAddress;
		this.storagePort = storagePort;
		this.user = user;
		this.password = password;
		this.fileSenderDelay = fileSenderDelay;
	}
	
	public synchronized long uploadFile( List<String> fileNames, String targetTable, String experimentSerial, 
			String sessionSerial, String sourcePath ) throws Exception {

		long size = 0;
        FTPClient ftpClient = new FTPClient();
        try {
 
            ftpClient.connect(storageAddress, storagePort);
            ftpClient.login(user, password);
            logger.debug("FTP Response: " + ftpClient.getReplyString() );  
            
			int indexFile = 1;
			for ( String fileName : fileNames ) {
				logger.debug("[" + indexFile + "] will send " + fileName );
				indexFile++;
			
				String newFileName = fileName + ".gz";
				compress(fileName, newFileName);
				File localFile = new File(newFileName);
				
				size = size + localFile.length();
				
		        logger.debug("sending " + localFile.getName() + " with size of " + localFile.length() + " bytes..." );
		        logger.debug("Strategy: FTP to " + user + "@" + storageAddress + ":" + storagePort);
			
	        	
	            ftpClient.enterLocalPassiveMode();
	            logger.debug("FTP Response: " +  ftpClient.getReplyString() );  
	            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	            
	            String remoteFile = "/" + sessionSerial + "/" + localFile.getName();
	            logger.debug("FTP copy " + newFileName + " to ftp://" + storageAddress + ":" + storagePort + remoteFile);            
	            
	            InputStream inputStream = new FileInputStream( localFile );            
	            boolean done = ftpClient.storeFile( remoteFile, inputStream );
	            logger.debug("FTP Response: " + ftpClient.getReplyString() );  
	            
	            inputStream.close();
	            if ( done ) {
	            	logger.debug("File [" + sessionSerial + "] " + localFile.getName()+" is uploaded successfully.");
	            } else {
	            	logger.error("Cant upload the file [" + sessionSerial + "] " + localFile.getName() );
	            }
	            
			}
            
        } catch ( Exception e ) {
        	logger.error("Error sending file by FTP: " + e.getMessage() );
        } finally {
        	try { 
        		ftpClient.disconnect();
            	logger.debug("FTP client disconnected.");
        	} catch ( Exception e ) { 
        		logger.error("cannot close FTP client: " + e.getMessage() );
        	}
        }
        
        try {
        	Thread.sleep( fileSenderDelay );
        } catch ( Exception e ) {
        	
        }
        
        return size;
	}
	
	public void compress(String source_filepath, String destinaton_zip_filepath) {
		logger.debug("compressing file ...");
		byte[] buffer = new byte[1024];
		try {
			FileOutputStream fileOutputStream =new FileOutputStream(destinaton_zip_filepath);
			GZIPOutputStream gzipOuputStream = new GZIPOutputStream(fileOutputStream);
			FileInputStream fileInput = new FileInputStream(source_filepath);
			int bytes_read;
			while ((bytes_read = fileInput.read(buffer)) > 0) {
				gzipOuputStream.write(buffer, 0, bytes_read);
			}
			fileInput.close();
			gzipOuputStream.finish();
			gzipOuputStream.close();
			fileOutputStream.close();
			
			logger.debug( "file was compressed successfully" );

		} catch (IOException ex) {
			logger.error("error compressing file: " + ex.getMessage() );
		}
	}	
	
	
}
