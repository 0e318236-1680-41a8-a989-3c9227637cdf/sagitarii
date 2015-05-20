package cmabreu.sagitarii.persistence.services;

import java.util.Calendar;
import java.util.List;

import cmabreu.sagitarii.core.types.PipelineStatus;
import cmabreu.sagitarii.persistence.entity.Pipeline;
import cmabreu.sagitarii.persistence.exceptions.DatabaseConnectException;
import cmabreu.sagitarii.persistence.exceptions.InsertException;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.exceptions.UpdateException;
import cmabreu.sagitarii.persistence.repository.PipelineRepository;

public class PipelineService {
	private PipelineRepository rep;
	
	public PipelineService() throws DatabaseConnectException {
		this.rep = new PipelineRepository();
	}
	
	public void newTransaction() {
		if( !rep.isOpen() ) {
			rep.newTransaction();
		}
	}

	public void close() {
		rep.closeSession();
	}

	public void finishPipeline( Pipeline pipeline ) throws UpdateException {
		Pipeline oldPipeline;
		try {
			oldPipeline = rep.getPipeline( pipeline.getSerial() );
		} catch (NotFoundException e) {
			throw new UpdateException( e.getMessage() );
		}
		
		oldPipeline.setStatus(  PipelineStatus.FINISHED );
		oldPipeline.setStartDateTime( pipeline.getStartDateTime() );
		oldPipeline.setFinishDateTime( Calendar.getInstance().getTime() );

		rep.newTransaction();
		rep.updatePipeline(oldPipeline);
	}	

	
	/*
	public void setPipelineStatus( String serial, ActivityStatus status ) throws UpdateException {
		Pipeline oldPipeline;
		try {
			oldPipeline = rep.getPipeline( serial );
		} catch (NotFoundException e) {
			throw new UpdateException( e.getMessage() );
		}
		
		oldPipeline.setStatus( status );

		rep.newTransaction();
		rep.updatePipeline(oldPipeline);
	}
	*/	
	
	public int insertPipeline(Pipeline pipeline) throws InsertException {
		rep.insertPipeline( pipeline );
		return 0;
	}
	
	public void insertPipelineList( List<Pipeline> pipes ) throws InsertException {
		rep.insertPipelineList( pipes );
	}
	
	public Pipeline getPipeline( String serial ) throws NotFoundException {
		return rep.getPipeline(serial);
	}

	public Pipeline getPipeline( int idPipeline ) throws NotFoundException {
		return rep.getPipeline( idPipeline );
	}
	
	
	public List<Pipeline> getList( int idFragment ) throws NotFoundException {
		List<Pipeline> pipes = rep.getList( idFragment );
		return pipes;
	}

	public List<Pipeline> getPipelinedList( int idFragment ) throws NotFoundException {
		List<Pipeline> pipes = rep.getPipelinedList( idFragment );
		return pipes;
	}
	
	public List<Pipeline> getHead( int howMany, int idFragment ) throws Exception {
		List<Pipeline> pipes = rep.getHead( howMany, idFragment );
		return pipes;
	}
	
	public List<Pipeline> getHeadJoin( int howMany, int idFragment ) throws Exception {
		List<Pipeline> pipes = rep.getHeadJoin( howMany, idFragment );
		return pipes;
	}

	public List<Pipeline> recoverFromCrash( ) throws Exception {
		List<Pipeline> pipes = rep.recoverFromCrash();
		return pipes;
	}
	
	
}
