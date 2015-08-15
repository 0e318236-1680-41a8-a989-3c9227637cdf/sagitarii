
package br.cefetrj.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.misc.json.JsonElementConversor;
import br.cefetrj.sagitarii.persistence.entity.Workflow;
import br.cefetrj.sagitarii.persistence.exceptions.DatabaseConnectException;
import br.cefetrj.sagitarii.persistence.exceptions.NotFoundException;
import br.cefetrj.sagitarii.persistence.exceptions.UpdateException;
import br.cefetrj.sagitarii.persistence.services.WorkflowService;

@Action (value = "doProcessActJson", results = { @Result (type="redirect", location = "indexRedir", name = "ok") 
}, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 

@ParentPackage("default")
public class DoProcessActJsonAction extends BasicActionClass {
	private int idWorkflow;
	private String actJson;
	private String imagePreviewData;
	
	public String execute () {
				
		JsonElementConversor jec = new JsonElementConversor();
		if ( !jec.validadeGraphStructure( actJson ) ) {
			setMessageText( "The Workflow Graph Definition is invalid." );
			return "ok";
		}
		
		try {
			WorkflowService wfs = new WorkflowService();
			Workflow wf = wfs.getWorkflow(idWorkflow);
			wf.setActivitiesSpecs( actJson );
			wf.setImagePreviewData(imagePreviewData);
			
			wfs.newTransaction();

			wfs.updateWorkflowActivities(wf);
			
			setMessageText("Activities created.");
		} catch ( NotFoundException  e) {
			// Lista vazia
		} catch (DatabaseConnectException | UpdateException  e) {
			setMessageText( e.getMessage() );
		}
		
		return "ok";
		
	}

	public void setIdWorkflow(int idWorkflow) {
		this.idWorkflow = idWorkflow;
	}

	public void setActJson(String actJson) {
		this.actJson = actJson;
	}

	public void setImagePreviewData(String imagePreviewData) {
		this.imagePreviewData = imagePreviewData;
	}

	
}
