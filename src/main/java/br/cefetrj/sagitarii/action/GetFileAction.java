
package br.cefetrj.sagitarii.action;

import java.io.File;
import java.io.FileInputStream;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.persistence.HDFS;

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
	private String idFile;
	private FileInputStream fileInputStream;
	
	public String execute () {
		try {
			
			File file = new File( idFile );
			fileName = file.getName() + ".gz";
			HDFS hdfs = new HDFS();
			fileInputStream = hdfs.getFile( idFile );
			
			
		} catch ( Exception e ) {
            e.printStackTrace();
		}
		return "ok";
	}

	
	public void setIdFile(String idFile) {
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
	

}
