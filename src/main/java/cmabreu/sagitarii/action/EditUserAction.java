
package cmabreu.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.persistence.entity.User;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.services.UserService;

@Action (value = "editUser", results = { @Result (location = "editUser.jsp", name = "ok"),
		@Result (type="redirect", location = "viewUsers", name = "error")
}, interceptorRefs= { @InterceptorRef("seguranca")} ) 

@ParentPackage("default")
public class EditUserAction extends BasicActionClass {
	private Integer idUser;
	private User user;
	
	public String execute () {
		try {
			UserService us = new UserService();
			user = us.getUser(idUser);
			user.setPassword("");
			
		} catch ( NotFoundException  e) {
			setMessageText("User not found.");
			return "error";
		} catch (Exception e) {
			setMessageText("Error: " + e.getMessage() );
		} 
		
		return "ok";
	}

	public Integer getIdUser() {
		return idUser;
	}

	public void setIdUser(Integer idUser) {
		this.idUser = idUser;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
