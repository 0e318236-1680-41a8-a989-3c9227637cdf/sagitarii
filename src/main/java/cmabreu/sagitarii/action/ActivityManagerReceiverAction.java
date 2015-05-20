package cmabreu.sagitarii.action;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.core.ClustersManager;

import com.opensymphony.xwork2.ActionContext;

@Action(value="activityManagerReceiver", results= {
			@Result(name="ok", type="httpheader", params={"status", "200"}) }
		)

@ParentPackage("default")
public class ActivityManagerReceiverAction  {
	private String response;
	private String pipelineId;
	private String node;

	public String execute () {
		if ( response != null ) {
			
			if ( response.equals("RUNNING") ) {
				ClustersManager cm = ClustersManager.getInstance();
				cm.acceptTask( pipelineId, node );
			}
			
			if ( response.equals("CANNOT_EXEC") ) {
				ClustersManager cm = ClustersManager.getInstance();
				cm.refuseTask(pipelineId, node);
			}
			
		}
		try {
			HttpServletResponse response = (HttpServletResponse)ActionContext.getContext().get(StrutsStatics.HTTP_RESPONSE);
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write("OK");
		} catch (IOException ex) {
		}
		return "ok";
	}
	
	
	public void setResponse(String response) {
		this.response = response;
	}
	
	
	public void setNode(String node) {
		this.node = node;
	}

	public void setPipelineId(String pipelineId) {
		this.pipelineId = pipelineId;
	}
	
	
}