package cmabreu.sagitarii.core.pipelines;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmabreu.sagitarii.core.UserTableEntity;
import cmabreu.sagitarii.persistence.entity.Activity;
import cmabreu.sagitarii.persistence.entity.Consumption;
import cmabreu.sagitarii.persistence.entity.Fragment;
import cmabreu.sagitarii.persistence.entity.Pipeline;
import cmabreu.sagitarii.persistence.services.RelationService;

public class MapPipelineGenerator implements IPipelineGenerator {
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
	
	@Override
	public List<Pipeline> generatePipelines(Activity activity, Fragment frag) throws Exception {
		String relation = activity.getInputRelation().getName();
		logger.debug( "Activity '" + activity.getTag() + "' allowed to run. Fetching data from source table " + relation );
		logger.debug("generating pipelines...");		

		PipelineCreator pc = new PipelineCreator();
		List<Pipeline> pipes = new ArrayList<Pipeline>();
		RelationService ts = new RelationService();
		
		String correctSql = "select u.* "
				+ " from experiments exp left join " + relation + " u on u.id_experiment = exp.id_experiment"  
				+ " left join pipelines p on p.id_pipeline = u.id_pipeline"
				+ " where exp.id_experiment = " + frag.getExperiment().getIdExperiment();

		Set<UserTableEntity> utes = ts.genericFetchList( correctSql );
		
		logger.debug("'MAP' type detected: " + utes.size() + " pipelines will be created for activity " + activity.getTag() );
		
		for ( UserTableEntity ute : utes ) {
			String parameter = pc.generateInputData( ute );
			
			// CONSUMPTION REGISTER
			int idRow = Integer.valueOf( ute.getData("index_id") );
			int idTable = activity.getInputRelation().getIdTable();
			Consumption con = new Consumption();
			con.setIdRow(idRow);
			con.setIdActivity( activity.getIdActivity() );
			con.setIdTable(idTable);
			// ===============================
			
			Pipeline pipe = pc.createPipeline(activity, frag, parameter );
			pipe.addConsumption(con);
			
			pipes.add(pipe);
		}
		return pipes;
	}

}
