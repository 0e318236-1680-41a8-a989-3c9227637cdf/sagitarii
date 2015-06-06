package cmabreu.sagitarii.action;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.core.ClustersManager;

import com.opensymphony.xwork2.ActionContext;

@Action(value="receiveNodeTasks", results= {  
	    @Result(name="ok", type="httpheader", params={"status", "200"}) }
)   

@ParentPackage("default")
public class ReceiveNodeTasksAction extends BasicActionClass {
	private String tasks;
	
	public String execute(){
		String resposta = "OK";
		ClustersManager.getInstance().receiveNodeTasks( tasks );
		
		try { 
			HttpServletResponse response = (HttpServletResponse)ActionContext.getContext().get(StrutsStatics.HTTP_RESPONSE);
			response.setCharacterEncoding("UTF-8"); 
			response.getWriter().write(resposta);  
		} catch (IOException ex) {
			
		}
		
		return "ok";
	}

	public void setTasks(String tasks) {
		this.tasks = tasks;
	}
	
}
