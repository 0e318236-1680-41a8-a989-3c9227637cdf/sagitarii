
package br.cefetrj.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.persistence.entity.CustomQuery;
import br.cefetrj.sagitarii.persistence.services.CustomQueryService;

@Action (value = "doNewQuery", results = { @Result (type="redirect", location = "${destiny}", name = "ok")
}, interceptorRefs= { @InterceptorRef("seguranca")}  ) 

@ParentPackage("default")
public class DoNewQueryAction extends BasicActionClass {
	private CustomQuery query;
	private int idExperiment;
	private String destiny;
	
	public String execute () {
		
		destiny = "viewQueries?idExperiment=" + idExperiment;
		
		CustomQueryService cqs;
		try {
			cqs = new CustomQueryService();
			cqs.insertCustomQuery(query, idExperiment);
			setMessageText( "Query saved." );
		} catch (Exception e) {
			setMessageText("Error: " + e.getMessage() );
		}
		
		
		return "ok";
	}

	public CustomQuery getQuery() {
		return query;
	}
	
	public void setQuery(CustomQuery query) {
		this.query = query;
	}
	
	public void setIdExperiment(int idExperiment) {
		this.idExperiment = idExperiment;
	}
	
	public String getDestiny() {
		return destiny;
	}
}
