package cmabreu.sagitarii.core;

import java.util.List;

import cmabreu.sagitarii.persistence.entity.Activity;
import cmabreu.sagitarii.persistence.entity.Pipeline;
import cmabreu.sagitarii.persistence.entity.Relation;

public class ReceivedData {

	private Activity activity;
	private Pipeline pipeline;
	private String macAddress;
	private String experimentSerial;
	private List<String> contentLines;
	private Relation table;
	
	public Relation getTable() {
		return table;
	}
	
	public boolean hasData() {
		return contentLines.size() > 0;
	}
	
	public ReceivedData( List<String> contentLines, String macAddress, Pipeline pipeline, Activity activity, Relation table, 
			String experimentSerial ) throws Exception {

		this.activity = activity;
		this.pipeline = pipeline;
		this.contentLines = contentLines;
		this.macAddress = macAddress;
		this.experimentSerial = experimentSerial;
		this.table = table;
		
	}
	
	public List<String> getContentLines() {
		return contentLines;
	}


	public String getExperimentSerial() {
		return experimentSerial;
	}


	public String getMacAddress() {
		return macAddress;
	}


	public Activity getActivity() {
		return activity;
	}
	
	public Pipeline getPipeline() {
		return pipeline;
	}
	
	
}
