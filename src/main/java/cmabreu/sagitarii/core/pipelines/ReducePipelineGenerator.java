package cmabreu.sagitarii.core.pipelines;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmabreu.sagitarii.core.UserTableEntity;
import cmabreu.sagitarii.persistence.entity.ActivationExecutor;
import cmabreu.sagitarii.persistence.entity.Activity;
import cmabreu.sagitarii.persistence.entity.Consumption;
import cmabreu.sagitarii.persistence.entity.Fragment;
import cmabreu.sagitarii.persistence.entity.Pipeline;
import cmabreu.sagitarii.persistence.services.ExecutorService;
import cmabreu.sagitarii.persistence.services.RelationService;

public class ReducePipelineGenerator implements IPipelineGenerator {
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
	
	@Override
	public List<Pipeline> generatePipelines(Activity activity, Fragment frag) throws Exception {
		String relation = activity.getInputRelation().getName();
		logger.debug( "Activity '" + activity.getTag() + "' allowed to run. Fetching data from source table " + relation );
		logger.debug("generating pipelines...");		

		PipelineCreator pc = new PipelineCreator();
		List<Pipeline> pipes = new ArrayList<Pipeline>();
		RelationService ts = new RelationService();

		ExecutorService cs = new ExecutorService();
		ActivationExecutor executor = cs.getExecutor( activity.getCommand() );

		// fieldsDef : campos de agrupamento. Ex: UF,SEXO
		// Precisa ser separado por vírgula.
		String fieldsDef = executor.getSelectStatement();

		if ( (fieldsDef != null) && ( !fieldsDef.equals("") ) ) {

			// Separa os registros de acordo com o criterio de agrupamento

			String correctSql = "select u.* "
					+ " from experiments exp left join " + relation + " u on u.id_experiment = exp.id_experiment"  
					+ " left join pipelines p on p.id_pipeline = u.id_pipeline"
					+ " where exp.id_experiment = " + frag.getExperiment().getIdExperiment();
			
			String groupingSql = "select distinct "+ fieldsDef + " from (" + correctSql + ") r1 order by " + fieldsDef;
			
			logger.debug( groupingSql );
			
			Set<UserTableEntity> groupedFields = ts.genericFetchList( groupingSql );
			String[] fields = fieldsDef.split(",");
			String selectionSql = "";
			String prefix = "";
			if ( (fields != null) && ( fields.length > 0 ) && ( groupedFields.size() > 0 ) ) {
				logger.debug("'REDUCE' type detected: " + groupedFields.size() + " pipelines will be created for activity " + activity.getTag());
				StringBuilder sb = new StringBuilder();
				for ( UserTableEntity ute : groupedFields ) {
					sb.setLength(0);
					prefix = "";
					for ( String field : fields  ) {
						String value = ute.getData( field );
						sb.append( prefix + field + " = '" + value + "'" );
						prefix = " and ";
					}
					String queryDef = sb.toString();
					// Para cada registro do agrupado, repete enquando os campos do agrupamento forem iguais:
					// Ex: UF,SEXO
					// Repete: 
					// SP,MASCULINO : 8 pipelines gerados
					// SP,FEMININO  : 12 pipelines gerados
					// RJ,MASCULINO : 3 pipelines gerados
					selectionSql = "select * from (" + correctSql + ") r1 where " + queryDef;
					
					logger.debug( selectionSql );
					
					if ( !selectionSql.trim().equals("") ) {
						Set<UserTableEntity> utes = ts.genericFetchList(selectionSql);
						logger.debug(utes.size() + " lines of data was selected for this query");
						String parameter = "";
						
						Set<Consumption> consumptions = new HashSet<Consumption>();
						
						for ( UserTableEntity uteInternal : utes ) {
							if ( parameter.equals("") ) {
								parameter = pc.generateInputData( uteInternal );
							} else {
								parameter = pc.appendInputData(uteInternal, parameter);
							}
							
							// CONSUMPTION REGISTER
							int idRow = Integer.valueOf( uteInternal.getData("index_id") );
							int idTable = activity.getInputRelation().getIdTable();
							Consumption con = new Consumption();
							con.setIdRow(idRow);
							con.setIdTable(idTable);
							con.setIdActivity( activity.getIdActivity() );
							consumptions.add(con);
							// ===============================
							
						}
						Pipeline pipe = pc.createPipeline(activity, frag, parameter);
						
						pipe.setConsumptions(consumptions);
						
						pipes.add(pipe);
					} 
					
				}
				logger.debug("done. " + pipes.size() + " pipelines generated.");
				
			} else {
				logger.error("Empty grouping fields descriptor for " + executor.getExecutorAlias() );
			}
			
		}
		
		return pipes;
	}

}
