
package cmabreu.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.jfree.chart.JFreeChart;

import cmabreu.sagitarii.metrics.MetricController;

@Action (value = "getMetrics", results = { 
		@Result ( type="chart", params = {"width", "660", "height", "225"} , name = "ok"),
	} 
) 


@ParentPackage("chart")
public class GetMetricsAction extends BasicActionClass {
	private JFreeChart retChart;
	private String metricName;
	
	public String execute () {
		retChart = MetricController.getInstance().getEntity(metricName).getImage();
		return "ok";
	}

	
	public JFreeChart getChart() {
		return retChart;
	}

	public JFreeChart getRetChart() {
		return retChart;
	}
	
	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}
}
