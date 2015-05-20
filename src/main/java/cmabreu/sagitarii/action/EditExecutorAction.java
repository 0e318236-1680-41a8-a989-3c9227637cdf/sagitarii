
package cmabreu.sagitarii.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.persistence.entity.ActivationExecutor;
import cmabreu.sagitarii.persistence.entity.Relation;
import cmabreu.sagitarii.persistence.services.ExecutorService;
import cmabreu.sagitarii.persistence.services.RelationService;

@Action (value = "editExecutor", results = { 
		@Result (location = "editExecutor.jsp", name = "ok")
	}, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 

@ParentPackage("default")
public class EditExecutorAction extends BasicActionClass {
	private int idExecutor;
	private ActivationExecutor executor;
	private List<Relation> customTables;
	
	public String execute () {
		
		try {
			ExecutorService es = new ExecutorService();
			executor = es.getExecutor(idExecutor);
			
			RelationService ts = new RelationService();
			customTables = ts.getList();
			
		} catch (Exception e) {
			
		}
		
		return "ok";
	}

	public void setIdExecutor(int idExecutor) {
		this.idExecutor = idExecutor;
	}
	
	public ActivationExecutor getExecutor() {
		return executor;
	}
	
	public List<Relation> getCustomTables() {
		return customTables;
	}
	
}
