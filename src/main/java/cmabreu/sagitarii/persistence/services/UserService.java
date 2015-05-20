package cmabreu.sagitarii.persistence.services;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmabreu.sagitarii.persistence.entity.User;
import cmabreu.sagitarii.persistence.exceptions.DatabaseConnectException;
import cmabreu.sagitarii.persistence.exceptions.DeleteException;
import cmabreu.sagitarii.persistence.exceptions.InsertException;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.exceptions.UpdateException;
import cmabreu.sagitarii.persistence.repository.UserRepository;

public class UserService {
	private UserRepository rep;
	private Logger logger = LogManager.getLogger( this.getClass().getName() );

	public UserService() throws DatabaseConnectException {
		this.rep = new UserRepository();
	}

	public void updateUser(User user) throws UpdateException {
		User oldUser;
		try {
			oldUser = rep.getUser( user.getIdUser() );
		} catch (NotFoundException e) {
			throw new UpdateException( e.getMessage() );
		}
		oldUser.setFullName( user.getFullName() );
		oldUser.setPassword( user.getPassword() );
		oldUser.setLoginName( user.getLoginName() );
		oldUser.setType( user.getType() );
		oldUser.setUserMail( user.getUserMail() );
		
		rep.newTransaction();
		rep.updateUser(oldUser);
	}	

	public User login( String loginName, String password ) throws NotFoundException{
		return rep.login( loginName, password);
	}
	
	public User getUser(int idUser) throws NotFoundException{
		return rep.getUser(idUser);
	}

	public void newTransaction() {
		if ( !rep.isOpen() ) {
			rep.newTransaction();
		}
	}
	
	public User insertUser(User user) throws InsertException {
		User expRet = rep.insertUser( user );
		return expRet ;
	}	
	
	public void deleteUser( int idUser ) throws DeleteException {
		try {
			User user = rep.getUser(idUser);
			rep.newTransaction();
			rep.deleteUser(user);
		} catch (NotFoundException e) {
			logger.error( e.getMessage() );
			throw new DeleteException( e.getMessage() );
		}
	}

	public Set<User> getList( ) throws NotFoundException {
		return rep.getList( );
	}
	
}
