package cmabreu.sagitarii.action;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.core.ClustersManager;
import cmabreu.sagitarii.core.types.ClusterType;

import com.opensymphony.xwork2.ActionContext;

@Action(value="announce", results= {  
	    @Result(name="ok", type="httpheader", params={"status", "200"}) }
)   

@ParentPackage("default")
public class AnnounceAction  {
	private String soName;
	private String macAddress;
	private String machineName;
	private	String localIpAddress;
	private String soFamily;
	private String javaVersion;
	private String cpuLoad;
	private Integer availableProcessors;
	private Integer maxAllowedTasks;
	private Long totalMemory;
	private Long freeMemory;
	
	
	public String execute(){
		String resposta = "";
		
		if ( cpuLoad != null ) {
		
			Double cpu = 0.0;
			if ( freeMemory == null ) {
				freeMemory = Long.valueOf(0);
			}
			if ( totalMemory == null ) {
				totalMemory = Long.valueOf(0);
			}
	
			try {
				cpu = Double.valueOf( cpuLoad );
				ClustersManager.getInstance().addOrUpdateCluster( ClusterType.NODE, javaVersion, soFamily, macAddress, 
						localIpAddress, machineName, cpu, soName, availableProcessors, 
						maxAllowedTasks, freeMemory, totalMemory );
				resposta = ClustersManager.getInstance().getTask( macAddress );
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
		
		try { 
			HttpServletResponse response = (HttpServletResponse)ActionContext.getContext().get(StrutsStatics.HTTP_RESPONSE);
			response.setCharacterEncoding("UTF-8"); 
			response.getWriter().write(resposta);  
		} catch (IOException ex) {
			
		}
		
		return "ok";
	}


	public void setSoName(String soName) {
		this.soName = soName;
	}


	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}


	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}


	public void setLocalIpAddress(String localIpAddress) {
		this.localIpAddress = localIpAddress;
	}


	public void setSoFamily(String soFamily) {
		this.soFamily = soFamily;
	}


	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}


	public void setCpuLoad(String cpuLoad) {
		this.cpuLoad = cpuLoad;
	}


	public void setAvailableProcessors(Integer availableProcessors) {
		this.availableProcessors = availableProcessors;
	}

	public void setMaxAllowedTasks(Integer maxAllowedTasks) {
		this.maxAllowedTasks = maxAllowedTasks;
	}
	
	public void setFreeMemory(Long freeMemory) {
		this.freeMemory = freeMemory;
	}
	
	public void setTotalMemory(Long totalMemory) {
		this.totalMemory = totalMemory;
	}
	
}
