
package br.cefetrj.sagitarii.action;

import java.io.File;
import java.io.FileInputStream;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.misc.DeleteOnCloseFileInputStream;
import br.cefetrj.sagitarii.misc.PathFinder;

@Action(value="getLocalFile", results= {  
	    @Result(name="ok", type="stream", params = {
                "contentType", "application/octet-stream",
                "inputName", "fileInputStream",
                "contentDisposition", "filename=\"${fileName}\"",
                "bufferSize", "1024"
        }) }
)   

@ParentPackage("default")
public class GetLocalFileAction extends BasicActionClass {
	private String fileName;
	private String macAddress;
	private DeleteOnCloseFileInputStream fileInputStream;
	
	public String execute () {
		
		try {
			String filePath = PathFinder.getInstance().getPath() + "/ssh-downloads/" + macAddress + "/" + fileName;			  
			File file = new File( filePath );
			
			System.out.println( filePath );
			
			fileInputStream = new DeleteOnCloseFileInputStream( file );			
		} catch ( Exception e ) {
            //
		}
		
		return "ok";
	}

	
	public FileInputStream getFileInputStream() {
		return fileInputStream;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
	public String getFileName() {
		return fileName;
	}

}
