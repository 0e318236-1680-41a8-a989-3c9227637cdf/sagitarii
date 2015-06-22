package cmabreu.sagitarii.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmabreu.sagitarii.core.delivery.InstanceDeliveryControl;
import cmabreu.sagitarii.core.types.ClusterStatus;
import cmabreu.sagitarii.core.types.InstanceStatus;
import cmabreu.sagitarii.metrics.MetricController;
import cmabreu.sagitarii.metrics.MetricType;
import cmabreu.sagitarii.misc.DateLibrary;
import cmabreu.sagitarii.misc.json.NodeTask;
import cmabreu.sagitarii.persistence.entity.Activity;
import cmabreu.sagitarii.persistence.entity.Instance;

public class Cluster {
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
	private List<Instance> runningInstances;
	private boolean restartSignal = false;
	private boolean quitSignal = false;
	private boolean cleanWorkspaceSignal = false;
	private boolean reloadWrappersSignal = false;
	private boolean mainCluster = false;
	private long freeMemory;
	private long totalMemory;
	private List<NodeTask> tasks;
	private Logger logger = LogManager.getLogger( this.getClass().getName() );

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
	
	public void setTotalMemory(long totalMemory) {
		this.totalMemory = totalMemory;
	}
	
	public void setFreeMemory(long freeMemory) {
		this.freeMemory = freeMemory;
	}
	
	public void cleanWorkspace() {
		cleanWorkspaceSignal = true;
	}


	public void inform( String instanceSerial ) {
		logger.warn("remember to ask Teapot for instance " + instanceSerial );
		//logger.warn( "INFORM#" + instanceSerial );
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
		
		setInstanceAsDone( rd );
		
		if ( rd.hasData() ) {
			logger.debug( "[" + this.macAddress +  "] data received from instance " + rd.getInstance().getSerial() + " (" + rd.getActivity().getTag() + ") is done");
		} else {
			logger.error( "[" + this.macAddress +  "] no data produced by instance " + rd.getInstance().getSerial() + " (" + rd.getActivity().getTag() + ")" );
			lastError = "No data produced by instance " + rd.getInstance().getSerial() + " (" + rd.getActivity().getTag() + ")";
		}
		
		if ( !rd.getCsvDataFile().getExitCode().equals("0") ) {
			lastError = "Exit Error: " + rd.getInstance().getSerial() + " (" + rd.getActivity().getTag() + ")";
		}
		
		MetricController.getInstance().hit( this.machineName, MetricType.NODE );
		MetricController.getInstance().hit( rd.getActivity().getTag(), MetricType.ACTIVITY_TAG );
		MetricController.getInstance().hit( rd.getActivity().getType().toString(), MetricType.ACTIVITY_TYPE );
		
		return true;
	}
	

	public void setInstanceAsDone( ReceivedData rd ) {
		String instanceSerial = rd.getInstance().getSerial();
		Activity actvt = rd.getActivity();
		MainLog.getInstance().storeLog( rd );
		setInstanceAsDone( instanceSerial, actvt );
	}
	
	
	public void setInstanceAsDone( String instanceSerial, Activity actvt ) {
		logger.debug("checking if instance " + instanceSerial + " (" + actvt.getTag() + ") is done");
		for( Instance pipe : runningInstances ) {
			if ( pipe.getSerial().equals( instanceSerial ) ) {
				pipe.decreaseQtdActivations();
				String finished = pipe.getFinishedActivities();
				if ( finished == null ) { finished = ""; }
				if( actvt != null ) {
					pipe.setFinishedActivities( finished + " " + actvt.getTag() );
				}
				if( pipe.getQtdActivations() == 0 ) {
					logger.debug("instance " + instanceSerial + " finished");
					processedPipes++;
					pipe.setStatus( InstanceStatus.FINISHED );
					
					Sagitarii.getInstance().finishInstance( pipe );
					InstanceDeliveryControl.getInstance().removeUnit( instanceSerial );
				} else {
					logger.debug("instance " + instanceSerial + " (" + actvt.getTag() + ") have " + pipe.getQtdActivations() + " tasks running");
				}
				break;
			}
		}
	}
	
	public List<Instance> getRunningInstances() {
		return new ArrayList<Instance>( runningInstances );
	}
	
	public void addInstance( Instance pipe ) {
		pipe.setStartDateTime( Calendar.getInstance().getTime() );
		runningInstances.add( pipe ); 
	}

	public void cancelAndRemoveInstance( String instanceSerial ) {
		for ( Instance instance : getRunningInstances() ) {
			if ( instance.getSerial().equalsIgnoreCase( instanceSerial ) ) {
				instance.setStatus( InstanceStatus.PIPELINED );
				runningInstances.remove( instance ); 
				break;
			}
		}
	}

	public Cluster(String javaVersion, String soFamily, String macAddress, String ipAddress, String machineName, Double cpuLoad, 
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
		runningInstances = new ArrayList<Instance>();
	}
	

	public String getMacAddress() {
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
	
	public void setLastError(String lastError) {
		this.lastError = lastError;
	}
}
