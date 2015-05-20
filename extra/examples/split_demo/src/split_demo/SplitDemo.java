package split_demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SplitDemo {
	// To hold parameters...
	private static String inputFile;		// args[0]
	private static String workFolder;		// args[1]

	private static List<String> outputData = new ArrayList<String>();

	public static void processLine( String[] lineData ) {
		String fileName = lineData[1].replace("\"", "");
		try {
			splitFile( fileName, workFolder + "/outbox");
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static void printOutput() throws Exception {
	    PrintWriter pw = new PrintWriter(new FileOutputStream( workFolder + "/sagi_output.txt") );
		for ( String line : outputData  ) {
	        pw.println( line );
		}
	    pw.close();
	}


	public static void main(String[] args) throws Exception {
		inputFile = args[0];		// CSV input data file
		workFolder = args[1];		// Working folder 

		List<String> inputData = readFile( inputFile );
		if( inputData.size() > 0 ) {
			// prepare output CSV header 
			// ( must match activity destination table schema in Sagitarii )
			outputData.add("\"nome\",\"arquivo\"");

			// If we have lines of data
			if( inputData.size() > 1 ) {
				// With each line of data...
				for( int x=1; x<inputData.size(); x++ ) {
					String[] lineData = inputData.get(x).split(",");
					// ... do something
					processLine( lineData );
				}
			}
			// Print out the outputData content to screen 
			// ( send to Teapot by using standard out )
			printOutput();
		}
	}


	/**
	 * This is a method to read the CSV data 
	 * @param file
	 * @return StringBuilder : The file data as a list of lines.
	 * @throws Exception
	 */
	public static List<String> readFile(String file) throws Exception {
		Scanner s = new Scanner( new File(file), "UTF-8" ).useDelimiter("\n");
		ArrayList<String> list = new ArrayList<String>();
		while (s.hasNext()){
			list.add(s.next());
		}
		s.close();
		return list;
	}

	/**
	 * Uncompress a ZIP file
	 * 
	 * @param zipFile
	 * @param outputFolder
	 */
	public static void splitFile( String zipFile, String outputFolder ) {
		byte[] buffer = new byte[1024];
		try {
			File folder = new File( outputFolder );
			if( !folder.exists() ){
				folder.mkdirs();
			}
			ZipInputStream zis = new ZipInputStream(new FileInputStream( workFolder + "/inbox/" + zipFile ) );
			ZipEntry ze = zis.getNextEntry();
			while(ze!=null) {
				String fileName = ze.getName();
				outputData.add("\"" + fileName + "\",\"" + fileName + "\"");
				File newFile = new File(outputFolder + File.separator + fileName);
				new File(newFile.getParent()).mkdirs();
				FileOutputStream fos = new FileOutputStream(newFile);             
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();   
				ze = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
		} catch ( IOException ex ){
			ex.printStackTrace(); 
		}
	}    

}
