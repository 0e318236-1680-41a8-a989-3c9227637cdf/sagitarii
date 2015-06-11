package cmabreu.sagitarii.action;

import java.util.List;
import java.util.Queue;

import cmabreu.sagitarii.core.Sagitarii;
import cmabreu.sagitarii.core.config.Configurator;
import cmabreu.sagitarii.persistence.entity.Experiment;
import cmabreu.sagitarii.persistence.entity.Instance;
import cmabreu.sagitarii.persistence.entity.User;

import com.opensymphony.xwork2.ActionContext;

public class BasicActionClass {
	private List<Experiment> runningExperiments;
	private Queue<Instance> instanceInputBuffer;
	private Queue<Instance> instanceJoinInputBuffer;
	private Queue<Instance> instanceOutputBuffer;	
	private Experiment experimentOnTable;
	private Experiment experimentOnTableJoin;
	private int maxBufferCapacity;
	private User loggedUser;
	private long freeMemory = 0;
	private long totalMemory = 0;
	private double cpuLoad = 0;
	
	public double getMemoryPercent() {
		double percent = Math.round( (freeMemory * 100 ) / totalMemory );
		return percent;
	}

	public long getFreeMemory() {
		return freeMemory / 1048576;
	}
	
	public long getTotalMemory() {
		return totalMemory / 1048576;
	}
	
	public double getCpuLoad() {
		return cpuLoad;
	}
	
	public User getLoggedUser() {
		loggedUser = (User)ActionContext.getContext().getSession().get("loggedUser");
		return loggedUser;
	}
	
	public void setMessageText(String messageText) {
		messageText = messageText.replaceAll("[\n\r]", "");
		ActionContext.getContext().getSession().put("messageText", messageText );
	}

	public String getMessageText() {
		String messageText = (String)ActionContext.getContext().getSession().get("messageText");
		setMessageText("");
		return messageText;
	}

	public BasicActionClass() {
		Sagitarii sagi = Sagitarii.getInstance();
		runningExperiments = sagi.getRunningExperiments();
		instanceInputBuffer = sagi.getInstanceInputBuffer();
		instanceJoinInputBuffer = sagi.getInstanceJoinInputBuffer();
		instanceOutputBuffer = sagi.getInstanceOutputBuffer();
		experimentOnTable = sagi.getExperimentOnTable();
		experimentOnTableJoin = sagi.getExperimentOnTableJoin();
		maxBufferCapacity = sagi.getMaxInputBufferCapacity();
		
		try {
			freeMemory = Configurator.getInstance().getFreeMemory();
			totalMemory = Configurator.getInstance().getTotalMemory();
			cpuLoad = Configurator.getInstance().getProcessCpuLoad();
		} catch ( Exception e ) {  }	
		
	}

	public List<Experiment> getRunningExperiments() {
		return runningExperiments;
	}

	public Queue<Instance> getInstanceInputBuffer() {
		return instanceInputBuffer;
	}

	public Queue<Instance> getInstanceJoinInputBuffer() {
		return instanceJoinInputBuffer;
	}

	public Queue<Instance> getInstanceOutputBuffer() {
		return instanceOutputBuffer;
	}

	public int getMaxBufferCapacity() {
		return maxBufferCapacity;
	}
	
	public Experiment getExperimentOnTable() {
		return experimentOnTable;
	}
	
	public Experiment getExperimentOnTableJoin() {
		return experimentOnTableJoin;
	}
}
