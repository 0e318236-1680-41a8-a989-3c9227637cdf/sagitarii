package br.cefetrj.sagitarii.teapot.comm.uploadstrategies;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import br.cefetrj.sagitarii.teapot.Logger;

public class StreamUploadStrategy implements IUploadStrategy {
	private Logger logger;
	private String storageAddress;
	private int storagePort;
	private int fileSenderDelay;
	
	public StreamUploadStrategy(Logger logger, String storageAddress, int storagePort, int fileSenderDelay ) {
		this.fileSenderDelay = fileSenderDelay;
		this.logger = logger;
		this.storageAddress = storageAddress;
		this.storagePort = storagePort;
	}
	
	public synchronized long uploadFile( List<String> fileName, String targetTable, String experimentSerial, 
			String sessionSerial, String sourcePath ) throws Exception {

		return 0;
		
		/*
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
        */
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
