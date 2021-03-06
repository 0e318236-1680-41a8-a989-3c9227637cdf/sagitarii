
package br.cefetrj.sagitarii.action;

import java.io.ByteArrayInputStream;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.persistence.services.RelationService;

@Action(value="getTableFull", results= {  
	    @Result(name="ok", type="stream", params = {
                "contentType", "application/octet-stream",
                "inputName", "fileInputStream",
                "contentDisposition", "filename=\"${fileName}\"",
                "bufferSize", "1024"
        }) }
)   

@ParentPackage("default")
public class GetTableFullAction extends BasicActionClass {
	private String fileName;
	private ByteArrayInputStream fileInputStream;
	private String tableName;
	private int idExperiment;
	
	public String execute () {
		
		try {
			RelationService rs = new RelationService();
			fileInputStream = rs.getTableFull( tableName, idExperiment );
	        fileName = tableName + ".csv";
		} catch ( Exception e ) {
            //
		}
		
		return "ok";
	}

	
	public String getFileName() {
		return fileName;
	}
	
	public ByteArrayInputStream getFileInputStream() {
		return fileInputStream;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public void setIdExperiment(int idExperiment) {
		this.idExperiment = idExperiment;
	}
	
}
