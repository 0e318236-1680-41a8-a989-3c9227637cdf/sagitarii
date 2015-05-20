
package cmabreu.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.core.Sagitarii;

@Action (value = "resumeExperiment", 
results = { 
		@Result ( location = "${destiny}", type="redirect", name = "ok") 
	}, interceptorRefs= { @InterceptorRef("seguranca")	 }) 

@ParentPackage("default")
public class ResumeExperimentAction extends BasicActionClass {
	private int idExperiment;
	private String destiny;
	
	public String execute () {
		destiny = "viewExperiment?idExperiment=" + idExperiment;
				
		try {
			Sagitarii.getInstance().resume( idExperiment );
			setMessageText("Experiment will resume. Wait until pipeline buffer is filled.");
		} catch (Exception e) {
			setMessageText("Erro: " + e.getMessage() );
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
