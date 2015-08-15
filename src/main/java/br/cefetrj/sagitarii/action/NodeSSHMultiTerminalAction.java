
package br.cefetrj.sagitarii.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.core.ssh.SSHSession;
import br.cefetrj.sagitarii.core.ssh.SSHSessionManager;

@Action (value = "nodeSSHMultiTerminal", results = { @Result (location = "nodeSSHMultiTerminal.jsp", name = "ok") 
}, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 

@ParentPackage("default")
public class NodeSSHMultiTerminalAction extends BasicActionClass {
	private String command;
	private List<SSHSession> sessions;
	private List<String> lastMultiCommands;
	private String hideCommand;
	
	public String execute () {

		try {
		
			SSHSessionManager mngr = SSHSessionManager.getInstance();
			lastMultiCommands = mngr.getLastMultiCommands();
			sessions = mngr.getSessions();
			
			if ( command != null && !command.equals("") ) {
				boolean hide = ( hideCommand != null && hideCommand.equals("on") ); 
				mngr.multipleRun( command, hide );
			}


		} catch ( Exception e ) {
			e.printStackTrace();
			setMessageText("Error: " + e.getMessage() );
		}
			
		return "ok";
	}

	
	public void setCommand(String command) {
		this.command = command;
	}

	public List<SSHSession> getSessions() {
		return sessions;
	}
	
	public List<String> getLastMultiCommands() {
		return lastMultiCommands;
	}
	
	public void setHideCommand(String hideCommand) {
		this.hideCommand = hideCommand;
	}
	
}
