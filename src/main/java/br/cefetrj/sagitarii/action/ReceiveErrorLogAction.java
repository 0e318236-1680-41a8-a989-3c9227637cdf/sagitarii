package br.cefetrj.sagitarii.action;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.core.ClustersManager;

import com.opensymphony.xwork2.ActionContext;

@Action(value="receiveErrorLog", results= {  
	    @Result(name="ok", type="httpheader", params={"status", "200"}) }
)   

@ParentPackage("default")
public class ReceiveErrorLogAction extends BasicActionClass {
	private String macAddress;
	private String errorLog;
	
	public String execute(){
		String resposta = "";
		
		ClustersManager.getInstance().setTeapotMessage(errorLog, macAddress);
		
		try { 
			HttpServletResponse response = (HttpServletResponse)ActionContext.getContext().get(StrutsStatics.HTTP_RESPONSE);
			response.setCharacterEncoding("UTF-8"); 
			response.getWriter().write(resposta);  
		} catch (IOException ex) {
			
		}
		
		return "ok";
	}

	public void setErrorLog(String errorLog) {
		this.errorLog = errorLog;
	}
	
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	
}
