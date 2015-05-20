package cmabreu.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.core.ClustersManager;

@Action (value = "cleanWorkspaces", results = { @Result (type="redirect", location = "viewClusters", name = "ok") 
}, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 

@ParentPackage("default")
public class CleanWorkspacesAction extends BasicActionClass {
	
	public String execute(){

		try {
			ClustersManager.getInstance().cleanWorkspaces();
			setMessageText( "Nodes will clean workspaces soon.");
		} catch ( Exception e ) {
			setMessageText( e.getMessage() );
		} 
			
		return "ok";
	}


}
