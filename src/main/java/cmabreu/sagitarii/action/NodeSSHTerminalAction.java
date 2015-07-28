
package cmabreu.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.core.Cluster;
import cmabreu.sagitarii.core.ClustersManager;
import cmabreu.sagitarii.core.ssh.SSHSession;
import cmabreu.sagitarii.core.ssh.SSHSessionManager;

@Action (value = "nodeSSHTerminal", results = { @Result (location = "nodeSSHTerminal.jsp", name = "ok") 
}, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 

@ParentPackage("default")
public class NodeSSHTerminalAction extends BasicActionClass {
	private String macAddress;
	private Cluster cluster;
	private String user;
	private String password;
	private SSHSession session;
	private String command;
	private int port;
	private String hideCommand;
	
	public String execute () {
		ClustersManager cm = ClustersManager.getInstance();
		cluster = cm.getCluster( macAddress );
		
		if ( cluster == null ) {
			setMessageText("Node " + macAddress + " not found");
			return "ok";
		}
		
		SSHSessionManager mngr = SSHSessionManager.getInstance();

		try {
			session = mngr.getSession( cluster.getMacAddress() );
			if ( session == null ) {
				
				//No command / No login : Just entering the screen 
				if (  (user == null) && (password == null) && (command == null) ) {
					return "ok";
				}				
				
				if ( (user != null) && (password != null)  ) {
					String host = cluster.getIpAddress();

					
					//session = mngr.newSession( cluster.getMachineName(), cluster.getMacAddress(), host, port, user, password );
					session = mngr.newSession( "SADLOG", cluster.getMacAddress(), "10.5.112.214", 22, "root", "sadlog" );

					
					
				} else {
					setMessageText("user and password must be set");						
				}
			} else {
				//
			}
			
			if ( session != null ) { // NOT "ELSE" because getSession or newSession
				if ( command != null && !command.equals("") ) {
					boolean hide = ( hideCommand != null && hideCommand.equals("on") ); 
					session.run( command, hide );
				}

			} else {
				setMessageText("session not found");
			}
    		

		} catch ( Exception e ) {
			setMessageText("Error: " + e.getMessage() );
		}
			
		return "ok";
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public SSHSession getSession() {
		return session;
	}
	
	public Cluster getCluster() {
		return cluster;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public void setHideCommand(String hideCommand) {
		this.hideCommand = hideCommand;
	}
	
}
