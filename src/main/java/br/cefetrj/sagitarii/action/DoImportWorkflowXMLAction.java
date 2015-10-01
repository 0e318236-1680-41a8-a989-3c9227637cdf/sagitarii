
package br.cefetrj.sagitarii.action;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.misc.PathFinder;
import br.cefetrj.sagitarii.persistence.services.WorkflowService;

@Action (value = "doImportXmlWorkflow", results = { @Result (type="redirect", 
	location = "indexRedir", name = "ok") }, 
	interceptorRefs= { @InterceptorRef("seguranca") } ) 

@ParentPackage("default")
public class DoImportWorkflowXMLAction extends BasicActionClass {
	private File workflowFile;
	private String workflowFileFileName;
	
	public String execute () {
		
		try {
			String workflowName = "";
			if ( workflowFile != null ) {
				String filePath = PathFinder.getInstance().getPath() + "/temp";
				new File(filePath).mkdirs();
				File fileToCreate = new File(filePath, workflowFileFileName);
		        FileUtils.copyFile( workflowFile, fileToCreate);
		        WorkflowService ws = new WorkflowService();
		        workflowName = ws.importWorkflowXml( filePath + "/" + workflowFileFileName, getLoggedUser() );
		        
			} else {
				setMessageText( "Error: You need to upload a XML file Workflow exported by Sagitarii." );
			}
			
			setMessageText( "Workflow " + workflowName + " created.");	
			
		} catch ( Exception e ) {
			setMessageText( "Error: " + e.getMessage() );
			return "error";
		}
		
		return "ok";
	}

	public void setTableFile(File workflowFile) {
		this.workflowFile = workflowFile;
	}

	public void setTableFileFileName(String workflowFileFileName) {
		this.workflowFileFileName = workflowFileFileName;
	}

}
