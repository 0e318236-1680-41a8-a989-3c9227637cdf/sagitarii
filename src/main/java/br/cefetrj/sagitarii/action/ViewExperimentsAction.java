
package br.cefetrj.sagitarii.action;

import java.util.Set;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.persistence.entity.Experiment;
import br.cefetrj.sagitarii.persistence.exceptions.NotFoundException;
import br.cefetrj.sagitarii.persistence.services.ExperimentService;

@Action (value = "viewExperiments", 
	results = { 
		@Result ( location = "viewExperiments.jsp", name = "ok")
	} , interceptorRefs= { @InterceptorRef("seguranca")	 }  ) 

@ParentPackage("default")
public class ViewExperimentsAction extends BasicActionClass {
	private Set<Experiment> experiments;
	
	public String execute () {
		try {
			ExperimentService es = new ExperimentService();
			experiments = es.getList( getLoggedUser());
		} catch ( NotFoundException e ) {
			//
		} catch ( Exception e ) {
			setMessageText( e.getMessage() );
		}
		return "ok";
	}

	public Set<Experiment> getExperiments() {
		return experiments;
	}
}
