
package br.cefetrj.sagitarii.action;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.misc.PathFinder;
import br.cefetrj.sagitarii.persistence.entity.ActivationExecutor;
import br.cefetrj.sagitarii.persistence.services.ExecutorService;

@Action (value = "updateExecutor", results = { @Result (type="redirect", location = "viewExecutors", name = "ok") }, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 

@ParentPackage("default")
public class UpdateExecutorAction extends BasicActionClass {
	private ActivationExecutor executor;
	private File wrapperFile;
	private String wrapperFileFileName;
	private int idExecutor;
	private String executorTarget;
	
	public String execute () {
		
		try {

			executor.setIdActivationExecutor( idExecutor );
			
			if ( !executorTarget.equals("SELECT") ) {
				if ( wrapperFile != null ) {
					String filePath = PathFinder.getInstance().getPath() + "/repository";			  
					File fileToCreate = new File(filePath, wrapperFileFileName);
			        FileUtils.copyFile( wrapperFile, fileToCreate);
					executor.setActivationWrapper( wrapperFileFileName );
				} else {
					if ( !executorTarget.equals("REDUCE") ) {
						setMessageText( "You need to upload a wrapper file." );
						return "ok";
					}
				}
			}
			
			new ExecutorService().updateExecutor( executor );
			setMessageText( "Executor updated.");	
			
		} catch ( Exception e ) {
			
			e.printStackTrace();
			
			setMessageText( "Error: " + e.getMessage() );
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
	
	public void setExecutorTarget(String executorTarget) {
		this.executorTarget = executorTarget;
	}
	
}
