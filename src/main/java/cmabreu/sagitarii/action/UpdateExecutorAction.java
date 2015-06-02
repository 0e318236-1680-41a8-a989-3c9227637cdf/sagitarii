
package cmabreu.sagitarii.action;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.misc.PathFinder;
import cmabreu.sagitarii.persistence.entity.ActivationExecutor;
import cmabreu.sagitarii.persistence.services.ExecutorService;

@Action (value = "updateExecutor", results = { @Result (type="redirect", location = "viewExecutors", name = "ok"),
		 @Result (type="redirect", location = "viewExecutors", name = "error") }, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 

@ParentPackage("default")
public class UpdateExecutorAction extends BasicActionClass {
	private ActivationExecutor executor;
	private File wrapperFile;
	private String wrapperFileFileName;
	private int idExecutor;
	
	public String execute () {
		
		try {

			executor.setIdActivationExecutor( idExecutor );
			
			if ( wrapperFile != null ) {
				String filePath = PathFinder.getInstance().getPath() + "/repository";			  
				File fileToCreate = new File(filePath, wrapperFileFileName);
		        FileUtils.copyFile( wrapperFile, fileToCreate);
				executor.setActivationWrapper( wrapperFileFileName );
				new ExecutorService().updateExecutor( executor );
				setMessageText( "Executor updated.");	
			} else {
				setMessageText( "You need to upload a wrapper file." );	
			}
			
		} catch ( Exception e ) {
			setMessageText( "Error: " + e.getMessage() );
			return "error";
		}
		
		return "ok";
	}

	public void setExecutor(ActivationExecutor executor) {
		this.executor = executor;
	}

	public void setWrapperFile(File wrapperFile) {
		this.wrapperFile = wrapperFile;
	}

	public void setWrapperFileFileName(String wrapperFileFileName) {
		this.wrapperFileFileName = wrapperFileFileName;
	}

	public void setIdExecutor(int idExecutor) {
		this.idExecutor = idExecutor;
	}
	
}
