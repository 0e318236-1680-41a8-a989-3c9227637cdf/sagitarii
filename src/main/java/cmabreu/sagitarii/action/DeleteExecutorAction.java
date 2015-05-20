
package cmabreu.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.persistence.services.ExecutorService;

@Action (value = "deleteExecutor", results = { @Result (type="redirect", location = "viewExecutors", name = "ok") 
}, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 

@ParentPackage("default")
public class DeleteExecutorAction extends BasicActionClass {
	private int idExecutor;
	
	public String execute () {
		
		try {
			ExecutorService cs = new ExecutorService();
			cs.deleteExecutor(idExecutor);
			setMessageText( "Selection Executor deleted.");
		} catch ( Exception e ) {
			setMessageText( "Error: " + e.getMessage() );
		}
		
		return "ok";
	}

	public void setIdExecutor(int idExecutor) {
		this.idExecutor = idExecutor;
	}

}
