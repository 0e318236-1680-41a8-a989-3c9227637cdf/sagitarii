
package cmabreu.sagitarii.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.core.ssh.SSHSession;
import cmabreu.sagitarii.core.ssh.SSHSessionManager;

@Action (value = "nodeSSHMultiTerminal", results = { @Result (location = "nodeSSHMultiTerminal.jsp", name = "ok") 
}, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 

@ParentPackage("default")
public class NodeSSHMultiTerminalAction extends BasicActionClass {
	private String command;
	private List<SSHSession> sessions;
	private List<String> lastMultiCommands;
	
	public String execute () {

		try {
		
			SSHSessionManager mngr = SSHSessionManager.getInstance();
			lastMultiCommands = mngr.getLastMultiCommands();
			sessions = mngr.getSessions();
			
			if ( command != null && !command.equals("") ) {
				mngr.multipleRun(command);
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
	
}
