
package cmabreu.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.persistence.entity.User;
import cmabreu.sagitarii.persistence.exceptions.DatabaseConnectException;
import cmabreu.sagitarii.persistence.services.UserService;

@Action (value = "doNewUser", results = { @Result (type="redirect", location = "viewUsers", name = "ok")
}, interceptorRefs= { @InterceptorRef("seguranca")}  ) 

@ParentPackage("default")
public class DoNewUserAction extends BasicActionClass {
	private User user;
	
	public String execute () {
		
		try {
			
			UserService es = new UserService();
			es.insertUser(user);
		} catch ( DatabaseConnectException e ) {
			setMessageText( e.getMessage() );
		} catch ( Exception e ) {
			setMessageText("Error: " + e.getMessage() );
		} 
		
		return "ok";
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}
}
