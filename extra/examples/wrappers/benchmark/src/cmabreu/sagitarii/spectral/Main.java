package cmabreu.sagitarii.spectral;

import cmabreu.sagitarii.sdk.Wrapper;


public class Main {
	
	public static void main(String[] args) throws Exception{
		Processor myProcessor = new Processor();
		try {
			Wrapper wrapper = new Wrapper("Echo Wrapper", args[0], args[1], myProcessor );
			wrapper.process();
			wrapper.save();
		} catch ( Exception e ) {
			System.out.println("Wrapper execution error: " + e.getMessage() );
		}
	}

}


