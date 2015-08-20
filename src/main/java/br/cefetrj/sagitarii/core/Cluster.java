package br.cefetrj.sagitarii.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.cefetrj.sagitarii.core.delivery.InstanceDeliveryControl;
import br.cefetrj.sagitarii.core.types.ClusterStatus;
import br.cefetrj.sagitarii.core.types.ClusterType;
import br.cefetrj.sagitarii.core.types.InstanceStatus;
import br.cefetrj.sagitarii.metrics.MetricController;
import br.cefetrj.sagitarii.metrics.MetricType;
import br.cefetrj.sagitarii.misc.DateLibrary;
import br.cefetrj.sagitarii.misc.ProgressListener;
import br.cefetrj.sagitarii.misc.json.NodeTask;
import br.cefetrj.sagitarii.persistence.entity.Activity;
import br.cefetrj.sagitarii.persistence.entity.Instance;
import br.cefetrj.sagitarii.persistence.entity.LogEntry;
import br.cefetrj.sagitarii.persistence.services.LogService;

public class Cluster {
	private ClusterType type;
	private String soName;
	private String macAddress;
	private String ipAddress;
	private ClusterStatus status;
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
	private List<Instance> runningInstances;
	private boolean restartSignal = false;
	private boolean quitSignal = false;
	private boolean cleanWorkspaceSignal = false;
	private boolean reloadWrappersSignal = false;
	private boolean askingForInstance = false;
	private boolean mainCluster = false;
	private long freeMemory;
	private long freeDiskSpace;
	private long totalMemory;
	private long totalDiskSpace;
	private List<NodeTask> tasks;
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
	private List<ProgressListener> progressListeners;
	private List<String> log = new ArrayList<String>();
	private List<LogEntry> logEntries = new ArrayList<LogEntry>();
	private int counter = 0;
	
	public boolean signaled() {
		return ( restartSignal || quitSignal || cleanWorkspaceSignal || reloadWrappersSignal || askingForInstance );
		
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
	
	public void addProgressListener( ProgressListener listener ) {
		progressListeners.add( listener );
	}

	public List<ProgressListener> getProgressListeners() {
		return new ArrayList<ProgressListener>(progressListeners);
	}
	
	private void removeListeners() {
		int total = 0;
		try {
			Iterator<ProgressListener> i = progressListeners.iterator();
			while ( i.hasNext() ) {
				ProgressListener pl = i.next(); 
				if ( pl.getPercentage() > 95 ) {
					i.remove();
					total++;
				}
			}
		} catch ( Exception e ) { }
		if ( total > 0 ) {
			debug( total + " listeners deleted" );
		}
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
		quitSignal = true;
	}
	
	public void reloadWrappers() {
		reloadWrappersSignal = true;
	}
	
	public double getMemoryPercent() {
		double percent = 0;
		try {
			percent = Math.round( (freeMemory * 100 ) / totalMemory );
		} catch ( Exception ignored ) {}
		return percent;
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
		cleanWorkspaceSignal = true;
	}

	public boolean isAskingForInstance() {
		return askingForInstance;
	}
	
	public String getLostInstance() {
		return lostInstance;
	}
	
	public void informReport( String instanceSerial, String status ) {
		debug( instanceSerial + " status is " + status );
		askingForInstance = false;
		lostInstance = "";
		
		if ( status.equals("NOT_FOUND") ) {
			debug("resubmiting instance " + instanceSerial + " to job queue");
			cancelAndRemoveInstance( instanceSerial );
		} 
		
	}
	
	public void inform( String instanceSerial ) {
		logger.warn("asking Teapot for lost instance " + instanceSerial + " working at node " + macAddress );
		
		if ( status == ClusterStatus.DEAD ) {
			logger.warn("this node is DEAD. try to recover lost instance from output buffer");
			informReport( instanceSerial, "NOT_FOUND");
		}
		
		if ( askingForInstance ) {
			logger.warn("already waiting for instance " + lostInstance);
			return;
		}
		askingForInstance = true;
		lostInstance = instanceSerial;
	}
	
	public void restart() {
		restartSignal = true;
	}

	public boolean isMainCluster() {
		return mainCluster;
	}
	
	public void setAsMainCluster() {
		this.mainCluster = true;
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
			setMessage("No data produced by instance " + rd.getInstance().getSerial() + " (" + rd.getActivity().getTag() + ")" );
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
		
		MainLog.getInstance().storeLog( activity, experiment, rd.getCsvDataFile().getTaskId(), rd.getActivity().getExecutorAlias(), rd.getCsvDataFile().getExitCode(),
				rd.getMacAddress(), rd.getCsvDataFile().getConsole(), rd.getCsvDataFile().getExecLog() );
		setInstanceAsDone( instanceSerial, actvt );
	}
	
	
	public void setInstanceAsDone( String instanceSerial, Activity actvt ) {
		debug("checking if instance " + instanceSerial + " (" + actvt.getTag() + ") is done");
		for( Instance pipe : runningInstances ) {
			if ( pipe.getSerial().equals( instanceSerial ) ) {
				pipe.decreaseQtdActivations();
				String finished = pipe.getFinishedActivities();
				if ( finished == null ) { finished = ""; }
				if( actvt != null ) {
					pipe.setFinishedActivities( finished + " " + actvt.getTag() );
				}
				if( pipe.getQtdActivations() == 0 ) {
					debug("instance " + instanceSerial + " finished");
					processedPipes++;
					pipe.setStatus( InstanceStatus.FINISHED );
					pipe.setFinishDateTime( Calendar.getInstance().getTime() );
					pipe.setExecutedBy(macAddress);
					pipe.setCoresUsed( availableProcessors );
					Sagitarii.getInstance().finishInstance( pipe );
					InstanceDeliveryControl.getInstance().removeUnit( instanceSerial );
				} else {
					debug("instance " + instanceSerial + " (" + actvt.getTag() + ") have " + pipe.getQtdActivations() + " tasks running");
				}
				break;
			}
		}
	}
	
	private void debug (String s ) {
		logger.debug( s );
		setMessage( s );
	}
	
	public List<Instance> getRunningInstances() {
		return new ArrayList<Instance>( runningInstances );
	}
	
	public void addInstance( Instance pipe ) {
		runningInstances.add( pipe ); 
	}

	public void cancelAndRemoveInstance( String instanceSerial ) {
		for ( Instance instance : getRunningInstances() ) {
			if ( instance.getSerial().equalsIgnoreCase( instanceSerial ) ) {
				instance.setStatus( InstanceStatus.PIPELINED );
				runningInstances.remove( instance ); 
				InstanceDeliveryControl.getInstance().cancelUnit( instanceSerial );
				Sagitarii.getInstance().returnToBuffer(instance);
				break;
			}
		}
	}

	public Cluster(ClusterType type, String javaVersion, String soFamily, String macAddress, String ipAddress, String machineName, Double cpuLoad, 
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
		this.status = ClusterStatus.IDLE;
		this.maxAllowedTasks = maxAllowedTasks;
		this.runningInstances = new ArrayList<Instance>();
		this.progressListeners = new ArrayList<ProgressListener>(); 
		this.type = type;
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
	public ClusterStatus getStatus() {
		return status;
	}
	public void setStatus(ClusterStatus status) {
		this.status = status;
	}

	public String getMachineName() {
		return machineName;
	}

	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}

	public void setMaxAllowedTasks(int maxAllowedTasks) {
		this.maxAllowedTasks = maxAllowedTasks;
	}

	public int getMaxAllowedTasks() {
		return maxAllowedTasks;
	}

	public boolean isDead() {
		return this.status == ClusterStatus.DEAD;
	}
	
	public void updateStatus() {
		cleanUp();
		this.age++;
		if ( age > 5 ) {
			this.status = ClusterStatus.DEAD;
			clearSignals();
		} else { 
			if ( runningInstances.size() == 0 ) {
				this.status = ClusterStatus.IDLE;
			}		
			if ( runningInstances.size() > 0 ) {
				this.status = ClusterStatus.ACTIVE;
			}
		}
	}
	
	public String getLastAnnounce() {
		DateLibrary.getInstance().setTo( this.lastAnnounce );
		return DateLibrary.getInstance().getDateHourTextHuman();
	}

	private synchronized Instance getFinishedTask() {
		for ( Instance pipe : runningInstances ) {
			if ( ( pipe.getQtdActivations() == 0 ) || ( pipe.getStatus() == InstanceStatus.FINISHED ) ) {
				return pipe;
			}
		}
		return null;
	}
	
	private void cleanUp() {
		Instance pipe = getFinishedTask();
		while ( pipe != null ) {
			runningInstances.remove( pipe );
			pipe = getFinishedTask();
		}
		removeListeners();
	}
	
	public void setLastAnnounce(Date lastAnnounce) {
		this.age = 0;
		this.lastAnnounce = lastAnnounce;
		cleanUp();
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
			ls.insetLogEntryList( logEntries );
		} catch ( Exception e ) {
			setMessage("cannot save log activity: " + e.getMessage() );
		}
		logEntries.clear();
	}
	
	public void setMessage(String logItem) {
		
		DateLibrary dl = DateLibrary.getInstance();
		dl.setTo( new Date() );
		logItem = dl.getHourTextHuman() + " " + logItem;
		
		LogEntry le = new LogEntry();
		le.setDateTime( Calendar.getInstance().getTime() );
		le.setLog( logItem );
		le.setNode( macAddress );
		logEntries.add( le );
		
		counter++;
		if ( counter == 100 ) {
			flushLog();
			counter = 0;
		}
		
		this.lastError = logItem;
		if ( log.size() > 40 ) {
			log.remove(0);
		}
		log.add( logItem );
	}
	
	public ClusterType getType() {
		return type;
	}
	
	public void clearLog() {
		log.clear();
	}
	
	public List<String> getLog() {
		return new ArrayList<String>(log);
	}
	
	public void clearListeners() {
		progressListeners.clear();
	}
	
	public void clearTasks() {
		tasks.clear();
	}
	
}
