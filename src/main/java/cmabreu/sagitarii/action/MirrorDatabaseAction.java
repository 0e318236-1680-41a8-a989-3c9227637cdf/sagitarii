
package cmabreu.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.persistence.services.RelationService;

@Action (value = "mirrorDatabase", results = { @Result (type="redirect", location = "index", name = "ok") } ) 

@ParentPackage("default")
public class MirrorDatabaseAction extends BasicActionClass {
	
	public String execute () {
		try {
			RelationService rs = new RelationService();
			rs.sendToSlave("127.0.0.1", "blanksagi", "postgres", "admin", "5432");
			
		} catch ( Exception e ) {
			setMessageText( e.getMessage() );
		} 
		
		return "ok";
	}


	
}
