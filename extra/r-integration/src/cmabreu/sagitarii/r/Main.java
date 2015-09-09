package br.cefetrj.sagitarii.r;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

public class Main {
	private static String rscript;			// args[0]
	private static String workFolder;		// args[1]
	private static String libraryFolder;	// args[2]

	public static void main(String[] args) {
		rscript = args[0];			// R Script to run
		workFolder = args[1];		// Working folder 
		libraryFolder = args[2];	// Wrappers / Library folder 
		
		TextConsole console = new TextConsole();
		
		Rengine rengine = new Rengine(new String [] {"--vanilla"}, false, console );

        if ( !rengine.waitForR() ) {
            System.out.println("Cannot load R");
            System.exit(1);
        }

    	System.out.println("Sagitarii R Wrapper v1.0              04/07/2015");
    	System.out.println("Carlos Magno Abreu        magno.mabreu@gmail.com");
		System.out.println("------------------------------------------------");
        System.out.println(rscript);
		System.out.println("------------------------------------------------");
        
        rengine.eval("sagitariiWorkFolder <- \""+ workFolder +"\"");
        rengine.eval("libraryFolder <- \""+ libraryFolder +"\"");
        rengine.eval("messageToSagitarii <- \"\"");
        
        console.enableLog();
        rengine.eval( "source( '" + rscript + "') " );
        
        REXP message = rengine.eval("messageToSagitarii");
        if ( message != null ) {
        	System.out.println( message.asString() );
        }
        
        rengine.end();

		System.out.println("------------------------------------------------");
        System.out.println("R Script done.");
        
	}

}
