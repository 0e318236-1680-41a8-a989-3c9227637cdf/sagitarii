
package br.cefetrj.sagitarii.action;

import java.util.List;
import java.util.Set;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.persistence.entity.ActivationExecutor;
import br.cefetrj.sagitarii.persistence.entity.Relation;
import br.cefetrj.sagitarii.persistence.entity.Workflow;
import br.cefetrj.sagitarii.persistence.exceptions.DatabaseConnectException;
import br.cefetrj.sagitarii.persistence.exceptions.NotFoundException;
import br.cefetrj.sagitarii.persistence.services.ExecutorService;
import br.cefetrj.sagitarii.persistence.services.RelationService;
import br.cefetrj.sagitarii.persistence.services.WorkflowService;

@Action (value = "actManager", 
	results = { 
		@Result ( location = "activities.jsp", name = "ok"),
		@Result ( location="indexRedir", type="redirect", name="erro") 
	} , interceptorRefs= { @InterceptorRef("seguranca")	 }) 

@ParentPackage("default")
public class ActivitiesManagerAction extends BasicActionClass {
	private int idWorkflow;
	private Workflow workflow;
	private List<Relation> tables;
	private Set<ActivationExecutor> criterias;
	
	
	public String execute () {
		
		try {
			RelationService ts = new RelationService();
			WorkflowService ws = new WorkflowService();
			ExecutorService cs = new ExecutorService();
			try {
				criterias = cs.getList();
			} catch ( NotFoundException e1 ) {
				setMessageText("No executors registered.");
				return "erro";
			} catch ( Exception e1 ) {
				setMessageText("Error: " + e1.getMessage() );
			}
			
			
			workflow = ws.getWorkflow(idWorkflow);
			try {
				tables = ts.getList();
			} catch ( NotFoundException e ) {
				setMessageText("No input/output relations registered. Go to <b>Custom Tables</b> and register the needed relations.");
				return "erro";
			}
			
		} catch (DatabaseConnectException e) {
			e.printStackTrace();
			return "erro";
		} catch (NotFoundException e) {
			// Não Encontrou
			setMessageText("Error: Workflow ID "+idWorkflow+" not found");
			return "erro";
		}
		
		return "ok";
	}

	public int getIdWorkflow() {
		return idWorkflow;
	}

	public void setIdWorkflow(int idWorkflow) {
		this.idWorkflow = idWorkflow;
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
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
	
	
}
