
package br.cefetrj.sagitarii.action;

import java.util.List;
import java.util.Set;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.core.UserTableEntity;
import br.cefetrj.sagitarii.persistence.entity.CustomQuery;
import br.cefetrj.sagitarii.persistence.entity.Experiment;
import br.cefetrj.sagitarii.persistence.entity.Relation;
import br.cefetrj.sagitarii.persistence.exceptions.NotFoundException;
import br.cefetrj.sagitarii.persistence.services.CustomQueryService;
import br.cefetrj.sagitarii.persistence.services.ExperimentService;

@Action (value = "inspectExperiment", 
	results = { 
		@Result ( location = "inspectExperiment.jsp", name = "ok") 
	}, interceptorRefs= { @InterceptorRef("seguranca")	 }) 

@ParentPackage("default")
public class InspectExperimentAction extends BasicActionClass {
	private int idExperiment;
	private Experiment experiment;
	private Set<UserTableEntity> result;
	private String sql;
	private List<Relation> customTables;
	private Set<CustomQuery> queries;
	
	public String execute () {
				
		try {
			experiment = new ExperimentService().getExperiment( idExperiment );
			customTables =  experiment.getUsedTables(); 
			experiment.updateMetrics();
			try {
				CustomQueryService cqs = new CustomQueryService();
				queries = cqs.getList( idExperiment );
			} catch ( NotFoundException ignored ) {
				//
			} 
			
			
		} catch ( Exception e ) {
			setMessageText("Error: " + e.getMessage() );
			return "ok";
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
	
	public Set<UserTableEntity> getResult() {
		return result;
	}
	
	public void setSql(String sql) {
		this.sql = sql;
	}
	
	public String getSql() {
		return sql;
	}
	
	public List<Relation> getCustomTables() {
		return customTables;
	}

	public Set<CustomQuery> getQueries() {
		return queries;
	}
	
}
