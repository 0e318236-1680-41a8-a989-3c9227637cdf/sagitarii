
package br.cefetrj.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.core.Sagitarii;

@Action (value = "instanceBuffer", 
	results = { 
		@Result ( type="redirect", location = "viewRunning", name = "ok")
	}, interceptorRefs= { @InterceptorRef("seguranca")	 }) 

@ParentPackage("default")
public class InstanceBufferAction extends BasicActionClass {
	private String op;
	
	public String execute () {
		
		setMessageText("Not implemented yet!");
	
		
		/*
		if ( op == null ) return "ok";
		
		if ( op.equals("restore") ) {
			Sagitarii.getInstance().restoreOutputBufferFromMemory();
		} 
		
		if ( op.equals("save") ) {
			Sagitarii.getInstance().transferOutputBufferToMemory();
		} 
		*/
		return "ok";
	}
	
	public void setOp(String op) {
		this.op = op;
	}
	
}
