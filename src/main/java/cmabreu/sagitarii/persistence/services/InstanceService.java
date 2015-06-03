package cmabreu.sagitarii.persistence.services;

import java.util.Calendar;
import java.util.List;

import cmabreu.sagitarii.core.types.InstanceStatus;
import cmabreu.sagitarii.persistence.entity.Instance;
import cmabreu.sagitarii.persistence.exceptions.DatabaseConnectException;
import cmabreu.sagitarii.persistence.exceptions.InsertException;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.exceptions.UpdateException;
import cmabreu.sagitarii.persistence.repository.InstanceRepository;

public class InstanceService {
	private InstanceRepository rep;
	
	public InstanceService() throws DatabaseConnectException {
		this.rep = new InstanceRepository();
	}
	
	public void newTransaction() {
		if( !rep.isOpen() ) {
			rep.newTransaction();
		}
	}

	public void close() {
		rep.closeSession();
	}

	public void finishInstance( Instance Instance ) throws UpdateException {
		Instance oldInstance;
		try {
			oldInstance = rep.getInstance( Instance.getSerial() );
		} catch (NotFoundException e) {
			throw new UpdateException( e.getMessage() );
		}
		
		oldInstance.setStatus(  InstanceStatus.FINISHED );
		oldInstance.setStartDateTime( Instance.getStartDateTime() );
		oldInstance.setFinishDateTime( Calendar.getInstance().getTime() );

		rep.newTransaction();
		rep.updateInstance(oldInstance);
	}	

	public int insertInstance(Instance Instance) throws InsertException {
		rep.insertInstance( Instance );
		return 0;
	}
	
	public void insertInstanceList( List<Instance> pipes ) throws InsertException {
		rep.insertInstanceList( pipes );
	}
	
	public Instance getInstance( String serial ) throws NotFoundException {
		return rep.getInstance(serial);
	}

	public Instance getInstance( int idInstance ) throws NotFoundException {
		return rep.getInstance( idInstance );
	}
	
	
	public List<Instance> getList( int idFragment ) throws NotFoundException {
		List<Instance> pipes = rep.getList( idFragment );
		return pipes;
	}

	public List<Instance> getPipelinedList( int idFragment ) throws NotFoundException {
		List<Instance> pipes = rep.getPipelinedList( idFragment );
		return pipes;
	}
	
	public List<Instance> getHead( int howMany, int idFragment ) throws Exception {
		List<Instance> pipes = rep.getHead( howMany, idFragment );
		return pipes;
	}
	
	public List<Instance> getHeadJoin( int howMany, int idFragment ) throws Exception {
		List<Instance> pipes = rep.getHeadJoin( howMany, idFragment );
		return pipes;
	}

	public List<Instance> recoverFromCrash( ) throws Exception {
		List<Instance> pipes = rep.recoverFromCrash();
		return pipes;
	}
	
	
}
