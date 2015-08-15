
package br.cefetrj.sagitarii.action;

import java.util.List;
import java.util.Set;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.core.types.ExperimentStatus;
import br.cefetrj.sagitarii.persistence.entity.ActivationExecutor;
import br.cefetrj.sagitarii.persistence.entity.Experiment;
import br.cefetrj.sagitarii.persistence.entity.Relation;
import br.cefetrj.sagitarii.persistence.exceptions.DatabaseConnectException;
import br.cefetrj.sagitarii.persistence.exceptions.NotFoundException;
import br.cefetrj.sagitarii.persistence.services.ExecutorService;
import br.cefetrj.sagitarii.persistence.services.ExperimentService;
import br.cefetrj.sagitarii.persistence.services.RelationService;

@Action (value = "editExperiment", 
	results = { 
		@Result ( location = "editExperiment.jsp", name = "ok"),
		@Result ( location="viewExperiments", type="redirect", name="erro") 
	} , interceptorRefs= { @InterceptorRef("seguranca")	 }) 

@ParentPackage("default")
public class EditExperimentAction extends BasicActionClass {
	private int idExperiment;
	private Experiment experiment;
	private List<Relation> tables;
	private Set<ActivationExecutor> criterias;
	
	
	public String execute () {
		
		try {
			RelationService ts = new RelationService();
			ExecutorService cs = new ExecutorService();
			ExperimentService es = new ExperimentService();
			try {
				criterias = cs.getList();
			} catch ( NotFoundException e1 ) {
				setMessageText("No selections criterias registered. You will not be able to create SELECT type Activities.");
			}
			
			experiment = es.getExperiment(idExperiment);
			
			if ( experiment.getStatus() != ExperimentStatus.STOPPED ) {
				setMessageText("Only stopped experiments can be edited.");
				return "erro";
			}
			
			try {
				tables = ts.getList();
			} catch ( NotFoundException e ) {
				setMessageText("No input/output relations registered. Go to <b>Custom Tables</b> and register the needed relations.");
				return "erro";
			}
			
		} catch (DatabaseConnectException e) {
			setMessageText("Error: " + e.getMessage() );
			return "erro";
		} catch (NotFoundException e) {
			setMessageText("Error: Experiment ID " + idExperiment + " not found");
			return "erro";
		}
		
		return "ok";
	}


	public int getIdExperiment() {
		return idExperiment;
	}


	public void setIdExperiment(int idExperiment) {
		this.idExperiment = idExperiment;
	}


	public Experiment getExperiment() {
		return experiment;
	}


	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}


	public List<Relation> getTables() {
		return tables;
	}


	public void setTables(List<Relation> tables) {
		this.tables = tables;
	}


	public Set<ActivationExecutor> getCriterias() {
		return criterias;
	}


	public void setCriterias(Set<ActivationExecutor> criterias) {
		this.criterias = criterias;
	}

	
	
}
