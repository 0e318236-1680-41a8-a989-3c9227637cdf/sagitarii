package br.cefetrj.sagitarii.action;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.core.NodesManager;

import com.opensymphony.xwork2.ActionContext;

@Action(value="checkNewCluster", results= {  
	    @Result(name="ok", type="httpheader", params={"status", "200"}) }
)   

@ParentPackage("default")
public class CheckNewClusterAction extends BasicActionClass {
	
	public String execute(){
		String resposta = "";
		String have = "";
		NodesManager cm = NodesManager.getInstance();
		
		if ( cm.haveNewCluster() ) {
			have = "\"result\":\"YES\"";
		} else {
			have = "\"result\":\"NO\"";
		}
		resposta = "{" + have + "}";
		try { 
			HttpServletResponse response = (HttpServletResponse)ActionContext.getContext().get(StrutsStatics.HTTP_RESPONSE);
			response.setCharacterEncoding("UTF-8"); 
			response.getWriter().write(resposta);  
		} catch (IOException ex) {
			
		}
		
		return "ok";
	}

}
