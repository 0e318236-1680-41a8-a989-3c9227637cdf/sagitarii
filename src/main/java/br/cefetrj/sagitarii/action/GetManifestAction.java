
package br.cefetrj.sagitarii.action;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.persistence.services.ExecutorService;

import com.opensymphony.xwork2.ActionContext;

@Action(value="getManifest", results= {  
	    @Result(name="ok", type="httpheader", params={"status", "200"}) }
)   

@ParentPackage("default")
public class GetManifestAction extends BasicActionClass {

	public String execute () {
		String resp = "";
		try {
			ExecutorService cs = new ExecutorService();
			resp = cs.getAsManifest();
		} catch ( Exception e ) {
			//e.printStackTrace();
		}
		
		try { 
			HttpServletResponse response = (HttpServletResponse)ActionContext.getContext().get(StrutsStatics.HTTP_RESPONSE);
			response.setCharacterEncoding("UTF-8"); 
			response.setContentType("application/xml");
			response.getWriter().write( resp );  
		} catch (IOException ex) {
			
		}
		
		return "ok";
	}

}
