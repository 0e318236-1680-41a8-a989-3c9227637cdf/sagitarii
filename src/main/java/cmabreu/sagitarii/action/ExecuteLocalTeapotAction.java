
package cmabreu.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.core.processor.teapot.LocalTeapotLoader;

@Action (value = "executeLocalTeapot", 
	results = { 
		@Result ( type="redirect", location = "indexRedir", name = "ok"), 
	}, interceptorRefs= { @InterceptorRef("seguranca") }
) 

@ParentPackage("default")
public class ExecuteLocalTeapotAction extends BasicActionClass {
	
	public String execute () {
				
		try {
			
			LocalTeapotLoader.getInstance().execute();
			
		} catch (Exception e) {
			setMessageText("Error: " + e.getMessage() );
			return "ok";
		}
		return "ok";
	}

	
}
