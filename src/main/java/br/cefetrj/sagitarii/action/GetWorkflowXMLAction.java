
package br.cefetrj.sagitarii.action;

import java.io.ByteArrayInputStream;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.persistence.services.WorkflowService;

@Action(value="getWorkflowXML", results= {  
	    @Result(name="ok", type="stream", params = {
                "contentType", "text/xml",
                "inputName", "fileInputStream",
                "contentDisposition", "attachment;filename=\"${fileName}\"",
                "bufferSize", "1024"
        }) }
) 

@ParentPackage("default")
public class GetWorkflowXMLAction extends BasicActionClass {
	private String fileName;
	private ByteArrayInputStream fileInputStream;
	private String workflowAlias;
	
	public String execute () {
		
		try {
			WorkflowService ws = new WorkflowService();
			fileInputStream = ws.getWorkflowXML( workflowAlias );
	        fileName = workflowAlias + ".xml";
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

	public void setWorkflowAlias(String workflowAlias) {
		this.workflowAlias = workflowAlias;
	}
}
