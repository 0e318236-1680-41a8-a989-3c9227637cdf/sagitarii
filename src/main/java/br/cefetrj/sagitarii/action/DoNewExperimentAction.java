
package br.cefetrj.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.persistence.entity.Experiment;
import br.cefetrj.sagitarii.persistence.exceptions.DatabaseConnectException;
import br.cefetrj.sagitarii.persistence.exceptions.InsertException;
import br.cefetrj.sagitarii.persistence.services.ExperimentService;

@Action (value = "doNewExperiment", results = { @Result (type="redirect", location = "${destiny}", name = "ok")
}, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 

@ParentPackage("default")
public class DoNewExperimentAction extends BasicActionClass {
	private int idWorkflow;
	private Experiment experiment;
	private String destiny;
	private String description;
	
	public String execute () {
		destiny = "viewWorkflow?idWorkflow=" + idWorkflow;
		try {
			
			ExperimentService es = new ExperimentService();
			experiment = es.generateExperiment( idWorkflow, getLoggedUser(), description );
			setMessageText("Experiment created.");
			
		} catch ( DatabaseConnectException e ) {
			e.printStackTrace();
		} catch ( InsertException e ) {
			e.printStackTrace();
		} 
		
		return "ok";
	}

	public void setIdWorkflow(int idWorkflow) {
		this.idWorkflow = idWorkflow;
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public String getDestiny() {
		return destiny;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
