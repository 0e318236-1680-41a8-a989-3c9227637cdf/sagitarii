package perfil_eleitorado;

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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// perfil_eleitorado ( no pipeline csv )
// 0.grau_de_escolaridade; 1.uf; 2.sexo; 3.faixa_etaria; 4.cod_municipio_tse; 5.qtd_eleitores_no_perfil;
// 6.nr_zona; 7.municipio; 8.periodo

// perfil_eleitorado_proj ( no banco de dados )
// 1.uf, 2.sexo, 3.faixa_etaria, 5.qtd_eleitores_no_perfil, 6.nr_zona


public class Projection {
	// To hold parameters...
	private static String inputFile;		// args[0]
	private static String workFolder;		// args[1]

	// You may want to set what colums you're interested
	// So you can match columns and lines with easy to create your
	// own output data
	private static int[] columnsWeWant = {1,3,5,6};
	// The output CSV data holder ( index 0 = header ) 
	private static List<String> outputData = new ArrayList<String>();
	// The output folder if passed as in optional parameter ( arg1 )
	private static String outputFolder;
	// The output file name if passed as in optional parameter ( arg2 ) without extension
	private static String outputFileName;

	/**
	 * Process the columns
	 * @param columns : Array of strings.
	 *
	 * Each index of this array will match the index of lines array
	 * 
	 */
	public static void processColumns( String[] columns ) {
		String prefix = "";
		StringBuilder sb = new StringBuilder();
		for( int colIndex : columnsWeWant ) {
			sb.append(prefix);
			prefix = ",";
			String data = columns[colIndex];
			if ( data.equals("") ) {
				data = "-1";
			}
			sb.append( data );
		}
		outputData.add( 0, sb.toString() );
	}

	/**
	 * Process the lines
	 * @param columns : Array of string
	 * 
	 * Each index of this array will match the index of columns array
	 * 
	 */
	public static void processLine( String[] lineData ) {
		String prefix = "";
		StringBuilder sb = new StringBuilder();
		for( int colIndex : columnsWeWant ) {
			sb.append(prefix);
			prefix = ",";
			String data = lineData[colIndex];
			if ( data.equals("") ) {
				data = "-1";
			}
			sb.append( data );
		}
		outputData.add( sb.toString() );
	}
	
	
	/**
	 * Print out the outputData content to screen 
	 * ( send to Teapot by using standard out )
	 */
	private static void printOutput() {
		for ( String line : outputData  ) {
			System.out.println( line );
		}
	}
	

	/**
	 * Entrance point
	 * 
	 * @param args Array of String with parameters passed by Sagitarii Teapot
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if ( args.length == 0 ) {
			System.out.println("Invalid argument list");
		}
		
		inputFile = args[0];		// CSV input data file
		workFolder = args[1];		// Working folder 

		// Read the CSV input data file
		List<String> inputData = readFile( inputFile );
		// If we have data...
		if( inputData.size() > 0 ) {
			// Get the first line : the columns
			String header = inputData.get(0);
			String[] columns = header.split(",");
			// Do something with the columns
			processColumns( columns );
			// If we have lines of data
			if( inputData.size() > 1 ) {
				// With each line of data...
				for( int x=1; x<inputData.size(); x++ ) {
					String[] lineData = inputData.get(x).split(",");
					// ... do something
					processLine( lineData );
				}
			}
			
			// Print out the outputData content to screen ( send to Teapot by using standard out )
			printOutput();
			
		} else {
			System.out.println("Empty input data file.");
		}
		
		
		// Spend some time doing nothing ... you lazy boy ;-)
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt( 1000 ) + 1000;
		try {
		    Thread.sleep( randomInt );
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}			

	}

	
	/**
	 * This is a method to read the CSV data 
	 * @param file
	 * @return StringBuilder : The file data as a list of lines.
	 * @throws Exception
	 */
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
