package cmabreu.sagitarii.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmabreu.sagitarii.core.pipelines.PipelineGeneratorFactory;
import cmabreu.sagitarii.core.types.FragmentStatus;
import cmabreu.sagitarii.misc.FragmentComparator;
import cmabreu.sagitarii.persistence.entity.Activity;
import cmabreu.sagitarii.persistence.entity.Experiment;
import cmabreu.sagitarii.persistence.entity.Fragment;
import cmabreu.sagitarii.persistence.entity.Pipeline;
import cmabreu.sagitarii.persistence.entity.Relation;
import cmabreu.sagitarii.persistence.services.FragmentService;
import cmabreu.sagitarii.persistence.services.PipelineService;
import cmabreu.sagitarii.persistence.services.RelationService;

public class FragmentPipeliner {
	private List<Fragment> fragments;
	private Experiment experiment;
	List<Pipeline> pipes = new ArrayList<Pipeline>();
	Logger logger = LogManager.getLogger( this.getClass().getName() );
	
	/**
	 * Check if a Fragment contains an Activity
	 * 
	 * @param fragment
	 * @param act
	 * @return
	 */
	private boolean contains( Fragment fragment, Activity act ) {
		for ( Activity activity : fragment.getActivities() ) {
			if ( activity.equals( act ) ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Localiza qual atividade é a inicial do fragmento analizando se sua(s) atividade(s)
	 * de entrada estão fora de seu próprio fragmento.
	 *  
	 * @param fragment
	 * @return
	 */
	private Activity getEntrancePoint( Fragment fragment ) {
		for ( Activity act : fragment.getActivities() ) {
			if ( act.getPreviousActivities().size() == 0 ) {
				return act;
			} else {
				for( Activity input : act.getPreviousActivities() ) {
					if ( !contains(fragment, input ) ) {
						return act;
					}
				}
			}
		}
		return null;
	}
	
	
	private void sort() {
		FragmentComparator fc = new FragmentComparator();
		Collections.sort(this.fragments, fc);
	}	
	
	/**
	 * Construtor.
	 * 
	 * @param experiment o experimento a ser processado
	 * 
	 */
	public FragmentPipeliner( Experiment experiment ) {
		this.experiment = experiment;
		this.fragments = experiment.getFragments();
		sort();
	}
	
	/**
	 * Verifica se a tabela tableName possui algum registro.
	 * 
	 * @param tableName
	 * 
	 * @return se possui ou nao 
	 */
	private boolean haveTableSomeData( String tableName ) {
		logger.debug("verify table " + tableName + " data...");
		boolean result = false;
		try {
			RelationService ts = new RelationService();
			Set<UserTableEntity> ute = ts.genericFetchList("select * from " + tableName + " where id_experiment = " + experiment.getIdExperiment() );
			result = ( ute.size() > 0 );
		} catch ( Exception e ) { /** Any error = have no data **/ }
		if ( !result ) {
			logger.debug("table " + tableName + " is empty.");
		}
		return result;
	}
	
	/**
	 * Verifica se a atividade 'activity' está apta a ser executada.
	 * Se é uma atividade de início de Workflow ( não há atividades de entrada )
	 * verifica se a tabela de entrada possui dados.
	 * 
	 * Se é uma atividade de início de fragmento ( possui atividade(s) de entrada )
	 * então verifica se todas estão como "FINISHED". Então verifica se todas as 
	 * tabelas de entrada possuem dados.  
	 *   
	 * @param activity
	 * @return
	 */
	private boolean isAllowedToRun( Activity activity ) {
		int count = 0;
		if ( activity.getPreviousActivities().size() == 0 ) {
			// � ponto de entrada do workflow.
			// Verificar se a tabela de entrada possui dados.
			logger.debug(activity.getTag() +  " is workflow entrance point. checking data availability...");
			boolean finalResult = activity.getInputRelations().size() > 0;
			for ( Relation rel : activity.getInputRelations() ) {
				finalResult = finalResult && haveTableSomeData( rel.getName() ); 
			}
			return finalResult; 
		} else {
			// � inicial de fragmento. 
			// Verificar se todas as atividades anteriores produziram dados.
			logger.debug(activity.getTag() +  " is fragment entrance point. checking data availability...");
			for ( Activity act : activity.getPreviousActivities() ) {
				if ( !haveTableSomeData( act.getOutputRelation().getName() ) ) {
					count++;
					logger.debug( "needed activity " + act.getTag() + " (" + act.getSerial() + ") produced no data. Table " + act.getOutputRelation().getName() );
				} else {
					logger.debug( "needed activity " + act.getTag() + " (" + act.getSerial() + ") produced data in table " + act.getOutputRelation().getName() );
				}
			}
			if ( count > 0 ) { return false; }
		}
		return true;
	}
	
	
	/**
	 *	Para cada fragmento da lista verifica se sua atividade de entrada
	 *	pode ser executada. Gera os pipelines, salva no banco e atualiza o 
	 *	status do fragmento para PIPELINED
	 * @throws Exception 
	 */
	public void generate() throws Exception {
		try {

			for ( Fragment frag : fragments ) {
				if ( ( frag.getStatus() == FragmentStatus.PREVIEW ) || ( frag.getStatus() == FragmentStatus.READY ) ) {
					logger.debug("will create pipes for fragment " + frag.getSerial() );
					Activity act = getEntrancePoint( frag );
					if ( act != null ) {
						logger.debug("entrance point: activity " + act.getTag() );
						if ( isAllowedToRun(act) ) {
							
							pipes = PipelineGeneratorFactory.getGenerator( act.getType() ).generatePipelines(act, frag);
	
							if ( pipes.size() > 0 ) {
								logger.debug("done. " + pipes.size() + " pipelines generated. Will store...");
								new PipelineService().insertPipelineList(pipes);
								logger.debug("done storing pipelines to database. Updating fragment status...");
								
								frag.setStatus( FragmentStatus.PIPELINED );
								frag.setRemainingPipelines( pipes.size() );
								frag.setTotalPipelines( pipes.size() );
								
							} else {
								logger.debug("ERROR: no pipelines was created.");
							}
							logger.debug("done");
							
						} else {
							logger.debug("not allowed to run");
							frag.setStatus( FragmentStatus.READY );
						}
						new FragmentService().updateFragment( frag );
					} else {
						logger.debug("no entrance point");
					}
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
			throw e;
		}
	}

	
	public List<Pipeline> getPipelines() {
		return pipes;
	}
	
}
