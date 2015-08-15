
package br.cefetrj.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.persistence.entity.Experiment;
import br.cefetrj.sagitarii.persistence.services.ExperimentService;

@Action (value = "runExperiment", 
	results = { 
		@Result ( location = "${destiny}", type="redirect", name = "ok") 
	}, interceptorRefs= { @InterceptorRef("seguranca")	 }) 

@ParentPackage("default")
public class RunExperimentAction extends BasicActionClass {
	private int idExperiment;
	private Experiment experiment;
	private String destiny;
	
	public String execute () {
		destiny = "viewExperiment?idExperiment=" + idExperiment;
				
		try {
			experiment = new ExperimentService().runExperiment( idExperiment );
			setMessageText("Experiment " + experiment.getTagExec() + " is now running.");
		} catch (Exception e) {
			setMessageText("Error: " + e.getMessage() );
			return "ok";
		}
		return "ok";
	}


	public int getIdExperiment() {
		return idExperiment;
	}

	public void setIdExperiment(int idExperiment) {
		this.idExperiment = idExperiment;
	}

	public String getDestiny() {
		return destiny;
	}

}
