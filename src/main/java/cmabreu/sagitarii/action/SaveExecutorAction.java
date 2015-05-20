
package cmabreu.sagitarii.action;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.core.types.ExecutorType;
import cmabreu.sagitarii.misc.PathFinder;
import cmabreu.sagitarii.persistence.entity.ActivationExecutor;
import cmabreu.sagitarii.persistence.services.ExecutorService;

@Action (value = "saveExecutor", results = { @Result (type="redirect", location = "viewExecutors", name = "ok"),
		 @Result (type="redirect", location = "viewExecutors", name = "error") }, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 

@ParentPackage("default")
public class SaveExecutorAction extends BasicActionClass {
	private ActivationExecutor executor;
	private File wrapperFile;
	private String wrapperFileFileName;
	private String executorTarget;
	private String executorAlias;
	
	public String execute () {
		
		try {

			executor.setExecutorAlias(executorAlias);
			executor.setType( ExecutorType.valueOf( executorTarget ) );
			
			if ( wrapperFile != null ) {
				String filePath = PathFinder.getInstance().getPath() + "/repository";			  
				File fileToCreate = new File(filePath, wrapperFileFileName);
		        FileUtils.copyFile( wrapperFile, fileToCreate);
				executor.setActivationWrapper( wrapperFileFileName );
			} else {
				if ( executorTarget.equals("MAP") || executorTarget.equals("LIBRARY") || executorTarget.equals("RSCRIPT") ) {
					setMessageText( "Error: You need to upload an activation wrapper for MAP Activities." );
					return "error";
				}
			}
			
			new ExecutorService().insertExecutor( executor );
			setMessageText( "Activation Executor created.");	
			
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

	public void setExecutorTarget(String executorTarget) {
		this.executorTarget = executorTarget;
	}

	public void setExecutorAlias(String executorAlias) {
		this.executorAlias = executorAlias;
	}


}
