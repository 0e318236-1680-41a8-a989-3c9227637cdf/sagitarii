
package br.cefetrj.sagitarii.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.core.Sagitarii;
import br.cefetrj.sagitarii.persistence.entity.Relation;
import br.cefetrj.sagitarii.persistence.exceptions.DatabaseConnectException;
import br.cefetrj.sagitarii.persistence.exceptions.NotFoundException;
import br.cefetrj.sagitarii.persistence.services.RelationService;

@Action (value = "tablesmanager", results = { @Result (location = "tables.jsp", name = "ok") }, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 

@ParentPackage("default")
public class TablesAction extends BasicActionClass {
	private List<Relation> tables;
	private String showWarning;
	
	public String execute () {
		
		showWarning = "none";
		try {
			RelationService ts = new RelationService();
			tables = ts.getList();
			if ( Sagitarii.getInstance().getRunningExperiments().size() > 0  ) {
				showWarning = "block";
			}
		} catch ( NotFoundException  e) {
			// Lista vazia
		} catch (DatabaseConnectException e) {
			setMessageText("Erro Grave: " + e.getMessage() );
		} 
		
		return "ok";
	}

	public List<Relation> getTables() {
		return tables;
	}

	public void setTables(List<Relation> tables) {
		this.tables = tables;
	}

	public String getShowWarning() {
		return showWarning;
	}

	public void setShowWarning(String showWarning) {
		this.showWarning = showWarning;
	}
}
