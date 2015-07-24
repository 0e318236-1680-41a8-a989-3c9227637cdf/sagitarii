package cmabreu.sagitarii.sdk;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * This class is the Wrapper engine.
 * The Wrapper engine encapsulates all complex Sagitarii-related stuffs
 * so you can concentrate in you own job.
 * 
 * @author Carlos Magno O. Abreu : magno.mabreu@gmail.com
 */
public class Wrapper {
	private String workFolder; 
	private String wrapperFolder; 
	private IWrapperProcessor processor;
	private String wrapperAlias;
	private WrapperHelper helper;
	
	/**
	 * Constructor.
	 * You must pass an alias short name ( 15 characters length ) to this wrapper.
	 * The workFolder and wrapperFolder are the same passed at args[0] and args[1] from Teapot to your wrapper class.
	 * Processor is your own implementation of {@link IWrapperProcessor}.
	 * processLine() will be called line by line when you call process() 
	 *  
	 */
	public Wrapper( String wrapperAlias, String workFolder, String wrapperFolder, IWrapperProcessor processor ) throws Exception {
		if ( wrapperAlias == null ) {
			wrapperAlias = "NULL_NAME";
		}
		if ( wrapperAlias.length() > 15 ) {
			wrapperAlias = wrapperAlias.substring(0, 14);
		}
		System.out.println("Sagitarii SDK                      23/07/2015");
    	System.out.println("Carlos Magno Abreu     magno.mabreu@gmail.com");
		System.out.println("---------------------------------------------");
		System.out.println("");
		System.out.println( "[" + wrapperAlias + "] Init");
		this.workFolder = workFolder;
		this.wrapperFolder = wrapperFolder;
		this.processor = processor;
		this.wrapperAlias = wrapperAlias;
		this.helper = new WrapperHelper( wrapperAlias, wrapperFolder, workFolder);
	}

	/**
	 * The wrapper folder
	 * 
	 */
	public String getWrapperFolder() {
		return wrapperFolder;
	}
	
	/**
	 *	Execute the {@link IWrapperProcessor} implementation and pass the input CSV line by line (with header) to it.
	 *	To get some data, use:
	 *
	 *  String myData = record.get("your_column_name");
	 *  
	 *  Don't forget to save your output data to the output List.
	 *  Your processor MUST implement a List<String> to hold the output data.
	 *  When you call save(), this class will call getOutputData() from your
	 *  processor to get the data to send to Sagitarii.
	 *  
	 */
	public void process() throws Exception {
		System.out.println( "[" + wrapperAlias + "] Loading input ");
		try {
			Reader in = new FileReader( workFolder + "/sagi_input.txt" );
			CSVParser records = CSVFormat.RFC4180
					.withHeader()
					.withNullString("\"\"")
					.withIgnoreSurroundingSpaces()
					.withAllowMissingColumnNames(false)
					.withIgnoreEmptyLines()
					.withDelimiter(',')
					.withQuote('"')
					.parse(in);
			
			Map<String, Integer> headerMap = records.getHeaderMap();
			LineData headerData = new LineData();
			for (String columnName : headerMap.keySet() ) {
				headerData.addDataItem( new DataItem(columnName, columnName, headerMap.get(columnName) ) );
			}
			
			System.out.println( "[" + wrapperAlias + "] Calling Before Start Event");
			processor.onProcessBeforeStart( headerData );
			
			System.out.println( "[" + wrapperAlias + "] Starting CSV input data process");
			for (CSVRecord record : records) {
				Map<String,String> rMap = record.toMap();
	
				LineData ld = new LineData();
				for (String columnName : rMap.keySet() ) {
					String data = rMap.get(columnName);
					Integer index = headerMap.get(columnName);
					ld.addDataItem( new DataItem(columnName, data, index) );
				}
				
			    processor.processLine( ld, helper );
			}
			
		    in.close();
		} catch ( Exception e ) {
			System.out.println( "[" + wrapperAlias + "] Error processing input data: " + e.getMessage() );
			throw e;
		}
		System.out.println( "[" + wrapperAlias + "] Calling Process Finish Event");
		processor.onProcessFinish();
		System.out.println( "[" + wrapperAlias + "] All done.");
	}
	
	/**
	 *	Save the output data.
	 *	This data must be provided by the Wrapper processor.  
	 */
	public void save() throws Exception {
		System.out.println( "[" + wrapperAlias + "] Save output data");
		
		List<String> outputData = processor.onNeedOutputData(); 
		
		if ( (outputData == null) || (outputData.size() == 0) ) {
			System.out.println( "[" + wrapperAlias + "] Empty output data");
			throw new Exception("no output data to save");
		}
		
	    PrintWriter pw = new PrintWriter( new FileOutputStream( workFolder + "/sagi_output.txt"  ) );
	    for ( String line : outputData ) {
	    	if ( ( line != null ) && ( !line.equals("") ) ) {
	    		pw.println( line );
	    	}
	    }
	    pw.close();
	    
		System.out.println( "[" + wrapperAlias + "] Saved " + outputData.size() + " lines of output data");
	}
	
	
}
