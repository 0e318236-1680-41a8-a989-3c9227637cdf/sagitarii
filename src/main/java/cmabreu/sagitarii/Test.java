package cmabreu.sagitarii;

import cmabreu.sagitarii.core.ssh.SSHSession;
import cmabreu.sagitarii.core.ssh.SSHSessionManager;

public class Test {

	public static void main(String[] args) {

		try {
			
			SSHSessionManager mngr = SSHSessionManager.getInstance();
			SSHSession sess = mngr.newSession( "alias", "eic.cefet-rj.br", 8091, "sagitarii", "Chiron2014!" );

			System.out.println( mngr.getSessions().size() );
			
			SSHSession sess2 = mngr.getSession("alias");
			System.out.println( sess2.getHost() );
			
    		//String toLocalPath = "d:/mydownloaded.jar";
    		//String remoteFile = "/srv/www/htdocs/tree/teapot-1.0.125.jar";
    		//sess.download(remoteFile, toLocalPath);
    		
    		String result = sess.run("ls -lh");
    		
    		System.out.println( result );
    		
    		//sess.disconnect();
    		System.out.println("done");
    	} catch ( Exception e ) {
    		e.printStackTrace();
    	}

	}

}
