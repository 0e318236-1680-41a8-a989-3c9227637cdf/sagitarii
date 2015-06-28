package cmabreu.sagitarii.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmabreu.sagitarii.core.instances.InstanceGeneratorFactory;
import cmabreu.sagitarii.core.types.FragmentStatus;
import cmabreu.sagitarii.misc.FragmentComparator;
import cmabreu.sagitarii.persistence.entity.Activity;
import cmabreu.sagitarii.persistence.entity.Experiment;
import cmabreu.sagitarii.persistence.entity.Fragment;
import cmabreu.sagitarii.persistence.entity.Instance;
import cmabreu.sagitarii.persistence.entity.Relation;
import cmabreu.sagitarii.persistence.services.FragmentService;
import cmabreu.sagitarii.persistence.services.InstanceService;
import cmabreu.sagitarii.persistence.services.RelationService;

public class FragmentInstancer {
	private List<Fragment> fragments;
	private Experiment experiment;
	List<Instance> instances = new ArrayList<Instance>();
	Logger logger = LogManager.getLogger( this.getClass().getName() );
	
	/**
	 * Check if a Fragment contains an Activity
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
	 */
	public FragmentInstancer( Experiment experiment ) {
		this.experiment = experiment;
		this.fragments = experiment.getFragments();
		sort();
	}
	
	/**
	 * Verifica se a tabela tableName possui algum registro.
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

	
	private boolean checkExperimentStartPoint( Activity activity ) {
		if ( activity.getPreviousActivities().size() == 0 ) {
			// Is the Experiment start point. Check data availability.
			logger.debug(activity.getTag() +  " is Experiment entrance point. checking data availability...");
			boolean finalResult = activity.getInputRelations().size() > 0;
			int count = 0;
			for ( Relation rel : activity.getInputRelations() ) {
				if ( !haveTableSomeData( rel.getName() ) ) {
					count++;
					logger.debug( " > needed source table " + rel.getName() + " produced no data.");
				} else {
					logger.debug( " > needed source table " + rel.getName() + " have data.");
				}
			}
			int totalAvailability = activity.getInputRelations().size() - count;
			finalResult = finalResult && ( totalAvailability > 0  );
			return finalResult; 
		} else {
			return true;
		}
	}
	
	private boolean checkSourceDataAvailability( Activity activity ) {
		boolean canRun = false;  
		if ( activity.getPreviousActivities().size() > 0 ) {
			// Is the Fragment start point. Check all previous activities for produced data. 
			logger.debug(activity.getTag() +  " is Fragment entrance point. checking data availability...");
			int count = 0;
			for ( Activity act : activity.getPreviousActivities() ) {
				if ( !haveTableSomeData( act.getOutputRelation().getName() ) ) {
					count++;
					logger.debug( " > needed activity " + act.getTag() + " (" + act.getSerial() + ") produced no data. Table " + act.getOutputRelation().getName() );
				} else {
					logger.debug( " > needed activity " + act.getTag() + " (" + act.getSerial() + ") produced data in table " + act.getOutputRelation().getName() );
				}
			}
			int totalAvailability = activity.getPreviousActivities().size() - count;
			if ( totalAvailability == 0  ) {
				canRun = false;
			}
		} else {
			return checkExperimentStartPoint( activity );
		}
		// canRun == false means: Its a Experiment entrance point ( previous activities = 0 ) or we have no data anywhere
		return canRun;
	}
	
	
	/**
	 *	Para cada fragmento da lista verifica se sua atividade de entrada
	 *	pode ser executada. Gera os instances, salva no banco e atualiza o 
	 *	status do fragmento para PIPELINED
	 */
	public void generate() throws Exception {
		try {

			for ( Fragment frag : fragments ) {
				if ( ( frag.getStatus() == FragmentStatus.PREVIEW ) || ( frag.getStatus() == FragmentStatus.READY ) ) {
					logger.debug("will create pipes for fragment " + frag.getSerial() );
					Activity act = getEntrancePoint( frag );
					if ( act != null ) {
						logger.debug("entrance point: activity " + act.getTag() );
						// Check if any of source tables have any data...
						if ( checkSourceDataAvailability( act ) ) {
							instances = InstanceGeneratorFactory.getGenerator( act.getType() ).generateInstances(act, frag);
							if ( instances.size() > 0 ) {
								logger.debug("done. " + instances.size() + " instances generated. will store...");
								new InstanceService().insertInstanceList(instances);
								logger.debug("done storing instances to database. updating fragment status...");
								
								frag.setStatus( FragmentStatus.PIPELINED );
								frag.setRemainingInstances( instances.size() );
								frag.setTotalInstances( instances.size() );
								FragmentService fs = new FragmentService();
								fs.updateFragment( frag );
								
							} else {
								logger.error("no instances were created.");
							}
							logger.debug("done");
						} else {
							if ( checkExperimentStartPoint(act) ) {
								logger.warn("no data found in any source tables. cannot run this Experiment.");
								// Its an Experiment entrance point with no data. Cannot Run!
							} else {
								// Its a Fragment entrance point with no data. Finish it!
								logger.warn("no data found in any source tables. finishing this Fragment...");
								frag.setStatus( FragmentStatus.FINISHED );
								frag.setRemainingInstances( 0 );
								frag.setTotalInstances( 0 );
								FragmentService fs = new FragmentService();
								fs.updateFragment( frag );
							}
						}
						
					} else {
						logger.debug("no entrance point. aborting...");
					}
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
			throw e;
		}
	}

	
	public List<Instance> getInstances() {
		return instances;
	}
	
}
