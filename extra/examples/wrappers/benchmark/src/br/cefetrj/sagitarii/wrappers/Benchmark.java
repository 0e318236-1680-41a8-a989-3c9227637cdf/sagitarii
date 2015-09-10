package br.cefetrj.sagitarii.wrappers;

import cmabreu.sagitarii.sdk.Wrapper;


public class Benchmark {
	
	public static void main(String[] args) throws Exception{
		Processor myProcessor = new Processor();
		try {
			// Echo the input data
			Wrapper wrapper = new Wrapper("Benchmark", args[0], args[1], myProcessor );
			wrapper.process();
			wrapper.save();

			// Do some stress...
			new StressMaker().doStress();
			
			
		} catch ( Exception e ) {
			System.out.println("Wrapper execution error: " + e.getMessage() );
		}
	}

}


