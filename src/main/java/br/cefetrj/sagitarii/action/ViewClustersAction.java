
package br.cefetrj.sagitarii.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.core.Node;
import br.cefetrj.sagitarii.core.NodesManager;
import br.cefetrj.sagitarii.persistence.HDFS;
import br.cefetrj.sagitarii.persistence.HDFS.HdfsData;

@Action (value = "viewClusters", results = { @Result (location = "viewClusters.jsp", name = "ok") 
}, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 

@ParentPackage("default")
public class ViewClustersAction extends BasicActionClass {
	private List<Node> clusterList;
	private HdfsData hdfsData;
	
	public String execute () {
		NodesManager cm = NodesManager.getInstance();
		clusterList = cm.getNodeList();
		
		try {
			HDFS hdfs = new HDFS();
			hdfsData = hdfs.getHDFSSpace();
		} catch ( Exception e ) {
			
		}
		
		return "ok";
	}

	public HdfsData getHdfsData() {
		return hdfsData;
	}
	
	public List<Node> getClusterList() {
		return clusterList;
	}

	public void setClusterList(List<Node> clusterList) {
		this.clusterList = clusterList;
	}


}
