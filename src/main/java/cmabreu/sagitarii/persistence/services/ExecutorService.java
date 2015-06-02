package cmabreu.sagitarii.persistence.services;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmabreu.sagitarii.core.types.ExecutorType;
import cmabreu.sagitarii.persistence.entity.ActivationExecutor;
import cmabreu.sagitarii.persistence.exceptions.DatabaseConnectException;
import cmabreu.sagitarii.persistence.exceptions.DeleteException;
import cmabreu.sagitarii.persistence.exceptions.InsertException;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.exceptions.UpdateException;
import cmabreu.sagitarii.persistence.repository.ExecutorRepository;

public class ExecutorService {
	private ExecutorRepository rep;
	private Logger logger = LogManager.getLogger( this.getClass().getName() );

	public String getAsManifest() throws NotFoundException {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<manifest>\n");
		Set<ActivationExecutor> preList = rep.getList();
		for ( ActivationExecutor executor :  preList  ) {
			ExecutorType type = executor.getType();
			if ( type != ExecutorType.SELECT ) {
				String alias = executor.getExecutorAlias();
				String wrapper = executor.getActivationWrapper();
				sb.append("\t<wrapper name=\""+ alias +"\" type=\"" + type.toString() + "\" target=\"ANY\" version=\"1.0\">\n");
				sb.append("\t\t<activityFile>"+wrapper+"</activityFile>\n");
				sb.append("\t\t<reload>true</reload>\n");
				sb.append("\t</wrapper>\n");
			}
		}
		sb.append("</manifest>\n\n");
		return sb.toString();
	}
	
	public ExecutorService() throws DatabaseConnectException {
		this.rep = new ExecutorRepository();
	}

	public void updateExecutor(ActivationExecutor executor) throws UpdateException {
		ActivationExecutor oldExecutor;
		try {
			oldExecutor = rep.getActivationExecutor( executor.getIdActivationExecutor() );
		} catch (NotFoundException e) {
			throw new UpdateException( e.getMessage() );
		}
		
		oldExecutor.setSelectStatement( executor.getSelectStatement() );
		if( ( executor.getActivationWrapper() != null ) && ( !executor.getActivationWrapper().equals("") )) {
			oldExecutor.setActivationWrapper( executor.getActivationWrapper()  );
		}
		
		newTransaction();
		rep.updateActivationExecutor(oldExecutor);
	}	
	
	public ActivationExecutor getExecutor(int idExecutor) throws NotFoundException{
		return rep.getActivationExecutor(idExecutor);
	}

	public ActivationExecutor getExecutor(String executorAlias) throws NotFoundException{
		return rep.getActivationExecutor(executorAlias);
	}
	
	public void newTransaction() {
		if ( !rep.isOpen() ) {
			rep.newTransaction();
		}
	}
	
	public ActivationExecutor insertExecutor(ActivationExecutor executor) throws InsertException {
		ActivationExecutor expRet = rep.insereActivationExecutor( executor );
		return expRet ;
	}	
	
	public void deleteExecutor( int idExecutor ) throws DeleteException {
		try {
			ActivationExecutor executor = rep.getActivationExecutor(idExecutor);
			rep.newTransaction();
			rep.excluiActivationExecutor(executor);
		} catch (NotFoundException e) {
			logger.error( e.getMessage() );
			throw new DeleteException( e.getMessage() );
		}
	}

	public Set<ActivationExecutor> getList() throws NotFoundException {
		Set<ActivationExecutor> preList = rep.getList();
		return preList;	
	}
	
}
