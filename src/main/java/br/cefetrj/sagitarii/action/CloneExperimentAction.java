
package br.cefetrj.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.persistence.entity.Experiment;
import br.cefetrj.sagitarii.persistence.exceptions.NotFoundException;
import br.cefetrj.sagitarii.persistence.services.ExperimentService;

@Action (value = "cloneExperiment", 
	results = { 
		@Result ( type="redirect", location = "viewExperiment?idExperiment=${idExperiment}", name = "ok") 
	}, interceptorRefs= { @InterceptorRef("seguranca")	 }) 

@ParentPackage("default")
public class CloneExperimentAction extends BasicActionClass {
	private int idExperiment;
	
	public String execute () {
		try {
			ExperimentService es = new ExperimentService();
			Experiment experiment = es.cloneExperiment( idExperiment, getLoggedUser() );

			idExperiment = experiment.getIdExperiment();
			
		} catch (NotFoundException e) {
			setMessageText("Cannot find this Experiment.");
		} catch (Exception e) {
			setMessageText("Error: " + e.getMessage() );
			e.printStackTrace();
		}
		
		return "ok";
	}


	public void setIdExperiment(int idExperiment) {
		this.idExperiment = idExperiment;
	}

	public int getIdExperiment() {
		return idExperiment;
	}
}
