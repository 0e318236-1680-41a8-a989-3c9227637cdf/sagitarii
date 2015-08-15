
package br.cefetrj.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

@Action (value = "requestAccess", results = { @Result (location = "requestAccess.jsp", name = "ok") } ) 

@ParentPackage("default")
public class RequestAccessAction  {
	
	public String execute () {
		return "ok";
	}

}
