
package cmabreu.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

@Action (value = "newExecutorLibrary", results = { 
		@Result (location = "newExecutorLibrary.jsp", name = "ok"),
		@Result (type="redirect", location = "indexRedir", name = "error") 
	}, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 

@ParentPackage("default")
public class NewExecutorLibraryAction extends BasicActionClass {
	
	public String execute () {
		return "ok";
	}


}
