package cmabreu.sagitarii.core.ssh;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.FileSystemFile;

public class SSHSession {
	private String alias;
	private int port = 22;
	private SSHClient ssh;
	private Session session;
	private int returnCode;
	private List<String> consoleOut;
	private List<String> consoleError;
	private String host;
	private String user;
	private boolean running = false;
	private String command;
	
	public boolean isRunning() {
		return running;
	}
	
	public String getUser() {
		return user;
	}
	
	public String getHost() {
		return host;
	}
	
	public SSHSession( String alias ) {
		this.alias = alias;
		consoleOut = new ArrayList<String>();
		consoleError = new ArrayList<String>();
	}
	
	
	public SSHSession( String alias, String host, String user, String password ) throws Exception {
		this.alias = alias;
		consoleOut = new ArrayList<String>();
		consoleError = new ArrayList<String>();
		connect( host, user, password );
	}
	
	
	public List<String> getConsoleError() {
		return consoleError;
	}
	
	public List<String> getConsoleOut() {
		return consoleOut;
	}
	
	public void disconnect() throws Exception {
		session.close();
		ssh.disconnect();
		ssh.close();
	}
	
	public void connect( String host, String user, String password ) throws Exception {
		this.host = host;
		this.user = user;
		ssh = new SSHClient();
		ssh.addHostKeyVerifier( new PromiscuousVerifier() );
		ssh.connect(host, port);
		ssh.authPassword(user, password);
		session = ssh.startSession();
	}

	public boolean isOperational() {
		return ( session.isOpen() && ssh.isConnected() );
	}

	public void upload( String file, String toPath ) throws Exception {
		 SFTPClient sftp = ssh.newSFTPClient();
		 try {
			 sftp.put( new FileSystemFile( file ), toPath );
		 } finally {
			 sftp.close();
		 }		
	}

	public void download( String remoteFile, String toLocalPath ) throws Exception {
		 SFTPClient sftp = ssh.newSFTPClient();
		 try {
			 sftp.get( remoteFile, new FileSystemFile(toLocalPath) );
		 } finally {
			 sftp.close();
		 }		
	}
	
	
	private void runInternal() {
		try {

			Command cmd = session.exec( command );
			cmd.join(5, TimeUnit.MINUTES);
			
			returnCode = cmd.getExitStatus();
			InputStream er = cmd.getErrorStream();
			BufferedReader brError = new BufferedReader( new InputStreamReader(er) );
			String line = null;
			consoleError.clear();
			while ( (line = brError.readLine() ) != null) {
				consoleError.add( line );
			}	
			
			InputStream is = cmd.getInputStream();
			BufferedReader br = new BufferedReader( new InputStreamReader(is) );
			line = null;
			consoleOut.clear();
			while ( (line = br.readLine() ) != null) {
				consoleOut.add( line );
			}	
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		running = false;
	};

	public void run( String command ) throws Exception {
		if ( running ) {
			throw new Exception("thread busy");
		}
		running = true;
		this.command = command;
		this.runInternal();
	}
	
	public int getReturnCode() {
		return returnCode;
	}
	
	public String getAlias() {
		return alias;
	}
	
}
