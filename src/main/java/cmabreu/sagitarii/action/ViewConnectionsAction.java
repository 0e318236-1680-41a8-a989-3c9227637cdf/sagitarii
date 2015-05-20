
package cmabreu.sagitarii.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.misc.DatabaseConnectionItem;
import cmabreu.sagitarii.persistence.services.RelationService;

@Action (value = "viewConnections", 
	results = { 
		@Result ( location = "viewConnections.jsp", name = "ok")
	}, interceptorRefs= { @InterceptorRef("seguranca")	 }) 

@ParentPackage("default")
public class ViewConnectionsAction extends BasicActionClass {
	private List<DatabaseConnectionItem> connections;
	
	public String execute () {
		
		try {
			RelationService rs = new RelationService();
			connections = rs.getConnectionUse();
		} catch( Exception e) {
			
		}
		
		return "ok";
	}

	public List<DatabaseConnectionItem> getConnections() {
		return connections;
	}
}
