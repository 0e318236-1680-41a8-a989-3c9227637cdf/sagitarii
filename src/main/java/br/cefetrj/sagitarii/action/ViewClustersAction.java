
package br.cefetrj.sagitarii.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.core.Cluster;
import br.cefetrj.sagitarii.core.ClustersManager;

@Action (value = "viewClusters", results = { @Result (location = "viewClusters.jsp", name = "ok") 
}, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 

@ParentPackage("default")
public class ViewClustersAction extends BasicActionClass {
	private List<Cluster> clusterList;
	
	public String execute () {
		ClustersManager cm = ClustersManager.getInstance();
		clusterList = cm.getClusterList();
		return "ok";
	}

	public List<Cluster> getClusterList() {
		return clusterList;
	}

	public void setClusterList(List<Cluster> clusterList) {
		this.clusterList = clusterList;
	}


}
