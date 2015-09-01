
package br.cefetrj.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.jfree.chart.JFreeChart;

import br.cefetrj.sagitarii.core.ClustersManager;

@Action (value = "getNodeRamMetrics", results = { 
		@Result ( type="chart", params = {"width", "400", "height", "200"} , name = "ok") }, 
		interceptorRefs= { @InterceptorRef("seguranca") } 
) 

@ParentPackage("chart")
public class GetNodeRamMetricsAction  {
	private JFreeChart chart;
	private String macAddress;
	private String time;
	
	public String execute () {
		try {
			if ( macAddress != null ) {
				chart = ClustersManager.getInstance().getCluster(macAddress).getRamMetrics().getImage();
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return "ok";
	}

	
	public JFreeChart getChart() {
		return chart;
	}
	
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
}
