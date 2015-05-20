package consolida_dados;

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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
// 0.uf;1.sexo;2.qtd_comparecimento;3.faixa_etaria;4.qtd_eleitores_no_perfil;5.nr_zona;6.qtd_aptos;7.numero_zona;8.qtd_votos_nominais
// BA;MASCULINO;95108;25 A 34 ANOS;490;59;121381;59;82568
// BA;MASCULINO;80868;60 A 69 ANOS;492;16;102560;16;73238
// BA;MASCULINO;84956;60 A 69 ANOS;498;9;105075;9;76934
// BA;MASCULINO;94504;SUPERIOR A 79 ANOS;490;84;116171;84;79691

public class ConsolidaDados {
	// To hold parameters...
	private static String inputFile;		// args[0]
	private static String workFolder;		// args[1]
	private static String hostUrl;			// args[2]
	private static String experimentSerial;	// args[3]
	// You may want to set what colums you're interested
	// So you can match columns and lines with easy to create your
	// own output data
	private static int[] columnsWeWant = {0,1,2,3,4};
	// The output CSV data holder ( index 0 = header ) 
	private static List<String> outputData = new ArrayList<String>();
	// The output folder if passed as in optional parameter ( arg1 )
	
	
	private static int count = 0;
	
	/**
	 * Process the header
	 * @param columns : Array of strings.
	 *
	 * Each index of this array will match the index of lines array
	 * 
	 */
	public static void processHeader( String[] columns ) {
		String header = "num_registers,filename";
		outputData.add( 0, header );
	}

	/**
	 * Process the lines
	 * @param columns : Array of string
	 * 
	 * Each index of this array will match the index of columns array
	 * 
	 */
	public static String processLine( String[] lineData ) throws Exception{
	
		String fileName = lineData[0] + "-" + lineData[1] + "_" + count + ".txt";
		count++;
		String content = "qtd_comparecimento\t\t\t" + lineData[2] + "\n" + 
		"qtd_aptos\t\t\t" + lineData[6] + "\n" + "qtd_votos_nominais\t\t\t" + lineData[8];
		
		saveFile( content, workFolder + "/" + fileName );
		
		return fileName;
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
	 * Compress files
	 * 
	 * @param files
	 * @param zipTargetFile
	 * @throws Exception
	 */
	public static void reduceFiles( List<String> files, String zipTargetFile ) throws Exception {
		byte[] buffer = new byte[1024];
		FileOutputStream fos = new FileOutputStream( zipTargetFile );
		ZipOutputStream zos = new ZipOutputStream(fos);
    	for( String file : files ){
    		Path p = Paths.get( file );
    		String fileEntry = p.getFileName().toString();
    		ZipEntry ze= new ZipEntry( fileEntry );
        	zos.putNextEntry(ze);
        	FileInputStream in = new FileInputStream( file );
        	int len;
        	while ((len = in.read(buffer)) > 0) {
        		zos.write(buffer, 0, len);
        	}
        	in.close();
    	}		
		zos.closeEntry();
		zos.close();
	}

	
	private static void saveFile( String content, String fileName ) throws Exception {
		FileWriter writer = new FileWriter( fileName ); 
		writer.write( content );
		writer.close();
	}
	
	public static void main(String[] args) throws Exception {
		inputFile = args[0];		// CSV input data file
		workFolder = args[1];		// Working folder 
		hostUrl = args[2];			// Sagitarii host URL
		experimentSerial = args[3];	// Experiment tag ID

		// Read the CSV input data file
		List<String> inputData = readFile( inputFile );
		// If we have data...
		if( inputData.size() > 0 ) {
			// Get the first line : the columns
			String header = inputData.get(0);
			String[] columns = header.split(",");
			// Do something with the columns
			processHeader( columns );
			// If we have lines of data
			if( inputData.size() > 1 ) {
				// With each line of data...
				List<String> files = new ArrayList<String>();
				for( int x=1; x<inputData.size(); x++ ) {
					String[] lineData = inputData.get(x).split(",");
					// ... do something
					String fileName = processLine( lineData );
					String fullFileName = workFolder + "/" + fileName;
					files.add(fullFileName);
				}
				outputData.add( inputData.size()-1 + "," + experimentSerial + ".zip"); 	// -1 for the header line
				reduceFiles(files, workFolder + "/outbox/" + experimentSerial + ".zip" );		// Aways use Task ID to avoid conflicts
			}
			
			// Print out the outputData content to screen ( send to Teapot by using standard out )
			printOutput();
			
		} else {
			System.out.println("Empty input data file.");
		}
		
	}
	
	/**
	 * This is a method to read the CSV data 
	 * @param file
	 * @return List<String> : The file data as a list of lines.
	 * @throws Exception
	 */
	public static List<String> readFile(String file) throws Exception {
		String line = "";
		ArrayList<String> list = new ArrayList<String>();
		BufferedReader br = new BufferedReader( new FileReader( file ) );
		if (br != null) {
			while ( (line = br.readLine() ) != null ) {
			    list.add( line );
			}
			br.close();
		}		
		return list;
	}

}
