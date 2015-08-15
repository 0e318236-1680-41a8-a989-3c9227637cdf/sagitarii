
package br.cefetrj.sagitarii.action;

import java.util.Set;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.core.UserTableEntity;
import br.cefetrj.sagitarii.persistence.services.RelationService;

@Action (value = "runUserSql", 
	results = { 
		@Result ( location = "runUserSql.jsp", name = "ok"), 
	}, interceptorRefs= { @InterceptorRef("seguranca") }
) 

@ParentPackage("default")
public class RunUserSqlAction extends BasicActionClass {
	private Set<UserTableEntity> result;
	private String sql;
	
	public String execute () {
		
		if ( sql != null && !sql.equals("" )) {
			try {
				result = new RelationService().runUserSql(sql);
			} catch (Exception e) {
				setMessageText("Error: " + e.getMessage() );
				return "ok";
			}
		}
		return "ok";
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
	
}
