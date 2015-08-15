
package br.cefetrj.sagitarii.action;

import java.util.Set;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.core.UserTableEntity;
import br.cefetrj.sagitarii.persistence.entity.CustomQuery;
import br.cefetrj.sagitarii.persistence.services.CustomQueryService;
import br.cefetrj.sagitarii.persistence.services.RelationService;

@Action (value = "executeQuery", 
	results = { 
		@Result ( location = "executeQuery.jsp", name = "ok"), 
	}, interceptorRefs= { @InterceptorRef("seguranca") }
) 

@ParentPackage("default")
public class ExecuteQueryAction extends BasicActionClass {
	private int idQuery;
	private Set<UserTableEntity> result;
	private CustomQuery query;
	
	public String execute () {
				
		try {
			CustomQueryService cqs = new CustomQueryService();
			query = cqs.getCustomQuery(idQuery);
			result = new RelationService().genericFetchList( query.getQuery() );
			
		} catch (Exception e) {
			setMessageText("Error: " + e.getMessage() );
			return "ok";
		}
		return "ok";
	}

	public Set<UserTableEntity> getResult() {
		return result;
	}

	public void setIdQuery(int idQuery) {
		this.idQuery = idQuery;
	}
	
	public CustomQuery getQuery() {
		return query;
	}
	
}
