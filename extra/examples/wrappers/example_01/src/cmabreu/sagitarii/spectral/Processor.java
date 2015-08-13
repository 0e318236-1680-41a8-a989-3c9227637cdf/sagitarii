package cmabreu.sagitarii.spectral;

import java.util.ArrayList;
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
		
		String attr01 = ld.getData("attr01"); 
		String attr02 = ld.getData("attr02");
		String attr03 = ld.getData("attr03");
		
		// List<String> configFile = helper.readFromLibraryFolder( "myapp.config" );
		// helper.runExternal("/my/path/myapplication.jar");
		
		helper.copyFile(helper.getWorkFolder() + "sagi_input.txt", helper.getOutboxFolder() + "sourcedata.csv");

		ld.addValue("attr04", "sourcedata.csv");		
		csvLines.add( ld );
		
		System.out.println("Source Data: " + attr01 + "/" + attr02 + "/" + attr03);
		
		String str = "";
		double xxx = 0.0;
        try {
            // sleep for 1 second to simulate a remote call,
            // just waiting for the call to return
            Thread.sleep(4000);
            // loop that just concatenate a str to simulate
            // work on the result form remote call
            for (int index = 0; index < 20000; index++) {
            	xxx = 23 * 3 + index / 34 + Math.acos( xxx ) + Math.PI / 2;
                str = str + 't';
            }
            for (int index = 0; index < 20000; index++) {
            	xxx = 23 * 3 + index / 34 + Math.acos( xxx ) + Math.PI / 2;
                str = str + 't';
            }
            for (int index = 0; index < 20000; index++) {
            	xxx = 23 * 3 + index / 34 + Math.acos( xxx ) + Math.PI / 2;
                str = str + 't';
            }
        } catch (InterruptedException e) {
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
