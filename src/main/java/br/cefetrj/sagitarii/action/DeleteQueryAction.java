
package br.cefetrj.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.persistence.services.CustomQueryService;

@Action (value = "deleteQuery", results = { @Result (type="redirect", location = "${destiny}", name = "ok") 
}, interceptorRefs= { @InterceptorRef("seguranca")} ) 

@ParentPackage("default")
public class DeleteQueryAction extends BasicActionClass {
	private int idExperiment;
	private String destiny;
	private int idQuery;
	
	public String execute () {
		destiny = "viewQueries?idExperiment=" + idExperiment;
	
		try {
			CustomQueryService cqs = new CustomQueryService();
			cqs.deleteCustomQuery(idQuery);
			
			setMessageText( "Query deleted." );
		} catch (Exception e) {
			setMessageText("Error: " + e.getMessage() );
		}
		
		return "ok";
	}

	public String getDestiny() {
		return destiny;
	}
	
	public void setIdExperiment(int idExperiment) {
		this.idExperiment = idExperiment;
	}
	
	public void setIdQuery(int idQuery) {
		this.idQuery = idQuery;
	}

}
