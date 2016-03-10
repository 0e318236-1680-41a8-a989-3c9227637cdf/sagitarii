package br.cefetrj.sagitarii.teapot.comm;
/**
 * Copyright 2015 Carlos Magno Abreu
 * magno.mabreu@gmail.com 
 *
 * Licensed under the Apache  License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required  by  applicable law or agreed to in  writing,  software
 * distributed   under the  License is  distributed  on  an  "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the  specific language  governing  permissions  and
 * limitations under the License.
 * 
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.zip.GZIPOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.turn.ttorrent.common.Torrent;

import br.cefetrj.sagitarii.teapot.Configurator;
import br.cefetrj.sagitarii.teapot.torrent.SynchFolderClient;
 
public class Client {
	private List<String> filesToSend;
	private String storageAddress;
	private int storagePort;
	private String sessionSerial;
	private String sagiHost;
	private int fileSenderDelay;
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
	private String announceUrl;

	
	public Client( Configurator configurator ) {
		filesToSend = new ArrayList<String>();
		this.storageAddress = configurator.getStorageHost();
		this.storagePort = configurator.getStoragePort();
		this.sagiHost = configurator.getHostURL();
		this.fileSenderDelay = configurator.getFileSenderDelay();
		this.announceUrl = configurator.getAnnounceUrl();		
	}
	
	
	public void sendFile( String fileName, String folder, String targetTable, String experimentSerial,  
			String macAddress) throws Exception {

		File f = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath() );
		String storageRootFolder =  f.getAbsolutePath();
		storageRootFolder = storageRootFolder.substring(0, storageRootFolder.lastIndexOf( File.separator ) + 1) + "namespaces/";
		
		String folderName = "outbox";
		String folderPath = folder.replace(storageRootFolder, "").replaceAll("/+", "/");
		
		SynchFolderClient sfc = new SynchFolderClient( storageRootFolder , announceUrl );
		Torrent torrent = sfc.createTorrentFromFolder(folderPath, folderName);
		
		String torrentFile = storageRootFolder + "/" + torrent.getHexInfoHash() + ".torrent";
		
		
		String instanceSerial = "";
		String activity = "";
		String fragment = "";
		String taskId = "";
		String exitCode = "0";
		getSessionKey();
		
		StringBuilder xml = new StringBuilder();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		
		xml.append("<session macAddress=\""+macAddress+"\" instance=\""+instanceSerial+
				"\" activity=\""+activity+"\"  taskId=\""+taskId+"\" exitCode=\""+exitCode+"\" fragment=\""+fragment + 
				"\" experiment=\""+experimentSerial + "\" id=\""+sessionSerial+"\" targetTable=\""+targetTable+"\">\n");
		
		xml.append("<file name=\""+fileName+"\" type=\"FILE_TYPE_CSV\" />\n");
		filesToSend.add( folder + "/" + fileName );
		
		File filesFolder = new File( folder + "/outbox" );
		if ( !filesFolder.exists() ) {
			filesFolder.mkdirs();
		}		
		

		File tor = new File(torrentFile);
		if ( tor.exists() ) {
			xml.append("<file name=\""+tor.getName()+"\" type=\"FILE_TYPE_TORRENT\" />\n");
			filesToSend.add( torrentFile );
		} else {
			logger.error("will not send Torrent file.");
		}		
		
	    for (final File fileEntry : filesFolder.listFiles() ) {
	        if ( !fileEntry.isDirectory() ) {
	    		xml.append("<file name=\""+fileEntry.getName()+"\" type=\"FILE_TYPE_FILE\" />\n");
	    		//filesToSend.add( folder + File.separator + "outbox" + File.separator + fileEntry.getName() );
	        }
	    }
		
	    xml.append("<console>");
	    xml.append("</console>");
	    
		xml.append("</session>\n");
		filesToSend.add( folder + File.separator + "session.xml" );
		PrintWriter writer = new PrintWriter( new FileOutputStream(folder + File.separator + "session.xml") );
		writer.write( xml.toString() );
		writer.close();

		long totalBytesSent = 0;
		if ( filesToSend.size() > 0 ) {
			logger.debug("need to send " + filesToSend.size() + " files to Sagitarii...");
			int indexFile = 1;
			for ( String toSend : filesToSend ) {
				logger.debug("[" + indexFile + "] will send " + toSend );
				indexFile++;
				totalBytesSent = totalBytesSent + uploadFile( toSend, targetTable, experimentSerial, sessionSerial );
			}
			logger.debug("total bytes sent: " + totalBytesSent );
		}
		commit();
		
		logger.debug("Will wait for Sagitarii to download the torrent...");
		while ( sfc.isSharing( torrent.getHexInfoHash() ) ) {
			sfc.show();
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
				//
			}
		}
		logger.debug("Done. Upload task finished.");
		
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
	
	
	private synchronized long uploadFile( String fileName, String targetTable, String experimentSerial, String sessionSerial ) throws Exception {
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

	private void commit() throws Exception {
		URL url = new URL( sagiHost + "/sagitarii/transactionManager?command=commit&sessionSerial=" + sessionSerial );
		Scanner s = new Scanner( url.openStream() );
		String response = s.nextLine();
		logger.debug("session "+sessionSerial+" commit: " + response);
		s.close();
	}
	
	private void getSessionKey() throws Exception {
		URL url = new URL( sagiHost + "/sagitarii/transactionManager?command=beginTransaction");
		Scanner s = new Scanner( url.openStream() );
		sessionSerial = s.nextLine();
		logger.debug("open session " + sessionSerial );
		s.close();
	}
	
}