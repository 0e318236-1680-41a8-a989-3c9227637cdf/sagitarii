
package br.cefetrj.sagitarii.action;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.persistence.entity.CustomQuery;
import br.cefetrj.sagitarii.persistence.services.CustomQueryService;
import br.cefetrj.sagitarii.persistence.services.RelationService;

import com.opensymphony.xwork2.ActionContext;

@Action (value = "executeQueryAjaxProcess", 
	results = { 
		@Result(name="ok", type="httpheader", params={"status", "200"}) } 
	) 

@ParentPackage("default")
public class ExecuteQueryAjaxProcessAction extends BasicActionClass {
	private Integer idQuery;

	private String sEcho;
	private String iDisplayStart;
	private String iDisplayLength;
	private String iColumns;
	private String sSearch;
	private String iSortCol0;
	private String sSortDir0;
	private List<String> columns;
	
	public String execute () {
		String resp = "";
		
		try {
			RelationService rs = new RelationService();

			HttpServletRequest req = (HttpServletRequest)ActionContext.getContext().get(StrutsStatics.HTTP_REQUEST);
			sEcho = req.getParameter("sEcho");
			iDisplayStart = req.getParameter("iDisplayStart");
			iDisplayLength = req.getParameter("iDisplayLength");
			sSearch = req.getParameter("sSearch");
			iColumns = req.getParameter("iColumns");
			iSortCol0 = req.getParameter("iSortCol_0");
			sSortDir0 = req.getParameter("sSortDir_0");
			
			String sortColumn = columns.get( Integer.valueOf( iSortCol0 ) );
			
			CustomQueryService cqs = new CustomQueryService();
			CustomQuery query = cqs.getCustomQuery(idQuery);
			resp = rs.inspectExperimentQueryPagination( query , sortColumn, sSortDir0, iDisplayStart,
					iDisplayLength, sEcho);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		try { 
			HttpServletResponse response = (HttpServletResponse)ActionContext.getContext().get(StrutsStatics.HTTP_RESPONSE);
			
			response.setCharacterEncoding("UTF-8"); 
			response.setContentType("application/json");
			response.getWriter().write( resp );  
		} catch (IOException ex) {
			
		}

		return "ok";
	}

	public void setIdQuery(Integer idQuery) {
		this.idQuery = idQuery;
	}
	
	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

}
