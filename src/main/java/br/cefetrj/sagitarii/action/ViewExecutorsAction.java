
package br.cefetrj.sagitarii.action;

import java.util.Set;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.persistence.entity.ActivationExecutor;
import br.cefetrj.sagitarii.persistence.exceptions.NotFoundException;
import br.cefetrj.sagitarii.persistence.services.ExecutorService;

@Action (value = "viewExecutors", results = { @Result ( location = "viewExecutors.jsp", name = "ok") 
}, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 

@ParentPackage("default")
public class ViewExecutorsAction extends BasicActionClass {
	private Set<ActivationExecutor> executors;
	
	public String execute () {
		
		try {
			ExecutorService cs = new ExecutorService();
			executors = cs.getList();
			
		} catch ( NotFoundException ignored ) {
			
		} catch ( Exception e ) {
			setMessageText( "Error: " + e.getMessage() );
		}
		
		return "ok";
	}

	public Set<ActivationExecutor> getExecutors() {
		return executors;
	}

	
}
