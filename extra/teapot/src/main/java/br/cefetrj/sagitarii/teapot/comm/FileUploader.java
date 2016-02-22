package br.cefetrj.sagitarii.teapot.comm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;

import br.cefetrj.sagitarii.teapot.LogManager;
import br.cefetrj.sagitarii.teapot.Logger;

public class FileUploader implements Runnable {
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
	private int fileSenderDelay;
	private String storageAddress;
	private int storagePort;	
	private String fileName;
	private String targetTable;
	private String experimentSerial;
	private String sessionSerial;
	private Client client;

	public FileUploader( Client client, String storageAddress, int storagePort, int fileSenderDelay,
			 String fileName, String targetTable, String experimentSerial, String sessionSerial ) {
		this.storageAddress = storageAddress;
		this.storagePort = storagePort;
		this.fileSenderDelay = fileSenderDelay;
		this.fileName = fileName;
		this.targetTable = targetTable;
		this.experimentSerial = experimentSerial;
		this.sessionSerial = sessionSerial;
		this.client = client;
	}
	
	private long uploadFile() throws Exception {
		String newFileName = fileName + ".gz";
		
		compress(fileName, newFileName);
		File file = new File(newFileName);

        logger.debug("sending " + file.getName() + " with size of " + file.length() + " bytes..." );
		
		@SuppressWarnings("resource")
		Socket socket = new Socket( storageAddress, storagePort);
        ObjectOutputStream oos = new ObjectOutputStream( socket.getOutputStream() );
        oos.flush();
 
        oos.writeObject( file.getName().replace(".gz", "") );
        
        oos.writeObject( sessionSerial );
        oos.writeObject( file.length() );
        oos.writeObject( targetTable );
        oos.writeObject( experimentSerial );
 
        FileInputStream fis = new FileInputStream(file);
        
        byte [] buffer = new byte[100];
        Integer bytesRead = 0;
 
        while ( (bytesRead = fis.read(buffer) ) > 0) {
            oos.writeUnshared(bytesRead);
            oos.writeUnshared(Arrays.copyOf(buffer, buffer.length));
        }
        
        try {
        	Thread.sleep( fileSenderDelay );
        } catch ( Exception e ) {
        	
        }
        
        oos.close();
        fis.close();

        long size = file.length();
        logger.debug("done sending " + file.getName() );
        file.delete();
        return size;
	}

	@Override
	public void run() {
		client.increaseThreadCount();
		try {
			uploadFile();
		} catch (Exception e) {
			logger.error( e.getMessage() );
			e.printStackTrace();
		}
		client.decreaseThreadCount();
	}
	
	/**
	 * Compress a file
	 */
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
