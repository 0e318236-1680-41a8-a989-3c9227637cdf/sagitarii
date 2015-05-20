
package cmabreu.sagitarii.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.persistence.entity.Workflow;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.services.WorkflowService;

@Action (value = "doEditWf", results = { @Result (type="redirect", location = "indexRedir", name = "ok") 
}, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 

@ParentPackage("default")
public class DoEditWorkflowAction extends BasicActionClass {
	private List<Workflow> wfList;
	private Workflow wf;
	
	public String execute () {
		
		try {
			WorkflowService wfs = new WorkflowService();
			wf.setOwner( getLoggedUser() );
			
			wfs.updateWorkflow(wf);
			
			wfs.newTransaction();
			wfList = wfs.getList();
			
			setMessageText("Workflow "+wf.getTag()+" updated.");
		} catch ( NotFoundException  e) {
			// Lista vazia
		} catch (Exception e) {
			setMessageText( e.getMessage() );
		} 
		
		return "ok";
	}

	
	public Workflow getWf() {
		return wf;
	}

	public void setWf(Workflow wf) {
		this.wf = wf;
	}

	public List<Workflow> getWfList() {
		return wfList;
	}

	public void setWfList(List<Workflow> wfList) {
		this.wfList = wfList;
	}

}
