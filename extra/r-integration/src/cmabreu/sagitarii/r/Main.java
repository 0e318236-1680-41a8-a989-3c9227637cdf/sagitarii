package cmabreu.sagitarii.r;

import org.rosuda.JRI.Rengine;

public class Main {
	private static String rscript;			// args[0]
	private static String workFolder;		// args[2]

	public static void main(String[] args) {
		// Teapot will give you these parameters:
		rscript = args[0];			// R Script to run
		workFolder = args[1];		// Working folder 
		
		Rengine rengine = new Rengine(new String [] {"--vanilla"}, false, new TextConsole() );
		
        if ( !rengine.waitForR() ) {
            System.out.println("Cannot load R");
            System.exit(1);
        }

        // Send the working folder to user's R script.
        rengine.eval("sagitariiWorkFolder <- \""+ workFolder +"\"");
        rengine.eval( "source( '" + rscript + "') " );
        
        rengine.end();
        
	}

}
