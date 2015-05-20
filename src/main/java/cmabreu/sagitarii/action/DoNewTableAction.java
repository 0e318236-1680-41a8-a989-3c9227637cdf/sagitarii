package cmabreu.sagitarii.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.core.TableAttribute;
import cmabreu.sagitarii.persistence.entity.Relation;
import cmabreu.sagitarii.persistence.services.RelationService;

@Action (value = "doNewTable", results = { @Result (type="redirect", location = "tablesmanager", name = "ok") 
}, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 

@ParentPackage("default")
public class DoNewTableAction extends BasicActionClass {
	private Relation table;
	private List<TableAttribute> attributes;
	
	
	public String execute () {
		try {
			RelationService ts = new RelationService();
			ts.insertTable(table, attributes);
			setMessageText("Relation " + table.getName() + " inserted and created into database.");
		} catch ( Exception e ) {
			setMessageText("Error: " + e.getMessage() );
		} 			
		return "ok";
	}

	
	public Relation getTable() {
		return table;
	}

	public void setTable(Relation table) {
		this.table = table;
	}

	public List<TableAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<TableAttribute> attributes) {
		this.attributes = attributes;
	}
	
}
