package cmabreu.sagitarii;

import cmabreu.sagitarii.core.ssh.SSHSession;

public class Test {

	public static void main(String[] args) {

		try {
    		SSHSession sess = new SSHSession( "alias", "10.5.112.214", "sadlog", "sadlog");
    		//String toLocalPath = "d:/mydownloaded.jar";
    		//String remoteFile = "/srv/www/htdocs/tree/teapot-1.0.125.jar";
    		//sess.download(remoteFile, toLocalPath);
    		
    		sess.run("ls /root");
    		
    		while( sess.isRunning() ) {
    			System.out.println( "waiting..." );
    		}
    		
    		for ( String line : sess.getConsoleOut() ) {
    			System.out.println( line );
    		}

    		
    		System.out.println( "---------- ");
    		for ( String line : sess.getConsoleError() ) {
    			System.out.println( line );
    		}
    		
    		sess.disconnect();
    		System.out.println("done");
    	} catch ( Exception e ) {
    		e.printStackTrace();
    	}

	}

}
