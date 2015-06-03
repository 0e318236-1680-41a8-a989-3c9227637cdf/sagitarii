
package cmabreu.sagitarii.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.core.types.ExecutorType;
import cmabreu.sagitarii.persistence.entity.ActivationExecutor;
import cmabreu.sagitarii.persistence.entity.Relation;
import cmabreu.sagitarii.persistence.services.ExecutorService;
import cmabreu.sagitarii.persistence.services.RelationService;

@Action (value = "editExecutor", results = { 
		@Result (location = "${dest}", name = "ok")
	}, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 

@ParentPackage("default")
public class EditExecutorAction extends BasicActionClass {
	private int idExecutor;
	private ActivationExecutor executor;
	private List<Relation> customTables;
	private String dest;
	
	public String execute () {
		dest = "editExecutor.jsp";
		try {
			ExecutorService es = new ExecutorService();
			executor = es.getExecutor(idExecutor);
			
			if ( (executor.getType() == ExecutorType.MAP) || (executor.getType() == ExecutorType.RSCRIPT) || (executor.getType() == ExecutorType.LIBRARY) ) {
				dest = "editExecutorMap.jsp";
			}
			if ( executor.getType() == ExecutorType.SELECT ) {
				dest = "editExecutorSelect.jsp";
			}
			
			
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
	
	public String getDest() {
		return dest;
	}
}
