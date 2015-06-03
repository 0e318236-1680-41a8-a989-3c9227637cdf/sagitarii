
package cmabreu.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.persistence.entity.User;
import cmabreu.sagitarii.persistence.exceptions.DatabaseConnectException;
import cmabreu.sagitarii.persistence.services.UserService;

import com.opensymphony.xwork2.ActionContext;

@Action (value = "doLogin", results = { @Result (type="redirect", location = "${destiny}", name = "ok") } ) 

@ParentPackage("default")
public class DoLoginAction extends BasicActionClass {
	private String destiny;
	private String username;
	private String password;
	
	public String execute () {
		destiny = "index";
		try {
			
			UserService es = new UserService();
			User user = es.login(username, password);
			ActionContext.getContext().getSession().put("loggedUser", user);
			
			destiny = "indexRedir";
			
		} catch ( DatabaseConnectException e ) {
			setMessageText( e.getMessage() );
		} catch ( Exception e ) {
			setMessageText("Invalid username or password.");
		} 
		
		return "ok";
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public String getDestiny() {
		return destiny;
	}

	
}
