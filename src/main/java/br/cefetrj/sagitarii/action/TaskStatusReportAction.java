package br.cefetrj.sagitarii.action;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.core.ClustersManager;

import com.opensymphony.xwork2.ActionContext;

@Action(value="taskStatusReport", results= {  
	    @Result(name="ok", type="httpheader", params={"status", "200"}) }
)   

@ParentPackage("default")
public class TaskStatusReportAction extends BasicActionClass {
	private String macAddress;
	private String status;
	private String instance;
	
	public String execute(){
		String resposta = "";
		
		ClustersManager.getInstance().informReport(macAddress, status, instance);
		
		try { 
			HttpServletResponse response = (HttpServletResponse)ActionContext.getContext().get(StrutsStatics.HTTP_RESPONSE);
			response.setCharacterEncoding("UTF-8"); 
			response.getWriter().write(resposta);  
		} catch (IOException ex) {
			
		}
		
		return "ok";
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public void setInstance(String instance) {
		this.instance = instance;
	}
	
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	
}
