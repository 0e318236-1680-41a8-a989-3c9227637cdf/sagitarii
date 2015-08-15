
package br.cefetrj.sagitarii.action;

import java.util.Set;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.core.UserTableEntity;
import br.cefetrj.sagitarii.persistence.services.RelationService;

@Action (value = "viewSql", 
	results = { 
		@Result ( location = "viewSQL.jsp", name = "ok"), 
	}, interceptorRefs= { @InterceptorRef("seguranca") }
) 

@ParentPackage("default")
public class ViewSqlAction extends BasicActionClass {
	private Set<UserTableEntity> result;
	private String tableName;
	
	public String execute () {
				
		try {
			result = new RelationService().viewSql( tableName );
		} catch (Exception e) {
			setMessageText("Error: " + e.getMessage() );
			return "ok";
		}
		return "ok";
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
