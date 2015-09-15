package br.cefetrj.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.core.delivery.InstanceDeliveryControl;

@Action (value = "reloadBuffers", results = { @Result (type="redirect", location = "viewRunning", name = "ok") 
}, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 

@ParentPackage("default")
public class ReloadBuffersAction extends BasicActionClass {
	private String instance;
	
	public String execute(){

		try {
			if( instance == null ) {
				InstanceDeliveryControl.getInstance().forceInformAllDelayed();
				setMessageText( "Sagitarii is asking nodes for all delayed Instances...");
			} else {
				if ( !instance.equals("") ) {
					InstanceDeliveryControl.getInstance().forceInformDelayed( instance );
				}
			}
		} catch ( Exception e ) {
			setMessageText( e.getMessage() );
		} 
			
		return "ok";
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

}
