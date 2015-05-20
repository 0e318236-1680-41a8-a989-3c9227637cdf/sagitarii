
package cmabreu.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.persistence.entity.Experiment;
import cmabreu.sagitarii.persistence.exceptions.DatabaseConnectException;
import cmabreu.sagitarii.persistence.exceptions.InsertException;
import cmabreu.sagitarii.persistence.services.ExperimentService;

@Action (value = "doNewExperiment", results = { @Result (type="redirect", location = "${destiny}", name = "ok")
}, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 

@ParentPackage("default")
public class DoNewExperimentAction extends BasicActionClass {
	private int idWorkflow;
	private Experiment experiment;
	private String destiny;
	
	public String execute () {
		destiny = "viewWorkflow?idWorkflow=" + idWorkflow;
		try {
			
			ExperimentService es = new ExperimentService();
			experiment = es.generateExperiment( idWorkflow, getLoggedUser() );
			setMessageText("You have a new Experiment!");
			
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

	
}
