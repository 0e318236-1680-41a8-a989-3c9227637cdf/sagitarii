
package cmabreu.sagitarii.action;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.core.filetransfer.FileReceiverManager;

import com.opensymphony.xwork2.ActionContext;

@Action (value = "getPercent", 
	results = { 
		@Result(name="ok", type="httpheader", params={"status", "200"}) } 
	) 

@ParentPackage("default")
public class GetPercentAction extends BasicActionClass {
	private String percent;
	private String sessionSerial;
	private String type;
	
	public String execute () {

		String resp = "";
		
		try {
			
			if( type == null ) {
				type = "";
			}
			
			if( type.equals("savers") ) {
				resp = FileReceiverManager.getInstance().getSaversPercentAsJson( sessionSerial );
			}
			
			if( type.equals("importers") ) {
				resp = FileReceiverManager.getInstance().getImportersPercentAsJson( sessionSerial );
			}
			
			
		} catch( Exception e) {
			e.printStackTrace();
		}
		
		try { 
			HttpServletResponse response = (HttpServletResponse)ActionContext.getContext().get(StrutsStatics.HTTP_RESPONSE);
			response.setCharacterEncoding("UTF-8"); 
			response.setContentType("application/json");
			response.getWriter().write( resp );  
		} catch (IOException ex) {
			
		}

		return "ok";
	}

	public String getPercent() {
		return percent;
	}
	
	public void setSessionSerial(String sessionSerial) {
		this.sessionSerial = sessionSerial;
	}
	
	public void setType(String type) {
		this.type = type;
	}
}
