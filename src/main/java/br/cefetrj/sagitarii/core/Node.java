package br.cefetrj.sagitarii.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.cefetrj.sagitarii.core.delivery.InstanceDeliveryControl;
import br.cefetrj.sagitarii.core.types.NodeStatus;
import br.cefetrj.sagitarii.core.types.NodeType;
import br.cefetrj.sagitarii.core.types.InstanceStatus;
import br.cefetrj.sagitarii.core.types.LogType;
import br.cefetrj.sagitarii.metrics.MetricController;
import br.cefetrj.sagitarii.metrics.MetricType;
import br.cefetrj.sagitarii.metrics.NodeLoadMonitorEntity;
import br.cefetrj.sagitarii.metrics.NodeVMMonitorEntity;
import br.cefetrj.sagitarii.misc.DateLibrary;
import br.cefetrj.sagitarii.misc.json.NodeTask;
import br.cefetrj.sagitarii.persistence.entity.Activity;
import br.cefetrj.sagitarii.persistence.entity.Instance;
import br.cefetrj.sagitarii.persistence.entity.LogEntry;
import br.cefetrj.sagitarii.persistence.services.LogService;

public class Node {
	private NodeType type;
	private String soName;
	private String macAddress;
	private String ipAddress;
	private NodeStatus status;
	private String machineName;
	private Date lastAnnounce;
	private Integer cpuLoad;
	private int availableProcessors;
	private int age;
    private String javaVersion;
    private String soFamily;
    private int maxAllowedTasks;
    private int processedPipes = 0;
    private String lastError = "";
    private String lostInstance = "";
    
    // Instances Sagitarii sent to Node
	private List<Instance> runningInstances;
	// Tasks ( Activities ) running on this node ( informed by node itself )
	private List<NodeTask> tasks;
	
		
	private List<String> log = new ArrayList<String>();
	private List<LogEntry> logEntries = new ArrayList<LogEntry>();
	private boolean restartSignal = false;
	private boolean quitSignal = false;
	private boolean cleanWorkspaceSignal = false;
	private boolean reloadWrappersSignal = false;
	
	private long freeMemory;
	private long freeDiskSpace;
	private long totalMemory;
	private long totalDiskSpace;
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
	private int counter = 0;
	private NodeLoadMonitorEntity metrics;
	private NodeVMMonitorEntity metricsVmRam;
	private double memoryPercent;
	private String timeWhenGoesDead = "";
	
	
	
	public void setMemoryPercent(double memoryPercent) {
		this.memoryPercent = memoryPercent;
	}

	public void saveMetricImages( String path ) throws Exception {
		metrics.saveImage(path);
		metricsVmRam.saveImage(path);
	}

	public boolean signaled() {
		return ( restartSignal || quitSignal || cleanWorkspaceSignal /* || reloadWrappersSignal */ );
		
	}
	
	public void setFreeDiskSpace(long freeDiskSpace) {
		this.freeDiskSpace = freeDiskSpace;
	}
	
	public void setTotalDiskSpace(long totalDiskSpace) {
		this.totalDiskSpace = totalDiskSpace;
	}

	public long getFreeDiskSpace() {
		return freeDiskSpace;
	}
	
	public long getTotalDiskSpace() {
		return totalDiskSpace;
	}
	
	public void setTasks(List<NodeTask> tasks) {
		this.tasks = tasks;
	}
	
	public List<NodeTask> getTasks() {
		if ( tasks == null ) {
			tasks = new ArrayList<NodeTask>();
			return tasks;
		}
		return new ArrayList<NodeTask>( tasks );
	}
	
	public void quit() {
		setMessage(LogType.SYSTEM, "SIGNALED: Quit");
		quitSignal = true;
	}
	
	public void reloadWrappers() {
		setMessage(LogType.SYSTEM, "SIGNALED: Reload Wrappers");
		reloadWrappersSignal = true;
	}
	
	public double getMemoryPercent() {
		return Math.ceil( memoryPercent );
	}
	
	public double getDiskPercent() {
		double percent = 0;
		try {
			percent = Math.round( (freeDiskSpace * 100 ) / totalDiskSpace );
		} catch ( Exception ignored ) {}
		return percent;
	}

	public void setTotalMemory(long totalMemory) {
		this.totalMemory = totalMemory;
	}
	
	public void setFreeMemory(long freeMemory) {
		this.freeMemory = freeMemory;
	}
	
	public void cleanWorkspace() {
		setMessage(LogType.SYSTEM, "SIGNALED: Clear Workspace");
		cleanWorkspaceSignal = true;
	}

	public String getLostInstance() {
		return lostInstance;
	}
	
	public void restart() {
		setMessage(LogType.SYSTEM, "SIGNALED: Restart");
		restartSignal = true;
	}

	public boolean isRestartSignal() {
		return restartSignal;
	}
	
	public boolean isReloadWrappersSignal() {
		return reloadWrappersSignal;
	}
	
	public boolean isQuitSignal() {
		return quitSignal;
	}
	
	public boolean isCleanWorkspaceSignal() {
		return cleanWorkspaceSignal;
	}
	
	public void clearSignals() {
		restartSignal = false;
		quitSignal = false;
		cleanWorkspaceSignal = false;
		reloadWrappersSignal = false;
	}
	
	public long getTotalMemory() {
		return totalMemory / 1048576;
	}
	
	public long getFreeMemory() {
		return freeMemory / 1048576;
	}
	
	public synchronized boolean confirmReceiveData( ReceivedData rd ) throws Exception {
		setLastAnnounce( Calendar.getInstance().getTime() );
		if ( rd.hasData() ) {
			debug( "[" + this.macAddress +  "] data received from instance " + rd.getInstance().getSerial() + " (" + rd.getActivity().getTag() + ") is done");
		} else {
			logger.error( "[" + this.macAddress +  "] no data produced by instance " + rd.getInstance().getSerial() + " (" + rd.getActivity().getTag() + ")" );
			setMessage(LogType.ACTIVITY_ERROR, "No data produced by instance " + rd.getInstance().getSerial() + " (" + rd.getActivity().getTag() + ")", rd.getActivity().getSerial() );
		}
		
		MetricController.getInstance().hit( this.machineName, MetricType.NODE );
		MetricController.getInstance().hit( rd.getActivity().getTag(), MetricType.ACTIVITY_TAG );
		MetricController.getInstance().hit( rd.getActivity().getType().toString(), MetricType.ACTIVITY_TYPE );
		
		finishInstance( rd );
		
		return true;
	}
	

	public void finishInstance( ReceivedData rd ) {
		String instanceSerial = rd.getInstance().getSerial();
		String experiment = rd.getCsvDataFile().getExperimentSerial();
		Activity actvt = rd.getActivity();
		String activity = actvt.getTag();
		String startTimeMillis = rd.getCsvDataFile().getRealStartTime();
		String finishTimeMillis = rd.getCsvDataFile().getRealFinishTime();
		String cpuCost = rd.getCsvDataFile().getCpuCost();

		logger.debug("finishing instance " + instanceSerial );
		
		// Do not pass rd to the log. Will avoid GC to clean it until Log flush this entry.
		List<String> console = new ArrayList<String>( rd.getCsvDataFile().getConsole() );
		List<String> execLog = new ArrayList<String>( rd.getCsvDataFile().getExecLog() );
		
		MainLog.getInstance().storeLog( activity, experiment, rd.getCsvDataFile().getTaskId(), rd.getActivity().getExecutorAlias(), rd.getCsvDataFile().getExitCode(),
				rd.getMacAddress(), console, execLog );
		
		if ( !rd.getCsvDataFile().getExitCode().equals("0") ) {
			StringBuilder consoleS = new StringBuilder();
			StringBuilder execLogS = new StringBuilder();
			for ( String s : console ) {
				consoleS.append( s + "\n");
			}
			for ( String s : execLog ) {
				execLogS.append( s + "\n");
			}
			setMessage( LogType.ACTIVITY_ERROR, "NO CSV DATA: Experiment: " + experiment + " Activity: " + activity + " Node: " + rd.getMacAddress() +
					consoleS.toString() + "\n\n" + execLogS.toString(), actvt.getSerial() );
		}
		
		setInstanceAsDone( instanceSerial, actvt, startTimeMillis, finishTimeMillis, cpuCost);
		
		cleanUp();
		
	}
	
	
	public void setInstanceAsDone( String instanceSerial, Activity actvt, String startTimeMillis, 
			String finishTimeMillis, String cpuCost ) {
		
		debug( macAddress + ": checking if instance " + instanceSerial + " (" + actvt.getTag() + ") is done");
		boolean found = false;
		for( Instance instance : getRunningInstances() ) {
			if ( instance.getSerial().equals( instanceSerial ) ) {
				logger.debug( macAddress + ": instance " + instanceSerial + " found in node " + macAddress + " execution list. try to finish.");
				found = true;
				instance.decreaseQtdActivations();
				String finished = instance.getFinishedActivities();
				if ( finished == null ) { finished = ""; }
				if( actvt != null ) {
					instance.setFinishedActivities( finished + " " + actvt.getTag() );
				}
				if( instance.getQtdActivations() == 0 ) {
					debug("instance " + instanceSerial + " finished");
					processedPipes++;
					instance.setStatus( InstanceStatus.FINISHED );
					instance.setFinishDateTime( Calendar.getInstance().getTime() );
					instance.setExecutedBy(macAddress);
					
					instance.setCoresUsed( NodesManager.getInstance().getCores() );
					
					instance.setRealStartTimeMillis( Long.valueOf( startTimeMillis) );
					instance.setRealFinishTimeMillis( Long.valueOf( finishTimeMillis ) );
					
					try {
						instance.setCpuCost( Double.valueOf( cpuCost ) );
					} catch ( Exception e ) {
						logger.error("invalid CPU cost from node " + macAddress + ": " + cpuCost );
					}
					
					Sagitarii.getInstance().finishInstance( instance );
					InstanceDeliveryControl.getInstance().removeUnit( instanceSerial );
					
				} else {
					debug("instance " + instanceSerial + " (" + actvt.getTag() + ") have " + instance.getQtdActivations() + " tasks running");
				}
				break;
			}
		}
		if ( !found ) {
			logger.error("instance " + instanceSerial + " not found in node " + macAddress + " execution list");
		}
	}
	
	private void debug (String s ) {
		logger.debug( s );
		setMessage( LogType.SYSTEM, s );
	}
	
	public synchronized List<Instance> getRunningInstances() {
		return new ArrayList<Instance>( runningInstances );
	}
	
	public synchronized void addInstance( Instance pipe ) {
		logger.debug( macAddress + ": Adding Instance " + pipe.getSerial() + " to the running list");
		pipe.setExecutedBy(macAddress);
		runningInstances.add( pipe ); 
	}

	public void resubmitInstanceToBuffer( String instanceSerial ) {
		logger.error( macAddress + ": Resubmit Instance " + instanceSerial + " to Sagitarii buffer..." );
		for ( Instance instance : getRunningInstances() ) {
			if ( instance.getSerial().equalsIgnoreCase( instanceSerial ) ) {
				
				int timesTried = instance.getTimesTried();
				if( timesTried > 5 ) {
					logger.error( macAddress + ": Tried too much. Cancel instance " + instanceSerial + " execution." );
					Sagitarii.getInstance().finishInstance( instance );
					runningInstances.remove( instance );
					InstanceDeliveryControl.getInstance().cancelUnit( instanceSerial );					
					setMessage( LogType.ACTIVITY_ERROR, "Instance " + instanceSerial + " canceled due to errors. ");
					return;
				} else {
					instance.setStatus( InstanceStatus.PIPELINED );
					runningInstances.remove( instance ); 
					InstanceDeliveryControl.getInstance().cancelUnit( instanceSerial );
					Sagitarii.getInstance().returnToBuffer(instance);
					logger.error("Instance " + instanceSerial + " found in this node buffer. Returned to Sagitarii output buffer");
					setMessage( LogType.SYSTEM, "[" + timesTried + "] Instance " + instanceSerial + " returned to buffer due to errors.");
					return;
				}
			}
		}
		logger.error( macAddress + ": Instance " + instanceSerial + " is not in Node buffer.");
	}

	public Node(NodeType type, String javaVersion, String soFamily, String macAddress, String ipAddress, String machineName, Double cpuLoad, 
			String soName, int availableProcessors,  int maxAllowedTasks, long freeMemory, long totalMemory) {
		this.soName = soName;
		this.macAddress = macAddress;
		this.ipAddress = ipAddress;
		this.machineName = machineName;
		this.cpuLoad = cpuLoad.intValue();
		this.lastAnnounce = new Date();
		this.availableProcessors = availableProcessors;
		this.age = 0;
		this.javaVersion = javaVersion;
		this.soFamily = soFamily;
		this.freeMemory = freeMemory;
		this.totalMemory = totalMemory;
		this.status = NodeStatus.IDLE;
		this.maxAllowedTasks = maxAllowedTasks;
		this.runningInstances = new ArrayList<Instance>();
		this.type = type;
		metrics = new NodeLoadMonitorEntity( macAddress, MetricType.NODE_LOAD );
		metricsVmRam = new NodeVMMonitorEntity( macAddress, MetricType.NODE_LOAD );
		
		// Just to create a new image.
		MetricController.getInstance().hit( this.machineName, MetricType.NODE );
		
		
	}
	
	private void addMetrics(  double valueCpu, double valueRam, double valueTasks ) {
		metrics.set(valueCpu, valueRam, valueTasks);
		metricsVmRam.set( getTotalMemory() );
	}

	public String getmacAddress() {
		return macAddress;
	}
	
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	public NodeStatus getStatus() {
		return status;
	}

	public String getMachineName() {
		return machineName;
	}

	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}

	public void setMaxAllowedTasks(int maxAllowedTasks) {
		if ( maxAllowedTasks != this.maxAllowedTasks ) {
			debug("Max tasks limit changed from " + this.maxAllowedTasks + " to " + maxAllowedTasks + 
					" [ RAM:" + memoryPercent + "% / CPU: " + cpuLoad + "% ]" );
		}
		this.maxAllowedTasks = maxAllowedTasks;
	}

	public int getMaxAllowedTasks() {
		return maxAllowedTasks;
	}

	public boolean isDead() {
		return this.status == NodeStatus.DEAD;
	}
	
	public void updateStatus( boolean useMetrics ) {
		cleanUp();
		NodeStatus oldStatus = this.status;
		this.age++;
		if ( age > 10 ) {
			this.status = NodeStatus.DEAD;
			clearSignals();
		} else { 
			if ( tasks.size() == 0 ) {
				this.status = NodeStatus.IDLE;
			}		
			if ( tasks.size() > 0 ) {
				this.status = NodeStatus.ACTIVE;
			}
		}
		
		if ( ( runningInstances.size() > 0) && ( tasks.size() == 0 ) ) {
			logger.error("Node " + macAddress + " have no tasks running but instances in buffer:");
			setMessage( LogType.NODE_STATUS, "Inconsistent state!" );
			for ( Instance ins : runningInstances ) {
				logger.error(" > " + ins.getSerial() );
			}
		}
		
		if ( oldStatus != this.status ) {
			if ( this.status == NodeStatus.DEAD ) {
				setMessage( LogType.NODE_STATUS, "Node " + macAddress + " is now OFFLINE." );
				timeWhenGoesDead = DateLibrary.getInstance().getDateHourTextHuman();
			}
			if ( oldStatus == NodeStatus.DEAD ) {
				setMessage( LogType.NODE_STATUS, "Node " + macAddress + " was offline since " + timeWhenGoesDead + 
						" and is now as " + this.status );
				timeWhenGoesDead = "";
			}
		}

		if ( useMetrics ) {
			try {
				//System.out.println(macAddress + " | " + cpuLoad + " | " + memoryPercent + " | " + tasks.size() );
				addMetrics( cpuLoad, memoryPercent, tasks.size() );
			} catch ( Exception e ) {
				
			}
		}

	}
	
	public String getLastAnnounce() {
		DateLibrary.getInstance().setTo( this.lastAnnounce );
		return DateLibrary.getInstance().getDateHourTextHuman();
	}

	private synchronized Instance getFinishedTask() {
		for ( Instance pipe : getRunningInstances() ) {
			if ( ( pipe.getQtdActivations() == 0 ) || ( pipe.getStatus() == InstanceStatus.FINISHED ) ) {
				return pipe;
			}
		}
		return null;
	}

	public synchronized boolean isRunning( String instanceSerial ) {
		logger.debug(macAddress + ": is Instance " + instanceSerial + " running here?");
		List<Instance> instances = getRunningInstances();
		for ( Instance pipe : instances ) {
			logger.debug("   > " + macAddress + ": " + pipe.getSerial() + " : " + pipe.getStatus() );
			if ( pipe.getSerial().equals( instanceSerial ) ) {
				if ( pipe.getStatus() == InstanceStatus.RUNNING ) {
					logger.debug(macAddress + ": Instance " + instanceSerial + " IS running here.");
					return true;
				} else {
					logger.debug(macAddress + ": Instance " + instanceSerial + " is here but status is " + pipe.getStatus() );
					return false;
				}
			}
		}
		logger.debug( macAddress + ": Instance " + instanceSerial + " is NOT running here.");
		return false;
	}

	private void cleanUp() {
		Instance pipe = getFinishedTask();
		while ( pipe != null ) {
			runningInstances.remove( pipe );
			logger.debug("instance " + pipe.getSerial() + " removed from running list because is finished.");
			InstanceDeliveryControl.getInstance().cancelUnit( pipe.getSerial() );
			pipe = getFinishedTask();
		}
	}
	
	public void setLastAnnounce(Date lastAnnounce) {
		this.age = 0;
		this.lastAnnounce = lastAnnounce;
	}

	public Integer getCpuLoad() {
		return cpuLoad;
	}

	public void setCpuLoad(Double cpuLoad) {
		this.cpuLoad = cpuLoad.intValue();
	}

	public int getAge() {
		return age;
	}

	public String getSoName() {
		return soName;
	}

	public void setSoName(String soName) {
		this.soName = soName;
	}

	public int getAvailableProcessors() {
		return availableProcessors;
	}

	public void setAvailableProcessors(int availableProcessors) {
		this.availableProcessors = availableProcessors;
	}

	public String getJavaVersion() {
		return javaVersion;
	}

	public String getSoFamily() {
		return soFamily;
	}

	public int getProcessedPipes() {
		return processedPipes;
	}

	public String getLastError() {
		return lastError;
	}
	
	private void flushLog() {
		try {
			LogService ls = new LogService();
			ls.insertLogEntryList( logEntries );
		} catch ( Exception e ) {
			e.printStackTrace();
			logger.error("cannot save log activity. See server log to details.");
		}
		logEntries.clear();
	}

	private void setMessage(LogType type, String logItem) {
		setMessage( type, logItem, "");
	}
	
	public void setMessage(LogType type, String logItem, String activitySerial) {
		
		DateLibrary dl = DateLibrary.getInstance();
		dl.setTo( new Date() );
		logItem = dl.getHourTextHuman() + " " + logItem;
		
		LogEntry le = new LogEntry();
		le.setDateTime( Calendar.getInstance().getTime() );
		le.setLog( logItem );
		le.setType( type );
		le.setNode( macAddress );
		le.setActivitySerial( activitySerial);
		logEntries.add( le );
		
		counter++;
		if ( (counter == 100) || (type == LogType.NODE_STATUS) ) {
			flushLog();
			counter = 0;
		} 
		
		this.lastError = logItem;
		if ( log.size() > 40 ) {
			log.remove(0);
		}
		log.add( logItem );
	}
	
	public NodeType getType() {
		return type;
	}
	
	public void clearLog() {
		log.clear();
	}
	
	public List<String> getLog() {
		return new ArrayList<String>(log);
	}
	
	public void clearTasks() {
		tasks.clear();
	}
	
	public String getTimeWhenGoesDead() {
		return timeWhenGoesDead;
	}
	
}