
package br.cefetrj.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

@Action (value = "mirrorDatabase", results = { @Result (location = "mirrorDatabase.jsp", name = "ok") } ) 

@ParentPackage("default")
public class MirrorDatabaseAction  {
	
	public String execute () {
		return "ok";
	}

}
