package cmabreu.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.core.ClustersManager;

@Action (value = "clusterControl", results = { @Result (type="redirect", location = "viewClusters", name = "ok") 
}, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 

@ParentPackage("default")
public class ClusterControlAction extends BasicActionClass {
	private String command;
	private String mac;
	
	public String execute(){

		if( command != null ) {
			try {
				if ( command.equals("quit")) {
					ClustersManager.getInstance().quit(mac);
				}
				
				if ( command.equals("restart")) {
					ClustersManager.getInstance().restart(mac);
				}
				
				if ( command.equals("reloadWrappers")) {
					ClustersManager.getInstance().reloadWrappers();
				}
			} catch ( Exception e ) {
				setMessageText( e.getMessage() );
			}
			
		}
		return "ok";
	}

	public void setCommand(String command) {
		this.command = command;
	}
	
	public void setMac(String mac) {
		this.mac = mac;
	}

}
