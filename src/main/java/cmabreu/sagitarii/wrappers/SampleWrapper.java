package cmabreu.sagitarii.wrappers;

/**
 * @author Carlos Magno Abreu 
 * magno.mabreu@gmail.com - 27/10/2014
 * 
 * This class is a sample wrapper.
 * It will execute a program on cluster machine and implement some task 
 * to do an Activity job. You decide what to do.
 * 
 * Sagitarii Teapot will receive some data as CSV, save on disk ( args[0] file in args[1] folder ) 
 * and call this wrapper as specified in XML pipeline file.
 * Your program must read this CSV data, do your job and print results as CSV data to
 * console, so Teapot can send it back to Sagitarii server with any file you may save in outbox folder. 
 * 
 * Teapot will give you these parameters:
 * 		- args[0] : The CSV file with input data.
 * 		- args[1] : The activation working folder.
 * 		- args[2] : The activation task ID serial number. To avoid file name conflicts you
 * 					MUST prefix all your output files with this parameter.
 * 		- args[3] : Sagitarii host address URL. Use it to download files from Sagitarii.
 * 					See downloadFile() method.
 * 		- args[4] : Experiment serial ID ( tag )
 * 
 *    	NOTE: 		If you want to send files to Sagitarii, just save them in args[1]/outbox folder.
 * 					This folder is already created by teapot for every activation. Don't forget other
 * 					activations will create files with same name, so you MUST prefix file name with
 * 					task serial ID number ( args[2] ).   			 	
 *  
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Please ee the following page for the LGPL license:
 * http://www.gnu.org/licenses/lgpl.txt
 *
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class SampleWrapper {
	// To hold parameters...
	private static String workFolder;		// args[0]
	// The output CSV data holder ( index 0 = header ) 
	private static List<String> outputData = new ArrayList<String>();

	/**
	 * Get the column index. You will need this to get the data value from this column given a line of data.
	 * 
	 * Ex: 
	 * 		header  = name,age,address
	 * 		data    = foo, 24, Elm Street
	 * 
	 * 		data[ getIndex("age") ] will return "24" 
	 * 
	 * 
	 * @param key the column name
	 * @param header the header columns in CSV format
	 * @return the index number of this column name
	 */
	private static int getIndex( String key, String header ) {
		int index = -1;
		String[] headers = header.split(",");
		for ( int x = 0; x < headers.length; x++  ) {
			if ( headers[x].equals( key )  ) {
				index = x;
			}
		}
		return index;
	}
	
	/**
	 * Process each line of CSV data
	 * @param line : Array of string: Contains all columns of a line
	 * 
	 * You must call line[x] for a specific column
	 * Example:
	 * 
	 * ID, NAME, FILE
	 * 34, test, myfile.dat
	 * 
	 * String id = line[0]
	 * String name = line[1]
	 * String file = line[2]
	 * 
	 */
	public static void processLine(  String header, String line  ) {
		// This method is called for each line of your CSV data
		// Each index in "lines" will be a column of your CSV data
		
		// Split line data into an array
		String[] lineData = line.split(",");
		// Get the inbox folder (you may need to get some files from that)
		String inboxFolder = workFolder + File.separator + "inbox";
		
		// if you need to get the data from a given column name...
		int dataIndex = getIndex("columnFooBar", header);
		// Get the correspondent data from this column
		String myData = lineData[ dataIndex ];
		
		// Just echo the line data to output file
		outputData.add( line );
	}
	
	
	/**
	 * Entrance point
	 * 
	 * @param args Array of String with parameters passed by Teapot
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// Teapot will give you these parameter:
		workFolder = args[0];		// Working folder 
		// Input data file
		String inputFile = workFolder + "/sagi_input.txt";
		
		// Read the CSV input data file
		List<String> inputData = readFile( inputFile );
		// If we have data...
		if( inputData.size() > 0 ) {
			String header = inputData.get(0);
			// If we have lines of data
			if( inputData.size() > 1 ) {
				// With each line of data...
				for( int x = 1; x < inputData.size(); x++ ) { // Start from 1 to discard column names.
					String lineData = inputData.get(x); 
					// ... do something with each line
					processLine( header, lineData );
				}
			}
			
			String outputCsvFile = workFolder + File.separator + "sagi_output.txt";
			
			saveFile( outputData, outputCsvFile );		}
	}
  
	/**
	 * Save a CSV file
	 * 
	 * @param fileName the complete file path + name
	 * @throws FileNotFoundException in case of error
	 * 
	 */
	public static void saveFile(List<String> outputCsv, String fileName) throws FileNotFoundException {
	    PrintWriter pw = new PrintWriter(new FileOutputStream(fileName));
	    for ( String outLine : outputCsv ) {
	        pw.println( outLine );
	    }
	    pw.close();
	}
	
	/**
	 * If you need to move files
	 * @param source source file
	 * @param dest destination
	 * @throws IOException in case of any error
	 */
	private static void moveFile(String source, String dest) throws IOException {
		File src = new File(source);
		File trgt = new File(dest);
		if ( src.exists() ) {
		    Files.copy(src.toPath(), trgt.toPath());
		    src.delete();
		}
	}	

	/**
	 * This is a method to read the CSV data 
	 * @param file
	 * @return StringBuilder : The file data as a list of lines.
	 * @throws Exception
	 */
	public static List<String> readFile(String file) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(file));
		ArrayList<String> list = new ArrayList<String>();
		String line;
		while ( (line = br.readLine() ) != null) {
		    list.add( line );
		}
		br.close();
		return list;
	}
	
}