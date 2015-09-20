package br.cefetrj.sagitarii.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.core.types.LogType;
import br.cefetrj.sagitarii.persistence.entity.LogEntry;
import br.cefetrj.sagitarii.persistence.services.LogService;

@Action(value="showSystemLog", results = { 
		@Result ( location = "systemLog.jsp", name = "ok") 
	} , interceptorRefs= { @InterceptorRef("seguranca")	 }
) 

@ParentPackage("default")
public class ShowSystemLogAction extends BasicActionClass {
	private List<LogEntry> log;
	
	public String execute(){
		
		try {
			LogService ls = new LogService();
			log = ls.getList( LogType.NODE_STATUS );
		} catch ( Exception e ) {
			
		}
		
		return "ok";
	}

	public List<LogEntry> getLog() {
		return log;
	}

}
