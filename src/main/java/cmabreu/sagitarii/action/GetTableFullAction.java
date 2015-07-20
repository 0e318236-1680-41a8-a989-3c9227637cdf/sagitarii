
package cmabreu.sagitarii.action;

import java.io.ByteArrayInputStream;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.persistence.services.RelationService;

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
	
	public String execute () {
		
		try {
			RelationService rs = new RelationService();
			fileInputStream = rs.getTableFull( tableName );
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
}
