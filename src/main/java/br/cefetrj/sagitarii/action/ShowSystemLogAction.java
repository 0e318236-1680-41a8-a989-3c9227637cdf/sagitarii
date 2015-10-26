package br.cefetrj.sagitarii.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.persistence.entity.LogEntry;
import br.cefetrj.sagitarii.persistence.services.LogService;

@Action(value="showSystemLog", results = { 
		@Result ( location = "systemLog.jsp", name = "ok") 
	} , interceptorRefs= { @InterceptorRef("seguranca")	 }
) 

@ParentPackage("default")
public class ShowSystemLogAction extends BasicActionClass {
	private List<LogEntry> log;
	private String type;
	private String activitySerial;
	
	public String execute(){
		log = new ArrayList<LogEntry>();
		try {
			LogService ls = new LogService();
			if ( type != null ) {
				log = ls.getList( type );
			}
			if ( activitySerial != null ) {
				log = ls.getListByActivity( activitySerial );
			}
		} catch ( Exception e ) {
			
		}
		
		return "ok";
	}

	public List<LogEntry> getLog() {
		return log;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public void setActivitySerial(String activitySerial) {
		this.activitySerial = activitySerial;
	}
	
}
