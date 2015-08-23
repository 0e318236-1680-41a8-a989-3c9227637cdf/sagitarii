
package br.cefetrj.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.persistence.entity.Workflow;
import br.cefetrj.sagitarii.persistence.exceptions.NotFoundException;
import br.cefetrj.sagitarii.persistence.services.WorkflowService;

@Action (value = "newExperiment", results = { @Result (location = "newExperiment.jsp", name = "ok") 
}, interceptorRefs= { @InterceptorRef("seguranca")} ) 

@ParentPackage("default")
public class NewExperimentAction extends BasicActionClass {
	private int idWorkflow;
	private Workflow workflow;
	
	public String execute () {
		try {
			WorkflowService ws = new WorkflowService();
			workflow = ws.getWorkflow(idWorkflow);
		} catch ( NotFoundException  e) {
			// Empty list. Don't panic.
		} catch (Exception e) {
			setMessageText("Error: " + e.getMessage() );
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
