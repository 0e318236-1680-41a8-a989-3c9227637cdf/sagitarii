package cmabreu.sagitarii.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
 
public class Client {
	private List<String> filesToSend;
	private String storageAddress;
	private int storagePort;
	private String sessionSerial;
	
	public Client( String storageAddress, int storagePort) {
		filesToSend = new ArrayList<String>();
		this.storageAddress = storageAddress;
		this.storagePort = storagePort;
	}
	
	
	public void sendFile( String fileName, String folder, String targetTable,  
			String experimentSerial, String macAddress, String pipelineSerial, String activity ) throws Exception {
		
		getSessionKey();
		
		StringBuilder xml = new StringBuilder();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		
		xml.append("<session macAddress=\""+macAddress+"\" instance=\""+pipelineSerial+
				"\" activity=\""+activity+"\"  experiment=\""+experimentSerial+
				"\" id=\""+sessionSerial+"\" targetTable=\""+targetTable+"\">\n");
		
		xml.append("<file name=\""+fileName+"\" type=\"FILE_TYPE_CSV\" />\n");
		filesToSend.add( folder + "/" + fileName );
		
		File filesFolder = new File( folder + "/outbox" );
	    for (final File fileEntry : filesFolder.listFiles() ) {
	        if ( !fileEntry.isDirectory() ) {
	    		xml.append("<file name=\""+fileEntry.getName()+"\" type=\"FILE_TYPE_FILE\" />\n");
	    		filesToSend.add( folder + "/outbox/" + fileEntry.getName() );
	        }
	    }
		
		xml.append("</session>\n");
		filesToSend.add( folder + "/session.xml" );
		PrintWriter writer = new PrintWriter( new FileOutputStream(folder + "/session.xml") );
		writer.write( xml.toString() );
		writer.close();

		for ( String toSend : filesToSend ) {
			System.out.println("will send " + toSend );
			uploadFile( toSend, targetTable, experimentSerial, sessionSerial );
		}
		
		commit();
		
	}
	
	
	private void uploadFile( String fileName, String targetTable, String experimentSerial, String sessionSerial ) throws Exception {
        File file = new File(fileName);
        Socket socket = new Socket( storageAddress, storagePort);
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
 
        oos.writeObject( file.getName() );
        oos.writeObject( sessionSerial );
        oos.writeObject( file.length() );
        oos.writeObject( targetTable );
        oos.writeObject( experimentSerial );
 
        System.out.println("sending " + file.getName() );
        
        FileInputStream fis = new FileInputStream(file);
        byte [] buffer = new byte[100];
        Integer bytesRead = 0;
 
        while ( (bytesRead = fis.read(buffer) ) > 0) {
            oos.writeUnshared(bytesRead);
            oos.writeUnshared(Arrays.copyOf(buffer, buffer.length));
        }
 
        fis.close();
        oos.close();
        ois.close();
        socket.close();
	}

	private void commit() throws Exception {
		URL url = new URL("http://localhost:8580/sagitarii/transactionManager?command=commit&sessionSerial=" + sessionSerial );
		Scanner s = new Scanner( url.openStream() );
		String response = s.nextLine();
		System.out.println("commit: " + response);
		s.close();
	}
	
	private void getSessionKey() throws Exception {
		URL url = new URL("http://localhost:8580/sagitarii/transactionManager?command=beginTransaction");
		Scanner s = new Scanner( url.openStream() );
		sessionSerial = s.nextLine();
		System.out.println("open session " + sessionSerial );
		s.close();
	}
	
    public static void main(String[] args) throws Exception {
    	
    	if ( args.length != 4  ) {
    		System.out.println("Parameters are:");
    		System.out.println("fileName targetTable experimentSerial sourceFolder" );
    		System.exit(0);
    	}
    	
        String fileName = args[0];
        String targetTable = args[1];
        String experimentSerial = args[2];
        String folder = args[3];
        
        new Client("localhost", 3333).sendFile( fileName, folder, targetTable, experimentSerial, "UPLOADER", "", "" );
        
    }
}