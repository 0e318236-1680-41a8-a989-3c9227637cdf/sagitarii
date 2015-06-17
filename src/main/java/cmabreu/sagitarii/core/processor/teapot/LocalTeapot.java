package cmabreu.sagitarii.core.processor.teapot;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmabreu.sagitarii.core.config.Configurator;


public class LocalTeapot extends Thread  {
	private Logger logger = LogManager.getLogger( this.getClass().getName() ); 
	private LocalTeapotLoader loader;
	
	public LocalTeapot( LocalTeapotLoader loader ) {
		this.loader = loader;
	}
	
	@Override
	public void run() {
		Process process = null;
        try {
        	process = Runtime.getRuntime().exec(  "java -jar " + Configurator.getInstance().getLocalTeapotFolder() + "/teapot.jar",
        			null, new File( Configurator.getInstance().getLocalTeapotFolder() ) );
        	
        	InputStream in = process.getInputStream(); 
        	BufferedReader br = new BufferedReader( new InputStreamReader(in) );
        	String line = null;
        	while( ( line=br.readLine() )!=null ) {
        		logger.debug( line );
        	}        	
            process.waitFor();
        } catch ( Exception e ) {
        	logger.error( e.getMessage() );
        }
        
        loader.notifyFinish();
	}
	


}
