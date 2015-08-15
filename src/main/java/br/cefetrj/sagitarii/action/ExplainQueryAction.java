package br.cefetrj.sagitarii.action;

import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.core.UserTableEntity;
import br.cefetrj.sagitarii.persistence.services.RelationService;

import com.opensymphony.xwork2.ActionContext;

@Action (value = "explainQuery", 
results = { 
	@Result(name="ok", type="httpheader", params={"status", "200"}) } 
) 

@ParentPackage("default")
public class ExplainQueryAction extends BasicActionClass {
		private String query;
		
		public String execute () {
			String resp = "";
			
			try {
				RelationService rs = new RelationService();
				
				query = query.replace("%ID_EXP%", "1");
				query = query.replace("%ID_PIP%", "1");
				query = query.replace("%ID_ACT%", "1");
				
				Set<UserTableEntity> explaination = rs.genericFetchList( "explain " + query );
				for ( UserTableEntity entity : explaination ) {
					String columnName = entity.getColumnNames().get(0);
					String data = entity.getData( columnName );
					resp = resp + data + "<br>";
				}
				
			} catch (Exception e) {	
				resp = e.getMessage();
			}

			
			try { 
				HttpServletResponse response = (HttpServletResponse)ActionContext.getContext().get(StrutsStatics.HTTP_RESPONSE);
				
				response.setCharacterEncoding("UTF-8"); 
				response.setContentType("text/plain");
				response.getWriter().write( resp );  
			} catch (IOException ex) {
				
			}

			return "ok";
	}

	
	public void setQuery(String query) {
		this.query = query;
	}	
}
