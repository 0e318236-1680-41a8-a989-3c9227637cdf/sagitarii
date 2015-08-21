package br.cefetrj.sagitarii.misc.json;

import java.util.List;

public class NodeTasks {
	private String nodeId;
	private String cpuLoad;
	private String freeMemory;
	private String totalMemory;
	private String freeDiskSpace;
	private String totalDiskSpace;
	private String maximunLimit;
	private double memoryPercent; 
	private List<NodeTask> data;
	
	public void setMemoryPercent(double memoryPercent) {
		this.memoryPercent = memoryPercent;
	}
	
	public double getMemoryPercent() {
		return memoryPercent;
	}
	
	public long getFreeDiskSpace() {
		return Long.parseLong(freeDiskSpace);
	}
	
	public void setMaximunLimit(String maximunLimit) {
		this.maximunLimit = maximunLimit;
	}
	
	public int getMaximunLimit() {
		return Integer.parseInt( maximunLimit );
	}
	
	public long getTotalDiskSpace() {
		return Long.parseLong(totalDiskSpace);
	}

	public void setFreeDiskSpace(String freeDiskSpace) {
		this.freeDiskSpace = freeDiskSpace;
	}
	
	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public List<NodeTask> getData() {
		return data;
	}
	
	public void setData(List<NodeTask> data) {
		this.data = data;
	}

	public double getCpuLoad() {
		return Double.parseDouble( cpuLoad );
	}

	public void setCpuLoad(String cpuLoad) {
		this.cpuLoad = cpuLoad;
	}

	public long getFreeMemory() {
		return Long.parseLong( freeMemory );
	}

	public void setFreeMemory(String freeMemory) {
		this.freeMemory = freeMemory;
	}

	public long getTotalMemory() {
		return Long.parseLong( totalMemory );
	}

	public void setTotalMemory(String totalMemory) {
		this.totalMemory = totalMemory;
	}
	
}
