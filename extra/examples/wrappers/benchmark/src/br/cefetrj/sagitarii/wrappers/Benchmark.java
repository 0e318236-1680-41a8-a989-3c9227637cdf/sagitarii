package br.cefetrj.sagitarii.wrappers;

import cmabreu.sagitarii.sdk.Wrapper;


public class Benchmark {
	
	public static void main(String[] args) throws Exception{
		Processor myProcessor = new Processor();
		try {
			Wrapper wrapper = new Wrapper("Benchmark", args[0], args[1], myProcessor );
			wrapper.process();
			wrapper.save();

			// Spend some time to simulate processing...
	        try {
	            Thread.sleep(20000);
	        } catch (InterruptedException e) {
	        }
			
			
		} catch ( Exception e ) {
			System.out.println("Wrapper execution error: " + e.getMessage() );
		}
	}

}


