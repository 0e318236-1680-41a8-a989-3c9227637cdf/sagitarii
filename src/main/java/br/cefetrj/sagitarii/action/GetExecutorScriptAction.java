
package br.cefetrj.sagitarii.action;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.persistence.entity.ActivationExecutor;
import br.cefetrj.sagitarii.persistence.exceptions.NotFoundException;
import br.cefetrj.sagitarii.persistence.services.ExecutorService;

import com.opensymphony.xwork2.ActionContext;

@Action(value="getExecutorScript", results= {  
	    @Result(name="ok", type="httpheader", params={"status", "200"}) }
)   

@ParentPackage("default")
public class GetExecutorScriptAction extends BasicActionClass {
	private String criteriaAlias;
	public String execute () {
		String resp = "";
		try {
			ExecutorService cs = new ExecutorService();
			ActivationExecutor executor = cs.getExecutor(criteriaAlias);
			resp = executor.getSelectStatement();
		} catch ( NotFoundException ignored ) {
			
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

	public void setExecutorAlias(String criteriaAlias) {
		this.criteriaAlias = criteriaAlias;
	}
	
}
