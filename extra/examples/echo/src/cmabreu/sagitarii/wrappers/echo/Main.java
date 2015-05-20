package cmabreu.sagitarii.wrappers.echo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class Main {
	private static String workFolder; 
	private static List<String> outputData = new ArrayList<String>();
	
	
	public static void saveOutput() throws FileNotFoundException {
	    PrintWriter pw = new PrintWriter( new FileOutputStream( workFolder + "/sagi_output.txt"  ) );
	    for ( String line : outputData ) {
	        pw.println( line );
	    }
	    pw.close();
	}
	
	private static boolean isDirEmpty( Path directory ) throws IOException {
		boolean result = false;
	    try( DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory) ) {
	        result = !dirStream.iterator().hasNext();
	        dirStream.close();
	    }
	    return result;
	}
	
	public static void main(String[] args) throws Exception{
		workFolder = args[0];		 
		List<String> inputData = readFile( workFolder + "/sagi_input.txt" );
		
		/*

		String previousOutbox = workFolder + "/inbox";
		String destInbox = workFolder + "/outbox";
		
		File source = new File( previousOutbox );
		File dest = new File( destInbox );
		if ( !isDirEmpty( source.toPath() )  ) {
			FileUtils.copyDirectory( source, dest );
		}
		
		*/
		
		try {
			Thread.sleep( 20000 );
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		outputData.addAll( inputData );
		saveOutput();
		
		
	}

	
	public static List<String> readFile(String file) throws Exception {
		String line = "";
		ArrayList<String> list = new ArrayList<String>();
		BufferedReader br = new BufferedReader( new FileReader( file ) );
		while ( (line = br.readLine() ) != null ) {
		    list.add( line );
		}
		if (br != null) {
			br.close();
		}		
		return list;
	}
	
	
	
}


