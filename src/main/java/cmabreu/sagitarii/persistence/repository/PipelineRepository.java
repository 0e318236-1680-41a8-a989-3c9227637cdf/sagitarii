package cmabreu.sagitarii.persistence.repository;

import java.util.List;

import cmabreu.sagitarii.persistence.entity.Pipeline;
import cmabreu.sagitarii.persistence.exceptions.DatabaseConnectException;
import cmabreu.sagitarii.persistence.exceptions.DeleteException;
import cmabreu.sagitarii.persistence.exceptions.InsertException;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.exceptions.UpdateException;
import cmabreu.sagitarii.persistence.infra.DaoFactory;
import cmabreu.sagitarii.persistence.infra.IDao;

public class PipelineRepository extends BasicRepository {

	public PipelineRepository() throws DatabaseConnectException {
		super();
		logger.debug("init");
	}

	
	public List<Pipeline> recoverFromCrash() throws Exception {
		logger.debug("recovering common pipelines" );
		DaoFactory<Pipeline> df = new DaoFactory<Pipeline>();
		IDao<Pipeline> fm = df.getDao(this.session, Pipeline.class);
		List<Pipeline> pipes = null;
		try {
			pipes = fm.getList("select * from pipelines where status = 'PAUSED' or status = 'RUNNING' order by id_pipeline");
		} catch (NotFoundException e) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done: " + pipes.size() + " pipelines.");
		return pipes;
	}
	
	
	public List<Pipeline> getHead( int howMany, int idFragment ) throws Exception {
		logger.debug("get first " + howMany + " records for fragment " + idFragment );
		DaoFactory<Pipeline> df = new DaoFactory<Pipeline>();
		IDao<Pipeline> fm = df.getDao(this.session, Pipeline.class);
		List<Pipeline> pipes = null;
		try {
			pipes = fm.getList("select * from pipelines where status = 'PIPELINED' and type <> 'SELECT' and id_fragment = " + idFragment  
					+ " order by id_pipeline limit " + howMany);
			
			String update ="update pipelines set start_date_time = now(), status = 'RUNNING' where id_pipeline in (" + 
					"select id_pipeline from pipelines where status = 'PIPELINED' and type <> 'SELECT' and id_fragment = " + idFragment
					+ " order by id_pipeline limit " + howMany + ")";
			fm.executeQuery( update, true );
		} catch (NotFoundException e) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done: " + pipes.size() + " pipelines.");
		return pipes;
	}

	
	public List<Pipeline> getHeadJoin( int howMany, int idFragment ) throws Exception {
		logger.debug("get first " + howMany + " JOIN records for fragment " + idFragment );
		logger.debug("get first " + howMany + " records" );
		DaoFactory<Pipeline> df = new DaoFactory<Pipeline>();
		IDao<Pipeline> fm = df.getDao(this.session, Pipeline.class);
		List<Pipeline> pipes = null;
		try {
			pipes = fm.getList("select * from pipelines where status = 'PIPELINED' and type = 'SELECT' and id_fragment = " + idFragment
					+ " order by id_pipeline limit " + howMany);

			String update ="update pipelines set status = 'RUNNING' where id_pipeline in (" +
					"select id_pipeline from pipelines where status = 'PIPELINED' and type = 'SELECT' and id_fragment = " + idFragment
					+ " order by id_pipeline limit " + howMany  + ")"; 
			fm.executeQuery( update, true );
			
		} catch (NotFoundException  e) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done: " + pipes.size() + " pipelines.");
		return pipes;		
		
	}

	public List<Pipeline> getPipelinedList( int idFragment ) throws NotFoundException {
		logger.debug("get list" );
		DaoFactory<Pipeline> df = new DaoFactory<Pipeline>();
		IDao<Pipeline> fm = df.getDao(this.session, Pipeline.class);
		List<Pipeline> pipes = null;
		try {
			pipes = fm.getList("select * from pipelines where status = 'PIPELINED' and id_fragment = " + idFragment);
		} catch (Exception e) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done: " + pipes.size() + " pipelines.");
		return pipes;
	}
	
	public List<Pipeline> getList( int idFragment ) throws NotFoundException {
		logger.debug("get list" );
		DaoFactory<Pipeline> df = new DaoFactory<Pipeline>();
		IDao<Pipeline> fm = df.getDao(this.session, Pipeline.class);
		List<Pipeline> pipes = null;
		try {
			pipes = fm.getList("select * from pipelines where id_fragment = " + idFragment);
		} catch (Exception e) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done: " + pipes.size() + " pipelines.");
		return pipes;
	}
	
	public Pipeline insertPipeline(Pipeline pipeline) throws InsertException {
		logger.debug("insert");
		DaoFactory<Pipeline> df = new DaoFactory<Pipeline>();
		IDao<Pipeline> fm = df.getDao(this.session, Pipeline.class);
		try {
			fm.insertDO(pipeline);
			commit();
		} catch (Exception e) {
			logger.error( e.getMessage() );
			rollBack();
			closeSession();
			throw new InsertException(e.getMessage());
		}
		closeSession();
		logger.debug("done");
		return pipeline;
	}

	
	public void insertPipelineList( List<Pipeline> pipes ) throws InsertException {
		logger.debug("insert");
		DaoFactory<Pipeline> df = new DaoFactory<Pipeline>();
		IDao<Pipeline> fm = df.getDao(this.session, Pipeline.class);
		try {
			for ( Pipeline pipeline : pipes ) {
				fm.insertDO(pipeline);
			}
			commit();
		} catch (Exception e) {
			logger.error( e.getMessage() );
			rollBack();
			closeSession();
			throw new InsertException(e.getMessage());
		}
		closeSession();
		logger.debug("done");
	}
	
	public Pipeline getPipeline(String serial) throws NotFoundException {
		logger.debug("retrieving pipeline by serial " + serial + "..." );
		DaoFactory<Pipeline> df = new DaoFactory<Pipeline>();
		IDao<Pipeline> fm = df.getDao(this.session, Pipeline.class);
		Pipeline pipeline = null;
		try {
			pipeline = fm.getList("select * from pipelines where serial = '" + serial + "'").get(0);
		} catch ( NotFoundException e ) {
			closeSession();		
			throw e;
		} 
		logger.debug("done");
		closeSession();
		return pipeline;
	}
	
	public void deletePipeline(Pipeline pipeline) throws DeleteException {
		logger.debug("delete pipeline." );
		DaoFactory<Pipeline> df = new DaoFactory<Pipeline>();
		IDao<Pipeline> fm = df.getDao(this.session, Pipeline.class);
		try {
			fm.deleteDO(pipeline);
			commit();
		} catch (Exception e) {
			logger.error( e.getMessage() );
			rollBack();
			closeSession();
			throw new DeleteException( e.getMessage() );			
		}
		logger.debug("done.");
		closeSession();
	}
	
	public Pipeline getPipeline(int idPipeline) throws NotFoundException {
		logger.debug("retrieve");
		DaoFactory<Pipeline> df = new DaoFactory<Pipeline>();
		IDao<Pipeline> fm = df.getDao(this.session, Pipeline.class);
		Pipeline pipeline = null;
		try {
			pipeline = fm.getDO(idPipeline);
		} catch ( Exception e ) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done");
		return pipeline;
	}
	
	public void updatePipeline( Pipeline pipeline ) throws UpdateException {
		logger.debug("update");
		DaoFactory<Pipeline> df = new DaoFactory<Pipeline>();
		IDao<Pipeline> fm = df.getDao(this.session, Pipeline.class);
		try {
			fm.updateDO(pipeline);
			commit();
		} catch (UpdateException e) {
			logger.error( e.getMessage() );
			rollBack();
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done");
	}

}
