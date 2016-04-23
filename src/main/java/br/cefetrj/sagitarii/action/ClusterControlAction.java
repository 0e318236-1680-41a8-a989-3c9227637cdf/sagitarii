package br.cefetrj.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.core.NodesManager;

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
					NodesManager.getInstance().quit(mac);
				}
				
				if ( command.equals("restart")) {
					NodesManager.getInstance().restart(mac);
				}
				
				if ( command.equals("reloadWrappers")) {
					NodesManager.getInstance().reloadWrappers();
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
