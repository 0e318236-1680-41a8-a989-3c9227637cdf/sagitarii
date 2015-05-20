package cmabreu.sagitarii.action;

import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.core.UserTableEntity;
import cmabreu.sagitarii.persistence.services.RelationService;

import com.opensymphony.xwork2.ActionContext;

@Action(value="showtable", results= {  
	    @Result(name="ok", type="httpheader", params={"status", "200"}) }
)   

@ParentPackage("default")
public class ShowTableAction extends BasicActionClass {
	private String tableName;
	
	
	public String execute(){
		StringBuilder resp = new StringBuilder();
		resp.append("<table>");
		
		try {
			Set<UserTableEntity> structure = new RelationService().getTableStructure( tableName );
			for ( UserTableEntity inEnt : structure ) {
				String name = inEnt.getData("column_name");
				String type = inEnt.getData("data_type");
				resp.append("<tr onclick=\"doFieldClick('"+name+"')\" class='idTableField'><td class='idTableFieldName'>" + name + "</td><td>" + type + "</td></tr>");
			}
			resp.append("</table>");
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		try { 
			HttpServletResponse response = (HttpServletResponse)ActionContext.getContext().get(StrutsStatics.HTTP_RESPONSE);
			response.setCharacterEncoding("UTF-8"); 
			response.getWriter().write( resp.toString() );  
		} catch (IOException ex) {
			
		}
		
		return "ok";
	}


	public void setTableName(String tableName) {
		this.tableName = tableName;
	}


}
