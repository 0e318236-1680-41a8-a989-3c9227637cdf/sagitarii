package br.cefetrj.sagitarii.core.ssh;

import static net.sf.expectit.filter.Filters.removeColors;
import static net.sf.expectit.filter.Filters.removeNonPrintable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Shell;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.FileSystemFile;
import net.sf.expectit.Expect;
import net.sf.expectit.ExpectBuilder;
import net.sf.expectit.Result;
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
	private String host;
	private String user;
	private String machineName;
	private Expect expect;
	private Shell shell;
	
	public String getUser() {
		return user;
	}
	
	public String getHost() {
		return host;
	}
	
	public String getMachineName() {
		return machineName;
	}
	
	private void init(String alias, String host, String user, String password ) throws Exception {
		this.alias = alias;
		consoleOut = new ArrayList<String>();
		consoleError = new ArrayList<String>();
		lastCommands = new ArrayList<String>();
		connect( host, user, password );
	}
	
	public List<String> getLastCommands() {
		return lastCommands;
	}
	
	public SSHSession(String machineName, String alias, String host, int port, String user, String password ) throws Exception {
		this.port = port;
		this.machineName = machineName;
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
                .withTimeout(3, TimeUnit.SECONDS)
                .build();
        
        String result = "";
        try {
        	result = expect.expect( Matchers.contains( "hahaoops" ) ).getInput();
        } catch ( Exception e ) {
        	result = e.getMessage();
        }
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
	
	public String run( String command, boolean hide ) throws Exception {

		if ( (command == null) || command.equals("") ){
			throw new Exception("invalid command");
		}

		/*
		if ( command.toLowerCase().equals("logout") || command.toLowerCase().equals("exit") ) {
			disconnect();
			consoleOut.add( "disconnected." );
			return "disconnected.";
		}
		*/
		
		consoleOut.clear();
    	expect.sendLine( command );
    	String resultString = "";
    	Result result;
    	try {
    		result = expect.expect( Matchers.contains("hahaoops") );
    		resultString = result.getInput();
    	} catch ( Exception e ) {
    		resultString = e.getMessage();
    	}
    	
    	consoleOut.add( resultString );
    	
    	if ( !hide ) {
	    	lastCommands.add( command );
	    	if ( lastCommands.size() > 25 ) {
	    		lastCommands.remove(0);
	    	}
    	}
    	
    	return resultString;
	}
	
	public int getReturnCode() {
		return returnCode;
	}
	
	public String getAlias() {
		return alias;
	}
	
}
