
package cmabreu.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.core.Sagitarii;

@Action (value = "pauseExperiment", 
	results = { 
		@Result ( location = "${destiny}", type="redirect", name = "ok") 
	}, interceptorRefs= { @InterceptorRef("seguranca")	 }) 

@ParentPackage("default")
public class PauseExperimentAction extends BasicActionClass {
	private int idExperiment;
	private String destiny;
	
	public String execute () {
		destiny = "viewExperiment?idExperiment=" + idExperiment;
				
		try {
			Sagitarii.getInstance().pause(idExperiment);
			setMessageText("Experiment will pause as soon pipeline buffer is empty. This may take several minutes depending on buffer size.");
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
