
package cmabreu.sagitarii.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.persistence.entity.Workflow;
import cmabreu.sagitarii.persistence.exceptions.DatabaseConnectException;
import cmabreu.sagitarii.persistence.services.WorkflowService;

@Action (value = "deleteWorkflow", 
	results = { 
		@Result ( type="redirect", location = "indexRedir", name = "ok") 
	}, interceptorRefs= { @InterceptorRef("seguranca")	 }) 

@ParentPackage("default")
public class DeleteWorkflowAction extends BasicActionClass {
	private int idWorkflow;
	private List<Workflow> wfList;
	
	public String execute () {
		
		try {
			WorkflowService wfs = new WorkflowService();
			Workflow wf = wfs.deleteWorkflow(idWorkflow);
			
			wfs.newTransaction();
			wfList = wfs.getList();
			
			setMessageText("Workflow "+wf.getTag()+" deleted.");

			
		} catch (DatabaseConnectException e) {
			e.printStackTrace();
		} catch (Exception e) {
			setMessageText("Error: " + e.getMessage() );
		}
		
		return "ok";
	}

	public void setIdWorkflow(int idWorkflow) {
		this.idWorkflow = idWorkflow;
	}

	public List<Workflow> getWfList() {
		return wfList;
	}

	public void setWfList(List<Workflow> wfList) {
		this.wfList = wfList;
	}

	
}
