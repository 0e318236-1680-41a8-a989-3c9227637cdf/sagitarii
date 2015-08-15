
package br.cefetrj.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.misc.json.JsonElementConversor;
import br.cefetrj.sagitarii.persistence.entity.Experiment;
import br.cefetrj.sagitarii.persistence.exceptions.DatabaseConnectException;
import br.cefetrj.sagitarii.persistence.exceptions.NotFoundException;
import br.cefetrj.sagitarii.persistence.exceptions.UpdateException;
import br.cefetrj.sagitarii.persistence.services.ExperimentService;

@Action (value = "saveExperiment", results = { @Result (type="redirect", location = "viewExperiments", name = "ok") 
}, interceptorRefs= { @InterceptorRef("seguranca")} ) 

@ParentPackage("default")
public class SaveExperimentAction extends BasicActionClass {
	private int idExperiment;
	private String actJson;
	private String imagePreviewData;
	
	public String execute () {
		
		JsonElementConversor jec = new JsonElementConversor();
		if ( !jec.validadeGraphStructure( actJson ) ) {
			setMessageText( "The Workflow Graph Definition does not allow alone Activities." );
			return "ok";
		}
		
		try {
			ExperimentService es = new ExperimentService(); 
			Experiment exp = es.getExperiment(idExperiment);
			exp.setActivitiesSpecs( actJson );
			exp.setImagePreviewData(imagePreviewData);
			es.newTransaction();
			es.updateExperiment(exp);
			setMessageText("Experiment updated.");
		} catch ( NotFoundException  e) {
			// Lista vazia
		} catch (DatabaseConnectException | UpdateException  e) {
			setMessageText( e.getMessage() );
		} 
		return "ok";
	}

	public void setIdExperiment(int idExperiment) {
		this.idExperiment = idExperiment;
	}
	
	public void setActJson(String actJson) {
		this.actJson = actJson;
	}

	public void setImagePreviewData(String imagePreviewData) {
		this.imagePreviewData = imagePreviewData;
	}

}
