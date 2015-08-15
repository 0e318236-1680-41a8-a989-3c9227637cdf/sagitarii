package br.cefetrj.sagitarii.core.instances;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.cefetrj.sagitarii.core.UserTableEntity;
import br.cefetrj.sagitarii.persistence.entity.ActivationExecutor;
import br.cefetrj.sagitarii.persistence.entity.Activity;
import br.cefetrj.sagitarii.persistence.entity.Consumption;
import br.cefetrj.sagitarii.persistence.entity.Fragment;
import br.cefetrj.sagitarii.persistence.entity.Instance;
import br.cefetrj.sagitarii.persistence.services.ExecutorService;
import br.cefetrj.sagitarii.persistence.services.RelationService;

public class ReduceInstanceGenerator implements IInstanceGenerator {
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
	
	@Override
	public List<Instance> generateInstances(Activity activity, Fragment frag) throws Exception {
		String relation = activity.getInputRelation().getName();
		logger.debug( "Activity '" + activity.getTag() + "' allowed to run. Fetching data from source table " + relation );
		logger.debug("generating instances...");		

		InstanceCreator pc = new InstanceCreator();
		List<Instance> pipes = new ArrayList<Instance>();
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
					+ " left join instances p on p.id_instance = u.id_instance"
					+ " where exp.id_experiment = " + frag.getExperiment().getIdExperiment();
			
			String groupingSql = "select distinct "+ fieldsDef + " from (" + correctSql + ") r1 order by " + fieldsDef;
			
			logger.debug( groupingSql );
			
			Set<UserTableEntity> groupedFields = ts.genericFetchList( groupingSql );
			String[] fields = fieldsDef.split(",");
			String selectionSql = "";
			String prefix = "";
			if ( (fields != null) && ( fields.length > 0 ) && ( groupedFields.size() > 0 ) ) {
				logger.debug("'REDUCE' type detected: " + groupedFields.size() + " instances will be created for activity " + activity.getTag());
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
					// SP,MASCULINO : 8 instances gerados
					// SP,FEMININO  : 12 instances gerados
					// RJ,MASCULINO : 3 instances gerados
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
						Instance pipe = pc.createInstance(activity, frag, parameter);
						
						pipe.setConsumptions(consumptions);
						
						pipes.add(pipe);
					} 
					
				}
				logger.debug("done. " + pipes.size() + " instance generated.");
				
			} else {
				logger.error("Empty grouping fields descriptor for " + executor.getExecutorAlias() );
			}
			
		}
		
		return pipes;
	}

}
