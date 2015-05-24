package cmabreu.sagitarii.action;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.api.ExternalApi;

import com.opensymphony.xwork2.ActionContext;

@Action(value="externalApi", results= {  
	    @Result(name="ok", type="httpheader", params={"status", "200"}) }
)   

@ParentPackage("default")
public class ExternalApiAction extends BasicActionClass {
	private String externalForm;
	
	public String execute(){

		try { 
			String resposta = new ExternalApi().execute( externalForm );
			
			HttpServletResponse response = (HttpServletResponse)ActionContext.getContext().get(StrutsStatics.HTTP_RESPONSE);
			response.setCharacterEncoding("UTF-8"); 
			response.getWriter().write(resposta);  
		} catch (Exception ex) {
			ex.printStackTrace();
			
		}
		return "ok";
	}

	
	public void setExternalForm(String externalForm) {
		this.externalForm = externalForm;
	}

	
}
