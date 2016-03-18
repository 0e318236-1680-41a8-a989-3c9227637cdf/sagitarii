package br.cefetrj.sagitarii.wrappers;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cmabreu.sagitarii.sdk.IWrapperProcessor;
import cmabreu.sagitarii.sdk.LineData;
import cmabreu.sagitarii.sdk.WrapperHelper;

public class Processor implements IWrapperProcessor {
	private List<String> outputData;
	private List<LineData> csvLines;

	
	public Processor() {
		outputData = new ArrayList<String>();
		csvLines = new ArrayList<LineData>();
	}
	
	
	@Override
	public List<String> onNeedOutputData() {
		return outputData;
	}

	
	@Override
	public void processLine( LineData ld, WrapperHelper helper ) throws Exception {
		
		// How to get data from the input CSV
		String attr01 = ld.getData("attr01"); 
		String attr02 = ld.getData("attr02");
		String attr03 = ld.getData("attr03");
		
		// How to access the wrappers library folder and read a config file
		// List<String> configFile = helper.readFromLibraryFolder( "myapp.config" );
		
		// How to run external applications 
		//helper.runExternal("/my/path/myapplication.jar");
		
       	//helper.copyFile(helper.getWorkFolder() + "sagi_input.txt", helper.getOutboxFolder() + "sourcedata.csv");
		
		System.out.println("Will write the random data:");
		PrintWriter writer = new PrintWriter( helper.getOutboxFolder() + "sourcedata.csv" , "UTF-8");

		//for ( int y=0; y<20; y++ ) {
			for ( int x =0; x <= 350000; x++ ) {
				writer.println( attr01 + " " + attr02 + " " + attr03 + "This is a line of data: How to add a new " +
									"column to the output CSV ( remember this column must exists in output table ) " + 
									Calendar.getInstance().getTimeInMillis() );
			}
		//}
		writer.close();
		System.out.println("Output file created with random data.");
		
		// How to move files
		// helper.moveFile(source, target);

		// How to add a new column to the output CSV ( remember this column must exists in output table )
		ld.addValue("attr04", "sourcedata.csv");		
		csvLines.add( ld );
		
		System.out.println("Source Data: " + attr01 + "/" + attr02 + "/" + attr03);
		
	}

	
	@Override
	public void onProcessFinish() {
		
		System.out.println("done.");

		
		// Get the first data just to create the header. Can be anyone since 
		// the header is present in all items
		// In most cases this method can left untouched because it can
		// be used by MAP (1:1), REDUCE (n:1) or SPLIT (1:n) operators.
		if ( csvLines.size() > 0 ) {
			outputData.add( csvLines.get(0).getCsvHeader() );	
			for ( LineData ld : csvLines ) {
				outputData.add( ld.getCsvLine() );
			}
		} else {
			System.out.println("No data for output");
		}
	}

	
	@Override
	public void onProcessBeforeStart( LineData headerData ) {
		System.out.println("starting...");
	}


}
