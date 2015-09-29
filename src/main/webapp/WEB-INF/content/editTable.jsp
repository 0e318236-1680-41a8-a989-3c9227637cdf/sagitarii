<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../../header.jsp" %>

				<div id="leftBox"> 
					<div id="bcbMainButtons" class="basicCentralPanelBar">
						<%@ include file="buttons.jsp" %>
					</div>
					
					<div id="basicCentralPanel">
					
						<div class="basicCentralPanelBar">
							<img src="img/tables.png">
							<div class="basicCentralPanelBarText">Edit Relation ${tableName}</div>
						</div>
						
						
						<div class="menuBarMain">
							<img alt="" onclick="back();" title="Back" class="button dicas" src="img/back.png" />
						</div>

						<div style="margin : 0 auto; width : 70%; margin-top:10px;"  >
							<form action="editTable" method="POST" id="formPost">
								<input type="hidden" name="op" value="add">
								<input type="hidden" name="tableName" value="${tableName}">
								<table class="tableForm" style="width:100%" >
									<thead>
										<tr>
											<th>Column Name</th>
											<th>Column Type</th>
											<th>&nbsp;</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="column" items="${attributes}">
											<tr>
												<td>${column.name}</td>
												<td>${column.type}</td>
												<td>
													<img class="miniButton dicas" title="Delete" onclick="askDeleteAttribute('${column.name}','${tableName}')" src="img/delete.png">
												</td>
											</tr>
										</c:forEach>
										<tr>
											<td> 
												<input id="columnName" style="float:left; width:100px" name="columnName" value="" class="tableCellFormInputText" type="text">
											</td>
											<td>
												<select style="float:left; width:100px" name="columnType" class="tableCellFormInputText">
													<option value="INTEGER">Integer</option>
													<option value="FILE">File</option>
													<option value="STRING">String</option>
													<option value="TEXT">Text</option>
													<option value="FLOAT">Float</option>
													<option value="DATE">Date</option>
												</select> 
											</td>
											<td>
												<img class="miniButton dicas" title="Add New Field" onclick="doPost();" src="img/add.png">	
											</td>
	
										</tr>									
									</tbody>
								</table>
							</form>
							
							<div style="float:left;color:#F90101;">Warning: Changes in table structure may cause data loss, data corruption or invalidate Activities.
							Sagitarii will not track it for you and it can be hard to debug. Make sure this table is empty before proceed.</div>
							
						</div>

					</div>												
					
				</div>
				<div id="rightBox"> 
					<%@ include file="commonpanel.jsp" %>
				</div>
				
				
<script>

	function askDeleteAttribute( column, table ) {
		showDialogBox( "This will delete column "+column+" from table "+table+".<br><br>ARE YOU SURE?", "editTable?tableName=${tableName}&op=remove&columnName=" + column );
	}
	
	function doPost() {
		var columnName = $("#columnName").val();
		if ( columnName.indexOf(' ') >= 0 ) {
			$("#columnName").focus();
			showMessageBox("The field name cannot have white spaces.");
			return false;
		}		

		if ( columnName.length == 0 ) {
			$("#columnName").focus();
			showMessageBox("You must provide a field name.");
			return false;
		}		
		$("#formPost").submit();
		
	}

	$(document).ready(function() {
		//
	} );	
	
	
</script>				
				
<%@ include file="../../footer.jsp" %>
				