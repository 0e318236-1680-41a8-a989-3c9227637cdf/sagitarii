package br.cefetrj.sagitarii.action;

import java.util.List;

import br.cefetrj.sagitarii.core.Sagitarii;
import br.cefetrj.sagitarii.core.config.Configurator;
import br.cefetrj.sagitarii.persistence.entity.Experiment;
import br.cefetrj.sagitarii.persistence.entity.User;

import com.opensymphony.xwork2.ActionContext;

public class BasicActionClass {
	private List<Experiment> runningExperiments;
	private int maxBufferCapacity;
	private User loggedUser;
	private long freeMemory = 0;
	private long totalMemory = 0;
	private double cpuLoad = 0;
	private double systemSpeedUp = 0;
	private double systemEfficiency = 0;
	private String useDLB;
	private String announceUrl;
	
	public String getUseDLB() {
		return useDLB;
	}

	public double getSystemEfficiency() {
		return systemEfficiency;
	}
	
	public double getSystemSpeedUp() {
		return systemSpeedUp;
	}
	
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
		useDLB = "No";
		try {
			if ( Configurator.getInstance().useDynamicLoadBalancer() ) {
				useDLB = "Yes";
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
				
		runningExperiments = sagi.getRunningExperiments();
		maxBufferCapacity = sagi.getMaxInputBufferCapacity();
		systemSpeedUp = sagi.getSystemSpeedUp();
		systemEfficiency = sagi.getSystemEfficiency();
		announceUrl = sagi.getTracker().getAnnounceUrl();
		
		try {
			freeMemory = Configurator.getInstance().getFreeMemory();
			totalMemory = Configurator.getInstance().getTotalMemory();
			cpuLoad = Configurator.getInstance().getProcessCpuLoad();
		} catch ( Exception e ) {  }	
		
	}

	public List<Experiment> getRunningExperiments() {
		return runningExperiments;
	}

	public int getMaxBufferCapacity() {
		return maxBufferCapacity;
	}
	
	public String getAnnounceUrl() {
		return announceUrl;
	}
	
}
