package br.cefetrj.parser;

import java.util.ArrayList;
import java.util.List;

public class Activity {
    private String operator;
    private String executor;
    private List<String> inputRelations;
    private String targetRelation;
    private String name;
    private List<Activity> previousActivities;
    
    public Activity() {
    	previousActivities = new ArrayList<Activity>();
    	inputRelations = new ArrayList<String>();
	}
    
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getExecutor() {
		return executor;
	}
	public void setExecutor(String executor) {
		this.executor = executor;
	}
	public List<String> getInputRelations() {
		return inputRelations;
	}
	public void setInputRelations(List<String> inputRelations) {
		this.inputRelations = inputRelations;
	}
	public String getTargetRelation() {
		return targetRelation;
	}
	public void setTargetRelation(String targetRelation) {
		this.targetRelation = targetRelation;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Activity> getPreviousActivities() {
		return previousActivities;
	}
	public void setPreviousActivities(List<Activity> previousActivities) {
		this.previousActivities = previousActivities;
	}
    
}
