
package cmabreu.sagitarii.action;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.persistence.services.WorkflowService;

import com.opensymphony.xwork2.ActionContext;

@Action(value="getXMLWorkflow", results= {  
	    @Result(name="ok", type="httpheader", params={"status", "200"}) }
)   

@ParentPackage("default")
public class GetXMLWorkflowAction extends BasicActionClass {
	private int idWorkflow;
	
	public String execute () {
		String resp = "";
		
		try {
			resp = new WorkflowService().getXML(idWorkflow);
		} catch ( Exception e ) {
			
		}
		
		try { 
			HttpServletResponse response = (HttpServletResponse)ActionContext.getContext().get(StrutsStatics.HTTP_RESPONSE);
			response.setCharacterEncoding("UTF-8"); 
			response.getWriter().write( resp );  
		} catch (IOException ex) {
			
		}
		
		return "ok";
	}

	public void setIdWorkflow(int idWorkflow) {
		this.idWorkflow = idWorkflow;
	}
	
}
