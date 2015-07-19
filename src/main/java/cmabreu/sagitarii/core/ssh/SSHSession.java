package cmabreu.sagitarii.core.ssh;

import static net.sf.expectit.filter.Filters.removeColors;
import static net.sf.expectit.filter.Filters.removeNonPrintable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cmabreu.sagitarii.core.config.Configurator;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Shell;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.FileSystemFile;
import net.sf.expectit.Expect;
import net.sf.expectit.ExpectBuilder;
import net.sf.expectit.matcher.Matchers;

public class SSHSession {
	private String alias;
	private int port = 22;
	private SSHClient ssh;
	private Session session;
	private int returnCode;
	private List<String> consoleOut;
	private List<String> consoleError;
	private List<String> lastCommands;
	private List<String> notAllowed;
	private String host;
	private String user;
	private String password;
	private boolean running = false;
	private Expect expect;
	private Shell shell;
	private String PROMPT;
	private boolean sudo = false;
	
	public boolean isRunning() {
		return running;
	}
	
	public boolean isSudo() {
		return sudo;
	}
	
	public String getUser() {
		return user;
	}
	
	public String getHost() {
		return host;
	}
	
	private void init(String alias, String host, String user, String password ) throws Exception {
		this.alias = alias;
		consoleOut = new ArrayList<String>();
		consoleError = new ArrayList<String>();
		lastCommands = new ArrayList<String>();
		notAllowed = new ArrayList<String>();
		
		notAllowed.add("sudo");
		notAllowed.add("vim");
		
		PROMPT =  Configurator.getInstance().getUserPrompt( user ); // user + "@";
		
		connect( host, user, password );
	}
	
	public List<String> getLastCommands() {
		return lastCommands;
	}
	
	public SSHSession( String alias, String host, int port, String user, String password ) throws Exception {
		this.port = port;
		init( alias, host, user, password );
	}
	
	public List<String> getConsoleError() {
		return consoleError;
	}
	
	public List<String> getConsoleOut() {
		return consoleOut;
	}
	
	public void disconnect() throws Exception {
		expect.close();
		shell.close();
		session.close();
		ssh.disconnect();
		ssh.close();
	}
	
	public void connect( String host, String user, String password ) throws Exception {
		this.host = host;
		this.user = user;
		this.password = password;
		ssh = new SSHClient();
		ssh.addHostKeyVerifier( new PromiscuousVerifier() );
		ssh.connect(host, port);
		ssh.authPassword(user, password);
		session = ssh.startSession();
		
        session.allocateDefaultPTY();
        shell = session.startShell();
        
        expect = new ExpectBuilder()
                .withOutput(shell.getOutputStream())
                .withInputs(shell.getInputStream(), shell.getErrorStream())
                .withInputFilters(removeColors(), removeNonPrintable())
                .withExceptionOnFailure()
                .withTimeout(5, TimeUnit.MINUTES)
                .build();
        
    	String result = expect.expect( Matchers.contains( PROMPT ) ).getInput();
    	consoleOut.add( result );
    	
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
	
	private String sudo() throws Exception {
    	consoleOut.clear();
    	expect.sendLine( "sudo -i" );
    	expect.expect( Matchers.contains( ":" ) );
    	expect.sendLine( password );
    	
    	PROMPT = Configurator.getInstance().getRootPrompt( user ); //user + ":";
    	
    	String result = expect.expect( Matchers.contains( PROMPT ) ).getInput();
    	
    	consoleOut.add( result );
    	sudo = true;
    	return result;
		
	}
	
	public String run( String command ) throws Exception {
		if ( running ) {
			throw new Exception("thread busy");
		}
		consoleOut.clear();
		
		if ( command.equals("sudo") ) {
			return sudo();
		}
		
		if ( command.toLowerCase().equals("logout") || command.toLowerCase().equals("exit") ) {
			disconnect();
			consoleOut.add( "disconnected." );
			return "disconnected.";
		}

		if ( (command == null) || command.equals("") || ( command.length() < 2 ) ){
			throw new Exception("invalid command: " + command);
		}
		
		for( String notRun : notAllowed ) {
			if ( command.toLowerCase().contains( notRun.toLowerCase() ) ) {
				throw new Exception("Command not allowed: " + notRun );
			}
		}
		
		running = true;
		
    	expect.sendLine( command );
    	String result = expect.expect( Matchers.contains( PROMPT ) ).getInput();
    	
    	consoleOut.add( result );
    	running = false;
    	lastCommands.add( command );
    	if ( lastCommands.size() > 25 ) {
    		lastCommands.remove(0);
    	}
    	return result;
	}
	
	public int getReturnCode() {
		return returnCode;
	}
	
	public String getAlias() {
		return alias;
	}
	
}
