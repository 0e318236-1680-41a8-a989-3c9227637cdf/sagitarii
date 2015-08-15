package br.cefetrj.sagitarii.core;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.cefetrj.sagitarii.core.filetransfer.ReceivedFile;
import br.cefetrj.sagitarii.persistence.entity.Activity;
import br.cefetrj.sagitarii.persistence.entity.Instance;
import br.cefetrj.sagitarii.persistence.entity.Relation;

public class ReceivedData {

	private Activity activity;
	private Instance instance;
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
	
	public ReceivedData( List<String> contentLines, String macAddress, Instance instance, Activity activity, Relation table, 
			ReceivedFile csvDataFile ) {

		this.activity = activity;
		this.instance = instance;
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
	
	public Instance getInstance() {
		return instance;
	}
	

}
