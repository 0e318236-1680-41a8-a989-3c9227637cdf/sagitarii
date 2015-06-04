package cmabreu.sagitarii.misc.json;

import java.util.List;

public class NodeTasks {
	private String nodeId;
	private List<NodeTask> data;
	
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

}
