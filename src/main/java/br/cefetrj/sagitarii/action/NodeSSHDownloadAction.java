
package br.cefetrj.sagitarii.action;

import java.io.File;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.core.ssh.SSHSessionManager;
import br.cefetrj.sagitarii.misc.PathFinder;

@Action (value = "nodeSSHDownload", results = { @Result (type="redirect", location = "${location}", name = "ok") }, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 

@ParentPackage("default")
public class NodeSSHDownloadAction extends BasicActionClass {
	private String macAddress;
	private String sourceFile;
	private String location;
	
	public String execute () {
		location = "nodeSSHTerminal?macAddress=" + macAddress;

		try {

			String filePath = PathFinder.getInstance().getPath() + "/ssh-downloads/" + macAddress + "/" ;			  
	        SSHSessionManager mngr = SSHSessionManager.getInstance();

	        String[] path = sourceFile.split("/");
	        String theFile = path[ path.length -1 ];
	        
	        File file = new File( filePath );
	        file.mkdirs();
	        
	        mngr.download( macAddress, sourceFile, filePath + theFile );

			location = "getLocalFile?fileName=" + theFile + "&macAddress=" + macAddress;

		} catch ( Exception e ) {
			setMessageText( "Error: " + e.getMessage() );
		}
		
		return "ok";
	}

	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}
	
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
	public String getLocation() {
		return location;
	}
	
}
