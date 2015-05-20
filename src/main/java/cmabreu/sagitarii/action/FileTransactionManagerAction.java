
package cmabreu.sagitarii.action;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.core.sockets.FileReceiverManager;

import com.opensymphony.xwork2.ActionContext;

@Action (value = "transactionManager", 
	results = { 
		@Result(name="ok", type="httpheader", params={"status", "200"}) } 
	) 

@ParentPackage("default")
public class FileTransactionManagerAction extends BasicActionClass {
	private String command;
	private String sessionSerial;
	
	
	public String execute () {

		String resp = "";
		try {
			
			if ( command.equals("beginTransaction") ) {
				resp = FileReceiverManager.getInstance().beginTransaction();
			}
			
			if ( command.equals("commit") ) {
				FileReceiverManager.getInstance().commit( sessionSerial );
				resp = "ok";
			}
			
		} catch (Exception e) {
			resp = e.getMessage();
		}
		
		try { 
			HttpServletResponse response = (HttpServletResponse)ActionContext.getContext().get(StrutsStatics.HTTP_RESPONSE);
			response.setCharacterEncoding("UTF-8"); 
			response.setContentType("text/plain");
			response.getWriter().write( resp );  
		} catch (IOException ex) {
			
		}

		return "ok";
	}


	public void setCommand(String command) {
		this.command = command;
	}
	
	public void setSessionSerial(String sessionSerial) {
		this.sessionSerial = sessionSerial;
	}
	
}
