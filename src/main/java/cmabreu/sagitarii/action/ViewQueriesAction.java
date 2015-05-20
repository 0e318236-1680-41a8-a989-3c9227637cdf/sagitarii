
package cmabreu.sagitarii.action;

import java.util.Set;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.persistence.entity.CustomQuery;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.services.CustomQueryService;

@Action (value = "viewQueries", results = { @Result (location = "viewQueries.jsp", name = "ok") 
}, interceptorRefs= { @InterceptorRef("seguranca")} ) 

@ParentPackage("default")
public class ViewQueriesAction extends BasicActionClass {
	private Set<CustomQuery> queries;
	private int idExperiment;
	
	public String execute () {

		try {
			CustomQueryService cqs = new CustomQueryService();
			queries = cqs.getList( idExperiment );
		} catch ( NotFoundException ignored ) {
			//
		} catch (Exception e) {
			setMessageText("Error: " + e.getMessage() );
		} 
		
		return "ok";
	}

	public void setIdExperiment(int idExperiment) {
		this.idExperiment = idExperiment;
	}

	public Set<CustomQuery> getQueries() {
		return queries;
	}
	
	public int getIdExperiment() {
		return idExperiment;
	}
	
}
