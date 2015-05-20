
package cmabreu.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.persistence.exceptions.DatabaseConnectException;
import cmabreu.sagitarii.persistence.services.RelationService;

@Action (value = "deleteTable", results = { @Result (type="redirect", location = "tablesmanager", name = "ok") 
}, interceptorRefs= { @InterceptorRef("seguranca")} ) 

@ParentPackage("default")
public class DeleteTableAction extends BasicActionClass {
	private Integer idTable;
	
	public String execute () {
		
		try {
			
			RelationService es = new RelationService();
			String tableName = es.deleteTable(idTable);
			setMessageText("Table " + tableName + " deleted.");
			
		} catch ( DatabaseConnectException e ) {
			setMessageText( e.getMessage() );
		} catch ( Exception e ) {
			setMessageText("Error: " + e.getMessage() );
		} 
		
		return "ok";
	}

	public void setIdTable(Integer idTable) {
		this.idTable = idTable;
	}

}
