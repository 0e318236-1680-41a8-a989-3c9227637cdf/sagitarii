
package br.cefetrj.sagitarii.action;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.core.ssh.SSHSessionManager;
import br.cefetrj.sagitarii.misc.PathFinder;

@Action (value = "nodeSSHUpload", results = { @Result (type="redirect", location = "${location}", name = "ok") }, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 

@ParentPackage("default")
public class NodeSSHUploadAction extends BasicActionClass {
	private File fileUpload;
	private String fileUploadFileName;
	private String macAddress;
	private String targetPath;
	private String location;
	private String option;
	
	public String execute () {
		if ( macAddress != null ) {
			location = "nodeSSHTerminal?macAddress=" + macAddress;
		} else {
			location = "nodeSSHMultiTerminal";
		}
		try {

			if ( fileUpload != null ) {
				
				String filePath = PathFinder.getInstance().getPath() + "/cache/";			  
				File fileToCreate = new File(filePath, fileUploadFileName);
		        FileUtils.copyFile( fileUpload, fileToCreate);
		        SSHSessionManager mngr = SSHSessionManager.getInstance();
		        
		        if ( option != null && option.equals("multi") ){
		        	mngr.multipleUpload( filePath+"/"+fileUploadFileName, targetPath );
		        } else {
		        	mngr.upload(macAddress, filePath+"/"+fileUploadFileName, targetPath);
		        }
		        
		        
			} else {
				setMessageText( "You need to upload a file." );
			}
			
			
		} catch ( Exception e ) {
			setMessageText( "Error: " + e.getMessage() );
		}
		
		return "ok";
	}

	public void setFileUpload(File fileUpload) {
		this.fileUpload = fileUpload;
	}
	
	public void setFileUploadFileName(String fileUploadFileName) {
		this.fileUploadFileName = fileUploadFileName;
	}
	
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setOption(String option) {
		this.option = option;
	}
	
}
