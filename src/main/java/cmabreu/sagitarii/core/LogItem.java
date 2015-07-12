package cmabreu.sagitarii.core;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LogItem {
	private String taskId;
	private String executorAlias;
	private String exitCode;
	private String macAddress;
	private List<String> console;
	private List<String> execLog;
	private Date time;
	
	public LogItem(String taskId, String executorAlias, String exitCode, String macAddress, List<String> console, List<String> execLog ) {
		this.taskId = taskId;
		this.executorAlias = executorAlias;
		this.exitCode = exitCode;
		this.console = console;
		this.execLog = execLog;
		this.macAddress = macAddress;
		this.time = Calendar.getInstance().getTime(); 
	}
	
	public Date getTime() {
		return time;
	}
	
	public String getMacAddress() {
		return macAddress;
	}
	
	public String getTaskId() {
		return taskId;
	}
	
	public String getExecutorAlias() {
		return executorAlias;
	}
	
	public String getExitCode() {
		return exitCode;
	}
	
	public List<String> getConsole() {
		return console;
	}
	
	public List<String> getExecLog() {
		return execLog;
	}
	
	
	
}
