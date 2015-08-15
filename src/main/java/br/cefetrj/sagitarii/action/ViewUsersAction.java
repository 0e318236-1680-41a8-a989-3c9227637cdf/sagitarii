
package br.cefetrj.sagitarii.action;

import java.util.Set;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.persistence.entity.User;
import br.cefetrj.sagitarii.persistence.exceptions.NotFoundException;
import br.cefetrj.sagitarii.persistence.services.UserService;

@Action (value = "viewUsers", results = { @Result (location = "viewUsers.jsp", name = "ok") 
}, interceptorRefs= { @InterceptorRef("seguranca")} ) 

@ParentPackage("default")
public class ViewUsersAction extends BasicActionClass {
	private Set<User> userList;
	
	public String execute () {
		try {
			UserService us = new UserService();
			userList = us.getList();
		} catch ( NotFoundException  e) {
			// Empty list. Don't panic.
		} catch (Exception e) {
			setMessageText("Error: " + e.getMessage() );
		} 
		
		return "ok";
	}

	public Set<User> getUserList() {
		return userList;
	}

}
