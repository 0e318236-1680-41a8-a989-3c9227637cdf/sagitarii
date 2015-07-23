
package cmabreu.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

@Action (value = "index", results = { @Result (location = "index_login.jsp", name = "ok") } ) 

@ParentPackage("default")
public class IndexAction  {
	
	public String execute () {
		return "ok";
	}

}
