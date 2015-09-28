
package br.cefetrj.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

@Action (value = "importTableXML", results = { @Result (location = "importTableXML.jsp", name = "ok")
}, interceptorRefs= { @InterceptorRef("seguranca")} ) 

@ParentPackage("default")
public class ImportTableXMLAction extends BasicActionClass {
	
	public String execute () {
		return "ok";
	}

}
