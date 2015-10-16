package br.cefetrj.sagitarii.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.core.TableAttribute;
import br.cefetrj.sagitarii.persistence.services.RelationService;

@Action (value = "editTable", 
results = { 
	@Result ( location = "editTable.jsp", name = "ok")
} , interceptorRefs= { @InterceptorRef("seguranca")	 }) 

@ParentPackage("default")
public class EditTableAction extends BasicActionClass {
	private String tableName;
	private List<TableAttribute> attributes;
	private String columnName;
	private String columnType;
	private String op;
	
	public String execute(){
		try {
			
			if ( op != null ) {
				// Remove column
				if ( op.equals("remove") ) {
					if ( columnName != null && tableName != null ) {
						new RelationService().dropColumn( tableName, columnName ); 
						setMessageText("Column " + columnName + " deleted from table " + tableName );
					}
				} else
				// Add column
				if ( op.equals("add") ) {
					if ( (columnName != null) && (tableName != null) && (columnType != null) ) {
						new RelationService().addColumn( tableName, columnName, columnType );
						setMessageText("Column " + columnName + " added for table " + tableName );
					}
				}
			}
			
		} catch ( Exception e ) {
			setMessageText( e.getMessage() );
		}

		
		try {
			attributes = new RelationService().getAttributes( tableName );
		} catch ( Exception e ) {
			setMessageText( e.getMessage() );
		}
		return "ok";
	}


	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<TableAttribute> getAttributes() {
		return attributes;
	}
	
	public void setOp(String op) {
		this.op = op;
	}

	public String getTableName() {
		return tableName;
	}
	
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}
	
}
