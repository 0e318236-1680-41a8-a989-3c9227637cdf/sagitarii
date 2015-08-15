
package br.cefetrj.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.jfree.chart.JFreeChart;

import br.cefetrj.sagitarii.metrics.ExperimentTimes;
import br.cefetrj.sagitarii.persistence.entity.Workflow;
import br.cefetrj.sagitarii.persistence.services.WorkflowService;

@Action (value = "getExperimentTimes", results = { 
		@Result ( type="chart", params = {"width", "800", "height", "400"} , name = "ok")  } 
) 

@ParentPackage("chart")
public class GetExperimentTimesAction  {
	private JFreeChart chart;
	private int idWorkflow;
	
	public String execute () {
	
		try {
			WorkflowService ws = new WorkflowService();
			Workflow workflow = ws.getWorkflow(idWorkflow);
			
			
			if ( workflow != null ) {
				ExperimentTimes et = new ExperimentTimes();
				chart = et.getImage( workflow );
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return "ok";
	}

	
	public JFreeChart getChart() {
		return chart;
	}
	
	public void setIdWorkflow(int idWorkflow) {
		this.idWorkflow = idWorkflow;
	}
	
}
