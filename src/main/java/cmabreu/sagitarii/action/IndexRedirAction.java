
package cmabreu.sagitarii.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.persistence.entity.Workflow;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.services.WorkflowService;

@Action (value = "indexRedir", results = { @Result (location = "workflows.jsp", name = "ok") 
}, interceptorRefs= { @InterceptorRef("seguranca")} ) 

@ParentPackage("default")
public class IndexRedirAction extends BasicActionClass {
	private List<Workflow> wfList;
	
	public String execute () {
		try {
			WorkflowService wf = new WorkflowService();
			wfList = wf.getList();
		} catch ( NotFoundException  e) {
			// Lista vazia
		} catch (Exception e) {
			setMessageText("Erro Grave: " + e.getMessage() );
		} 
		
		return "ok";
	}

	public List<Workflow> getWfList() {
		return wfList;
	}

}
