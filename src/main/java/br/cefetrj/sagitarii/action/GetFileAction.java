
package br.cefetrj.sagitarii.action;

import java.io.FileInputStream;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

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
	private FileInputStream fileInputStream;
	private String macAddress;
	
	public String execute () {
		/*
		try {
			FileService fs = new FileService();
			if ( (idFile != null) && ( idFile > -1 ) ) {
				file = fs.getFile( idFile );
				fileName = file.getFileName() + ".gz";
				String theFile = file.getFilePath() + idFile + "/" + fileName;
				File fil = new File( theFile );
				fileInputStream = new FileInputStream( fil );
			}
		} catch ( Exception e ) {
            e.printStackTrace();
		}
		*/
		
		return "ok";
	}

	
	public void setIdFile(Integer idFile) {
		this.idFile = idFile;
	}
	
	public String getFileName() {
		return fileName;
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

}
