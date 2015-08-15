
package br.cefetrj.sagitarii.action;

import java.util.Set;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.core.UserTableEntity;
import br.cefetrj.sagitarii.persistence.entity.Experiment;
import br.cefetrj.sagitarii.persistence.services.ExperimentService;
import br.cefetrj.sagitarii.persistence.services.RelationService;

@Action (value = "viewTableData", 
	results = { 
		@Result ( location = "viewTableData.jsp", name = "ok"), 
	}, interceptorRefs= { @InterceptorRef("seguranca") }
) 

@ParentPackage("default")
public class ViewTableDataAction extends BasicActionClass {
	private int idExperiment;
	private Experiment experiment;
	private Set<UserTableEntity> result;
	private String tableName;
	
	public String execute () {
				
		try {
			experiment = new ExperimentService().getExperiment( idExperiment );
			result = new RelationService().inspectExperimentTable(tableName, experiment);
		} catch (Exception e) {
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

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	
}
