
package cmabreu.sagitarii.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.persistence.entity.Relation;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.services.RelationService;

@Action (value = "newExecutorBash", results = { 
		@Result (location = "newExecutorBash.jsp", name = "ok"),
		@Result (type="redirect", location = "indexRedir", name = "error") 
	}, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 

@ParentPackage("default")
public class NewExecutorBashAction extends BasicActionClass {
	private List<Relation> customTables;
	
	public String execute () {
		
		try {
			RelationService ts = new RelationService();
			customTables = ts.getList();
		} catch ( NotFoundException nfe ) {
			setMessageText("No custom tables found.");
			return "error";
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		return "ok";
	}

	public List<Relation> getCustomTables() {
		return customTables;
	}

}
