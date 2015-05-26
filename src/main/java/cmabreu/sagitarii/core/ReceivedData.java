package cmabreu.sagitarii.core;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cmabreu.sagitarii.core.sockets.ReceivedFile;
import cmabreu.sagitarii.persistence.entity.Activity;
import cmabreu.sagitarii.persistence.entity.Pipeline;
import cmabreu.sagitarii.persistence.entity.Relation;

public class ReceivedData {

	private Activity activity;
	private Pipeline instance;
	private String macAddress;
	private List<String> contentLines;
	private Relation table;
	private ReceivedFile csvDataFile;
	private Date time;
	
	public Date getTime() {
		return time;
	}
	
	public Relation getTable() {
		return table;
	}
	
	public boolean hasData() {
		return contentLines.size() > 0;
	}
	
	public ReceivedFile getCsvDataFile() {
		return csvDataFile;
	}
	
	public ReceivedData( List<String> contentLines, String macAddress, Pipeline pipeline, Activity activity, Relation table, 
			ReceivedFile csvDataFile ) throws Exception {

		this.activity = activity;
		this.instance = pipeline;
		this.contentLines = contentLines;
		this.macAddress = macAddress;
		this.csvDataFile = csvDataFile;
		this.table = table;
		this.time = Calendar.getInstance().getTime();
		
	}
	
	public List<String> getContentLines() {
		return contentLines;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public Activity getActivity() {
		return activity;
	}
	
	public Pipeline getInstance() {
		return instance;
	}
	

}
