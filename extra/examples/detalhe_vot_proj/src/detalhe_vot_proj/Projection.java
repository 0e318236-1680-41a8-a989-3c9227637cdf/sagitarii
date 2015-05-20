package detalhe_vot_proj;

/**
 * This class is a cluster wrapper.
 * It will execute a program on cluster machine implement some task 
 * to do an Activity job. You decide what to do.
 * 
 * Sagitarii Teapot will receive some data as CSV, save on disk and call this wrapper as
 * specified in XML manifest file.
 * So, your program must read this CSV data, do your job and print results as CSV data to
 * consloe, so Teapot can send it back to Sagitarii sever. 
 *  
 * @author Carlos Magno Abreu 
 * magno.mabreu@gmail.com - 27/10/2014
 *
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Projection {
	private static String inputFile;		// args[0]
	private static String workFolder;		// args[1]
	private static List<String> outputData = new ArrayList<String>();

	public static void processLine( String[] lineData, String originalLine ) {
		int qtd_aptos_tot =  Integer.valueOf( lineData[7].replace("\"", "") );
		int qtd_votos_nominais = Integer.valueOf( lineData[10].replace("\"", "") );
		float percent = ( qtd_votos_nominais * 100 ) / qtd_aptos_tot;
		
		outputData.add(originalLine + "," + percent );
		
		
	}
	
	
	private static void printOutput() throws Exception {
	    PrintWriter pw = new PrintWriter(new FileOutputStream( workFolder + "/sagi_output.txt") );
		for ( String line : outputData  ) {
	        pw.println( line );
		}
	    pw.close();
	}

	public static void main(String[] args) throws Exception {
		if ( args.length < 2 ) {
			System.out.println("Invalid argument list");
			System.exit(0);
		}
		
		inputFile = args[0];		// CSV input data file
		workFolder = args[1];		// Working folder 

		List<String> inputData = readFile( inputFile );
		
		if( inputData.size() > 0 ) {

			outputData.add( inputData.get(0) + ",porcentagem" );
			
			// If we have lines of data
			if( inputData.size() > 1 ) {
				// With each line of data...
				for( int x=1; x<inputData.size(); x++ ) {
					String[] lineData = inputData.get(x).split(",");
					// ... do something
					processLine( lineData, inputData.get(x) );
				}
			}
			
			printOutput();
		}
		
		
	}

	
	public static List<String> readFile(String file) throws Exception {
		Scanner s = new Scanner( new File(file) ).useDelimiter("\n");
		ArrayList<String> list = new ArrayList<String>();
		while (s.hasNext()){
			list.add(s.next());
		}
		s.close();
		return list;
	}
	
	
	
}
