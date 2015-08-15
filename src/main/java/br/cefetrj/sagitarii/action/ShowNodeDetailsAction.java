
package br.cefetrj.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.core.Cluster;
import br.cefetrj.sagitarii.core.ClustersManager;

@Action (value = "showNodeDetails", results = { @Result (location = "viewClusterDetail.jsp", name = "ok") 
}, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 

@ParentPackage("default")
public class ShowNodeDetailsAction extends BasicActionClass {
	private String macAddress;
	private Cluster cluster;
	
	public String execute () {
		ClustersManager cm = ClustersManager.getInstance();
		cluster = cm.getCluster( macAddress );
		return "ok";
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
	public Cluster getCluster() {
		return cluster;
	}
	
}
