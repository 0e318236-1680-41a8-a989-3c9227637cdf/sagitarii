
package cmabreu.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.metrics.MetricController;

@Action (value = "resetMetrics", results = { 
		@Result (type="redirect", location = "viewMetrics", name = "ok") 
	}, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 


@ParentPackage("default")
public class ResetMetricsAction extends BasicActionClass {
	
	public String execute () {
		MetricController.getInstance().reset();
		return "ok";
	}


}
