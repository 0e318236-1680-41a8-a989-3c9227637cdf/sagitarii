package br.cefetrj.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.core.Sagitarii;

@Action (value = "reloadBuffers", results = { @Result (type="redirect", location = "viewRunning", name = "ok") 
}, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 

@ParentPackage("default")
public class ReloadBuffersAction extends BasicActionClass {
	
	public String execute(){

		try {
			Sagitarii.getInstance().reloadAfterCrash();
			setMessageText( "AfterCrash Routine Called. This may cause buffer overload. Be carefull.");
		} catch ( Exception e ) {
			setMessageText( e.getMessage() );
		} 
			
		return "ok";
	}


}
