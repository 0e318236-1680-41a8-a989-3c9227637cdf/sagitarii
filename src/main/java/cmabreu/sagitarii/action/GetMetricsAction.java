
package cmabreu.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.jfree.chart.JFreeChart;

import cmabreu.sagitarii.metrics.MetricController;

@Action (value = "getMetrics", results = { 
		@Result ( type="chart", params = {"width", "420", "height", "220"} , name = "ok") }, 
		interceptorRefs= { @InterceptorRef("seguranca") } 
) 

@ParentPackage("chart")
public class GetMetricsAction  {
	private JFreeChart chart;
	private String metricName;
	private String time;
	
	public String execute () {
		try {
			if ( metricName != null ) {
				chart = MetricController.getInstance().getEntity(metricName).getImage();
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return "ok";
	}

	
	public JFreeChart getChart() {
		return chart;
	}
	
	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
}
