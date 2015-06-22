package cmabreu.sagitarii.core.instances;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmabreu.sagitarii.core.UserTableEntity;
import cmabreu.sagitarii.persistence.entity.Activity;
import cmabreu.sagitarii.persistence.entity.Consumption;
import cmabreu.sagitarii.persistence.entity.Fragment;
import cmabreu.sagitarii.persistence.entity.Instance;
import cmabreu.sagitarii.persistence.services.RelationService;

public class MapInstanceGenerator implements IInstanceGenerator {
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
	
	@Override
	public List<Instance> generateInstances(Activity activity, Fragment frag) throws Exception {
		String relation = activity.getInputRelation().getName();
		logger.debug( "Activity '" + activity.getTag() + "' allowed to run. Fetching data from source table " + relation );
		logger.debug("generating instances...");		

		InstanceCreator pc = new InstanceCreator();
		List<Instance> pipes = new ArrayList<Instance>();
		RelationService ts = new RelationService();
		
		String correctSql = "select u.* "
				+ " from experiments exp left join " + relation + " u on u.id_experiment = exp.id_experiment"  
				+ " left join instances p on p.id_instance = u.id_instance"
				+ " where exp.id_experiment = " + frag.getExperiment().getIdExperiment();

		Set<UserTableEntity> utes = ts.genericFetchList( correctSql );
		
		logger.debug( correctSql );
		logger.debug("'MAP' type detected: " + utes.size() + " instances will be created for activity " + activity.getTag() );
		
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
			Instance pipe = pc.createInstance(activity, frag, parameter );
			pipe.addConsumption(con);
			
			pipes.add(pipe);
		}
		return pipes;
	}

}
