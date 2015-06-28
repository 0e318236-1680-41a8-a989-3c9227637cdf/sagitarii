
package cmabreu.sagitarii.action;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.core.filetransfer.FileReceiverManager;

import com.opensymphony.xwork2.ActionContext;

@Action (value = "stopTransferSession", 
	results = { 
		@Result(name="ok", type="httpheader", params={"status", "200"}) } 
	) 

@ParentPackage("default")
public class StopTransferSessionAction extends BasicActionClass {
	private String sessionSerial;
	
	public String execute () {

		String resp = "ok";
		
		try {
			FileReceiverManager.getInstance().forceStopAndCancel( sessionSerial );
		} catch( Exception e) {
			resp = e.getMessage();
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

	public void setSessionSerial(String sessionSerial) {
		this.sessionSerial = sessionSerial;
	}
	
}
