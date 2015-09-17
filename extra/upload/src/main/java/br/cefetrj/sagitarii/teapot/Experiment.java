package br.cefetrj.sagitarii.teapot;

public class Experiment {
	private String tagExec;
	private String status;
	private String startDate;
	private String workflow;
	private String elapsedTime;
	
	public String getElapsedTime() {
		return elapsedTime;
	}
	
	public String getWorkflow() {
		return workflow;
	}
	
	public void setWorkflow(String workflow) {
		this.workflow = workflow;
	}
	
	public void setElapsedTime(String elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
	
	public String getStartDate() {
		return startDate;
	}
	
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getTagExec() {
		return tagExec;
	}
	
	public void setTagExec(String tagExec) {
		this.tagExec = tagExec;
	}

}
