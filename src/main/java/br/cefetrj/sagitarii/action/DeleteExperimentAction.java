
package br.cefetrj.sagitarii.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.persistence.entity.Activity;
import br.cefetrj.sagitarii.persistence.entity.Experiment;
import br.cefetrj.sagitarii.persistence.exceptions.DatabaseConnectException;
import br.cefetrj.sagitarii.persistence.services.ExperimentService;

@Action (value = "deleteExperiment", 
	results = { 
		@Result ( location="${destiny}", type="redirect", name="ok") 
	}, interceptorRefs= { @InterceptorRef("seguranca")	 }) 

@ParentPackage("default")
public class DeleteExperimentAction extends BasicActionClass {
	private int idExperiment;
	private int idWorkflow;
	private Experiment experiment;
	private String destiny;
	private List<Activity> activities = new ArrayList<Activity>();
	
	public String execute () {
		if ( idWorkflow >= 0 ) {
			destiny = "viewWorkflow?idWorkflow=" + idWorkflow;
		} else {
			destiny = "viewExperiments";
		}
		try {
			setMessageText("Deleting experiment. This can take some time. You can close this message or wait until the experiment is deleted.");
			new ExperimentService().deleteExperiment( idExperiment );
		} catch (DatabaseConnectException e) {
			setMessageText("Database Error.");
		} catch (Exception e) {
			setMessageText("Error: " + e.getMessage() );
		}
		
		return "ok";
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public void setIdExperiment(int idExperiment) {
		this.idExperiment = idExperiment;
	}

	public List<Activity> getActivities() {
		return activities;
	}

	public String getDestiny() {
		return destiny;
	}

	public void setIdWorkflow(int idWorkflow) {
		this.idWorkflow = idWorkflow;
	}

	
	
}
