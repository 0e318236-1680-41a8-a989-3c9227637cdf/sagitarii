package cmabreu.sagitarii.core.ssh;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SSHSessionManager {
	private List<SSHSession> sessions;
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
	private static SSHSessionManager instance;
	private List<String> lastMultiCommands;

	public List<String> getLastMultiCommands() {
		return lastMultiCommands;
	}
	
	public static SSHSessionManager getInstance() {
		if ( instance == null ) {
			instance = new SSHSessionManager();
		}
		return instance;
	}
	
	private SSHSessionManager() {
		sessions = new ArrayList<SSHSession>();
		lastMultiCommands = new ArrayList<String>();
	}
	
	public List<SSHSession> getSessions() {
		return new ArrayList<SSHSession>(sessions);
	}
	
	public SSHSession getSession( String alias ) throws Exception {
		cleanUp();
		logger.debug("get session " + alias );
		for ( SSHSession session : getSessions() ) {
			if ( session.getAlias().equals(alias) ) {
				logger.debug("session connected to " + session.getHost() );
				return session;
			}
		}
		logger.debug("session " + alias + " not found");
		return null;
	}

	public SSHSession getSessionByHost( String host ) {
		logger.debug("get session by host " + host );
		for ( SSHSession session : getSessions() ) {
			if ( session.getHost().equals( host ) ) {
				logger.debug("session connected as " + session.getAlias() );
				return session;
			}
		}
		logger.debug("session to host " + host + " not found");
		return null;
	}
	
	public List<String> getConsoleOutput( String alias ) throws Exception {
		SSHSession session =  getSession(alias);
		if ( session != null ) {
			return session.getConsoleOut();
		}
		return new ArrayList<String>();
	}
	
	public List<String> getConsoleError( String alias ) throws Exception {
		SSHSession session =  getSession(alias);
		if ( session != null ) {
			return session.getConsoleError();
		}
		return new ArrayList<String>();
	}

	public void multipleUpload( String file, String toPath ) throws Exception {
		// TODO: Must be in a Thread !!!!!
		logger.debug("uploading file to all sessions");
		for ( SSHSession session : getSessions() ) {
			if ( session.isOperational() ) {
				logger.debug(" > " + session.getAlias() );
				session.upload(file, toPath);
			}
		}
	}

	public void multipleRun( String command ) throws Exception {
		logger.debug("sending command to all sessions");
		for ( SSHSession session : getSessions() ) {
			if ( session.isOperational() ) {
				logger.debug(" > " + session.getAlias() );
				session.run( command );
			}
		}
		lastMultiCommands.add( command );
		if ( lastMultiCommands.size() > 25 ) {
			lastMultiCommands.remove(0);
		}
	}

	public void upload( String alias, String file, String toPath ) throws Exception {
		SSHSession session =  getSession(alias);
		if ( session != null ) {
			// TODO: Must be in a Thread !!!!!
			if ( session.isOperational() ) {
				session.upload( file, toPath );
			}
		}
	}
	
	public void download( String alias, String remoteFile, String toLocalPath ) throws Exception {
		SSHSession session =  getSession(alias);
		if ( session != null ) {
			// TODO: Must be in a Thread !!!!!
			if ( session.isOperational() ) {
				session.download( remoteFile, toLocalPath );
			}
		}
	}
	
	public void run( String alias, String command ) throws Exception {
		SSHSession session =  getSession(alias);
		if ( session != null ) {
			if ( session.isOperational() ) {
				session.run( command );
			}
		}
	}
	
	public void cleanUp() {
		for ( SSHSession session : getSessions()  ) {
			if ( !session.isOperational() ) {
				sessions.remove( session );
			}
		}
	}
	
	public SSHSession newSession( String alias, String host, int port, String user, String password ) throws Exception {
		SSHSession sess = getSessionByHost(host);
    	if ( sess != null ) {
    		throw new Exception("already connected to " + host + " as '" + sess.getAlias() + "'");
    	}
		sess = new SSHSession( alias, host, port, user, password );
		sessions.add( sess );
		return sess;
	}
	
}
