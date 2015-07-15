package cmabreu.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.core.ClustersManager;

@Action (value = "clearNodeLog", results = { @Result (type="redirect", location = "viewClusters", name = "ok") 
}, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 

@ParentPackage("default")
public class ClearNodeLogAction extends BasicActionClass {
	private String macAddress;
	
	public String execute(){

		try {
			ClustersManager.getInstance().clearNodeLog( macAddress );
		} catch ( Exception e ) {
			setMessageText( e.getMessage() );
		} 
			
		return "ok";
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
}
