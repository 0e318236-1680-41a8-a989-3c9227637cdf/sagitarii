
package cmabreu.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.persistence.entity.Workflow;
import cmabreu.sagitarii.persistence.exceptions.DatabaseConnectException;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.services.WorkflowService;

@Action (value = "viewWorkflow", 
	results = { 
		@Result ( location = "viewWorkflow.jsp", name = "ok"),
		@Result ( location="wfmanager", type="redirect", name="erro") 
	}, interceptorRefs= { @InterceptorRef("seguranca")	 }) 

@ParentPackage("default")
public class ViewWorkflowAction extends BasicActionClass {
	private int idWorkflow;
	private Workflow workflow;
	
	public String execute () {
		
		try {
			WorkflowService ws = new WorkflowService();
			workflow = ws.getWorkflow(idWorkflow);
		} catch (DatabaseConnectException e) {
			e.printStackTrace();
			return "erro";
		} catch (NotFoundException e) {
			setMessageText("Error: " + e.getMessage() );
			return "erro";
		}
		
		return "ok";
	}

	public void setIdWorkflow(int idWorkflow) {
		this.idWorkflow = idWorkflow;
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	
}
