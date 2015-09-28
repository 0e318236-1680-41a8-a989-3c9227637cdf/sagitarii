
package br.cefetrj.sagitarii.action;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.misc.PathFinder;
import br.cefetrj.sagitarii.persistence.services.RelationService;

@Action (value = "doImportXmlTable", results = { @Result (type="redirect", 
	location = "tablesmanager", name = "ok") }, 
	interceptorRefs= { @InterceptorRef("seguranca") } ) 

@ParentPackage("default")
public class DoImportTableXMLAction extends BasicActionClass {
	private File tableFile;
	private String tableFileFileName;
	
	public String execute () {
		
		try {
			String tableName = "";
			if ( tableFile != null ) {
				String filePath = PathFinder.getInstance().getPath() + "/temp";
				new File(filePath).mkdirs();
				File fileToCreate = new File(filePath, tableFileFileName);
		        FileUtils.copyFile( tableFile, fileToCreate);
		        
		        RelationService rs = new RelationService();
		        tableName = rs.importTableXml( filePath + "/" + tableFileFileName );
		        
			} else {
				setMessageText( "Error: You need to upload a XML file table exported by Sagitarii." );
			}
			
			setMessageText( "Table " + tableName + " created.");	
			
		} catch ( Exception e ) {
			setMessageText( "Error: " + e.getMessage() );
			return "error";
		}
		
		return "ok";
	}

	public void setTableFile(File tableFile) {
		this.tableFile = tableFile;
	}

	public void setTableFileFileName(String tableFileFileName) {
		this.tableFileFileName = tableFileFileName;
	}

}
