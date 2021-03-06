package br.cefetrj.sagitarii.core.filetransfer;

import java.util.ArrayList;
import java.util.List;

public class ReceivedFile {
	private String experimentSerial;
	private String sessionSerial;
	private String targetTable;
	private String fileName;
	private String type;
	private String activity;
	private String instance;
	private String macAddress;
	private String fragment;
	private String taskId;
	private String exitCode;
	private List<String> console;
	private List<String> execLog;
	private String realStartTime;
	private String realFinishTime;
	private String cpuCost;
	
	public void setCpuCost(String cpuCost) {
		this.cpuCost = cpuCost;
	}
	
	public String getCpuCost() {
		return cpuCost;
	}

	public ReceivedFile() {
		exitCode = "0";
		console = new ArrayList<String>();
		execLog = new ArrayList<String>();
		
	}
	
	public String getRealFinishTime() {
		return realFinishTime;
	}
	
	public String getRealStartTime() {
		return realStartTime;
	}
	
	public void setRealFinishTime(String realFinishTime) {
		this.realFinishTime = realFinishTime;
	}
	
	public void setRealStartTime(String realStartTime) {
		this.realStartTime = realStartTime;
	}
	
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getExitCode() {
		return exitCode;
	}

	public void setExitCode(String exitCode) {
		this.exitCode = exitCode;
	}

	public List<String> getConsole() {
		return console;
	}

	public void setConsole(List<String> console) {
		this.console = console;
	}
	
	public void setExecLog(List<String> execLog) {
		this.execLog = execLog;
	}
	
	public List<String> getExecLog() {
		return execLog;
	}

	public String getFragment() {
		return fragment;
	}
	
	public void setFragment(String fragment) {
		this.fragment = fragment;
	}
	
	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getInstance() {
		return instance;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getExperimentSerial() {
		return experimentSerial;
	}
	
	public void setExperimentSerial(String experimentSerial) {
		this.experimentSerial = experimentSerial;
	}
	
	public String getSessionSerial() {
		return sessionSerial;
	}
	
	public void setSessionSerial(String sessionSerial) {
		this.sessionSerial = sessionSerial;
	}
	
	public String getTargetTable() {
		return targetTable;
	}
	
	public void setTargetTable(String targetTable) {
		this.targetTable = targetTable;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
}
