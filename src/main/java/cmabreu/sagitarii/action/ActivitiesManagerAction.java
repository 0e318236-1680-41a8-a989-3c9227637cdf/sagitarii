
package cmabreu.sagitarii.action;

import java.util.List;
import java.util.Set;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.persistence.entity.ActivationExecutor;
import cmabreu.sagitarii.persistence.entity.Relation;
import cmabreu.sagitarii.persistence.entity.Workflow;
import cmabreu.sagitarii.persistence.exceptions.DatabaseConnectException;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.services.ExecutorService;
import cmabreu.sagitarii.persistence.services.RelationService;
import cmabreu.sagitarii.persistence.services.WorkflowService;

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
				setMessageText("No selections criterias registered. You will not be able to create SELECT type Activities.");
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
