package cmabreu.sagitarii.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.core.LogItem;
import cmabreu.sagitarii.core.MainLog;

@Action(value="showNodeLog", results = { 
		@Result ( location = "nodeLog.jsp", name = "ok") 
	} , interceptorRefs= { @InterceptorRef("seguranca")	 }
) 

@ParentPackage("default")
public class ShowNodeLogAction extends BasicActionClass {
	private String macAddress;
	private List<LogItem> log;
	
	public String execute(){
		log =  MainLog.getInstance().getLogByNode( macAddress );
		return "ok";
	}

	public List<LogItem> getLog() {
		return log;
	}
	
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
	public String getMacAddress() {
		return macAddress;
	}

}
