
package br.cefetrj.sagitarii.action;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import com.opensymphony.xwork2.ActionContext;

@Action (value = "experimentFilesAjaxProcess", 
	results = { 
		@Result(name="ok", type="httpheader", params={"status", "200"}) } 
	) 

@ParentPackage("default")
public class ExperimentFilesAjaxProcessAction extends BasicActionClass {
	private Integer idExperiment;
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
		
		/*
		try {
			HttpServletRequest req = (HttpServletRequest)ActionContext.getContext().get(StrutsStatics.HTTP_REQUEST);
			sEcho = req.getParameter("sEcho");
			iDisplayStart = req.getParameter("iDisplayStart");
			iDisplayLength = req.getParameter("iDisplayLength");
			sSearch = req.getParameter("sSearch");
			iColumns = req.getParameter("iColumns");
			iSortCol0 = req.getParameter("iSortCol_0");
			sSortDir0 = req.getParameter("sSortDir_0");

			String sortColumn = columns.get( Integer.valueOf( iSortCol0 ) );
			
			FileService fs = new FileService();	
			
			resp = fs.getFilesPagination( idExperiment, sortColumn, sSortDir0, iDisplayStart, iDisplayLength, sEcho, sSearch);
			
			resp = resp.replace("\\", "\\\\");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		try { 
			HttpServletResponse response = (HttpServletResponse)ActionContext.getContext().get(StrutsStatics.HTTP_RESPONSE);
			
			response.setCharacterEncoding("UTF-8"); 
			response.setContentType("application/json");
			response.getWriter().write( resp );  
		} catch (IOException ex) {
			
		}

		return "ok";
	}


	public void setIdExperiment(Integer idExperiment) {
		this.idExperiment = idExperiment;
	}


	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public void setsSearch(String sSearch) {
		this.sSearch = sSearch;
	}
	
	public String getsSearch() {
		return sSearch;
	}
	
	public void setiColumns(String iColumns) {
		this.iColumns = iColumns;
	}
	
	public String getiColumns() {
		return iColumns;
	}
}
