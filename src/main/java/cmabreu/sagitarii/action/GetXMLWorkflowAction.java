
package cmabreu.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.persistence.services.WorkflowService;

@Action(value="getXMLWorkflow", results= {  
	    @Result(location = "viewText.jsp", name = "ok")}, interceptorRefs= { @InterceptorRef("seguranca")}
)   

@ParentPackage("default")
public class GetXMLWorkflowAction extends BasicActionClass {
	private int idWorkflow;
	private String textContent;
	private String fileName;
	
	public String execute () {
		
		try {
			textContent = new WorkflowService().getXML(idWorkflow);
			fileName = "workflow.xml";
		} catch ( Exception e ) {
			textContent = e.getMessage();

		}
		
		return "ok";
	}

	public void setIdWorkflow(int idWorkflow) {
		this.idWorkflow = idWorkflow;
	}
	
	public String getTextContent() {
		return textContent;
	}
	
	public String getFileName() {
		return fileName;
	}
	
}
