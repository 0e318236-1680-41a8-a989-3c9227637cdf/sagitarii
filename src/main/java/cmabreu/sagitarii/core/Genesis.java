package cmabreu.sagitarii.core;

/**
 * Converte a especificação JSON de um workflow em classes concretas do tipo Activity.
 * Classifica as atividades em fragmentos.
 * 
 */

import java.util.Set;
import java.util.TreeSet;

import cmabreu.sagitarii.misc.json.JsonElementConversor;
import cmabreu.sagitarii.persistence.entity.Activity;
import cmabreu.sagitarii.persistence.entity.Experiment;
import cmabreu.sagitarii.persistence.entity.Fragment;
import cmabreu.sagitarii.persistence.entity.Relation;
import cmabreu.sagitarii.persistence.services.RelationService;

public class Genesis {
	private Set<Activity> activities = new TreeSet<Activity>();
	private Activity root = null;

	private JsonElementConversor conversor  = new JsonElementConversor();
	private Experiment experiment;

	public void setExperiment( Experiment experiment ) {
		this.experiment = experiment;
	}
	
	private void convert() throws Exception {
		setActivities( conversor.convert(experiment) );
	}
	
	/**
	 * Verifica se as tabelas de entrada das atividades de entrada possuem dados.
	 */
	private void checkEntrancePointsAvailability() {
		boolean canRun = true;
		try {
			RelationService rs = new RelationService();
			for ( Activity act : activities  ) {
				// Se for uma atividade de início de workflow (não há atividades anteriores)...
				if ( act.getPreviousActivities().size() == 0 ) {
					// Percorre as suas tabelas de entrada para ver se possuem dados.
					for ( Relation rel : act.getInputRelations() ) {
						String inputTable = rel.getName();
						int count = rs.getCount( inputTable, "where id_experiment = " + experiment.getIdExperiment() );
						rel.setSize( count );
						if ( count == 0 ) {
							canRun = false;
						}
					}
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
			canRun = false;
		}
		experiment.setAvailability( canRun );
	}

	
	public Experiment generate( Experiment experiment ) throws Exception {
		if ( experiment != null ) {
			this.experiment = experiment;
			convert();
		}
		checkEntrancePointsAvailability();
		return this.experiment;
	}


	public Experiment checkTables( Experiment experiment ) throws Exception {
		this.experiment = experiment;
		for ( Fragment frag : experiment.getFragments()  ) {
			activities.addAll( frag.getActivities() );
		}
		checkEntrancePointsAvailability();
		return this.experiment;
	}
	
	
	public Activity getRoot() {
		return root;
	}

	public Set<Activity> getActivities() {
		return activities;
	}
	
	private void setActivities( Set<Activity> activities ) {
		root = null;
		this.activities = activities;
		for ( Activity act : activities  ) {
			if ( act.getPreviousActivities().size() == 0 ) {
				root = act;
				break;
			}
		}
		getFragments();
	}
	
	private void getFragments() {
		if ( activities.size() > 0 ) {
			FragmentCreator fc = new FragmentCreator();
			this.experiment.setFragments( fc.getFragments( activities ) );
		}
	}
	
}
