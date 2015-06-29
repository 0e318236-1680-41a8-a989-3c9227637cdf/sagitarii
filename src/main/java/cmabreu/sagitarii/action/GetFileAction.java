
package cmabreu.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.misc.ProgressAwareInputStream;
import cmabreu.sagitarii.persistence.services.FileService;

@Action(value="getFile", results= {  
	    @Result(name="ok", type="stream", params = {
                "contentType", "application/octet-stream",
                "inputName", "fileInputStream",
                "contentDisposition", "filename=\"${fileName}\"",
                "bufferSize", "1024"
        }) }
)   

@ParentPackage("default")
public class GetFileAction extends BasicActionClass {
	private String fileName;
	private Integer idFile;
	private ProgressAwareInputStream fileInputStream;
	private String macAddress;
	
	public String execute () {
		cmabreu.sagitarii.persistence.entity.File file = null;
		try {
			FileService fs = new FileService();
			if ( (idFile != null) && ( idFile > -1 ) ) {
				file = fs.getFile( idFile );
				fileName = file.getFileName();
				fileInputStream = file.getDownloadStream( macAddress );
			}
		} catch ( Exception e ) {
            //
		}
		
		return "ok";
	}

	
	public void setIdFile(Integer idFile) {
		this.idFile = idFile;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	
	public ProgressAwareInputStream getFileInputStream() {
		return fileInputStream;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

}
