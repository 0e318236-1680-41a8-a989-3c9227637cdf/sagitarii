package cmabreu.sagitarii.spectral;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
		String maxDegree = ld.getData("maxdegree"); 
		String minDegree = ld.getData("mindegree");
		String order = ld.getData("gorder");
		
		List<String> configFile = helper.readFromLibraryFolder( "spectral.config" );
		if ( configFile.size() > 0 ) {
			String libraryDirectory = configFile.get(0);
			
			String degreeOptions = " -d" + minDegree + " -D" + maxDegree;
			
			String fileName = UUID.randomUUID().toString().toUpperCase().substring(0,8) + "_" + order + ".g6";

			String gengOutput = helper.getWorkFolder() + fileName;
			String geng = libraryDirectory + "/geng " + degreeOptions + " -g -q " + order + " " + gengOutput;

			helper.runExternal(geng);

			// Send back original data plus file name
			ld.addValue("g6file", fileName);		
			csvLines.add( ld );
			
		} else {
			System.out.println("Config file 'spectral.config' not found at library folder.");
		}
		
	}

	
	@Override
	public void onProcessFinish() {
		// Get the first data just to create the header. Can be anyone since 
		// the header is present in all items
		// In most cases this method can left untouched because it can
		// be used by MAP (1:1), REDUCE (n:1) or SPLIT (1:n) operator.
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
		//
	}


}
