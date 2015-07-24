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
							<div class="basicCentralPanelBarText">Manage Relations</div>
						</div>
						
						
						<div class="menuBarMain">
							<img alt="" onclick="back();" title="Back" class="button dicas" src="img/back.png" />
							<img onclick="showNewPannel();" title="New Relation" class="button dicas" src="img/add.png">
						</div>


						<div id="newPannel" style="display:none; height:180px; width:95%; margin:0 auto;margin-top:10px;margin-bottom:10px;">
							<form action="doNewTable" method="POST" id="formPost">
								<table id="tblAddRel">
									<tr>
										<td class="tableCellFormLeft">Name</td>
										<td class="tableCellFormRight"> 
											<input maxlength="20" id="tableName" name="table.name" value="" class="tableCellFormInputText" type="text"> 
										</td>
									</tr>
	
									<tr>
										<td class="tableCellFormLeft">Description</td>
										<td class="tableCellFormRight"> 
											<input maxlength="100" id="tableDescription" name="table.description" value="" class="tableCellFormInputText" type="text"> 
										</td>
									</tr>
								
									<tr>
										<td class="tableCellFormLeft">Field</td>
										<td class="tableCellFormRight"> 
											<input id="firstField" style="float:left; width:100px" name="attributes[0].name" value="" class="tableCellFormInputText" type="text">
											<select style="float:left; margin-left:5px;width:100px" name="attributes[0].type" class="tableCellFormInputText">
												<option value="INTEGER">Integer</option>
												<option value="FILE">File</option>
												<option value="STRING">String</option>
												<option value="TEXT">Text</option>
												<option value="FLOAT">Float</option>
												<option value="DATE">Date</option>
											</select> 
											<img  class="dicas" title="New Field" style="cursor:pointer;float:right;margin-right:5px;width:15px;height:15px" src="img/add.png" onclick="addField()">
										</td>
									</tr>


								</table>
							</form>
							<div onclick="doPost()" class="basicButton">Create</div>							
							<div onclick="cancelNewPanel()" class="basicButton">Cancel</div>							
						
						</div>


						<div style="margin : 0 auto; width : 95%; margin-top:10px;"  >
							<table class="tableForm"  id="example" >
								<thead>
									<tr>
										<th>Name</th>
										<th>Description</th>
										<th>File Field Domains</th>
										<th>&nbsp;</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="table" items="${tables}">
										<tr>
											<td>${table.name}</td>
											<td>${table.description}</td>
											<td>&nbsp;<c:forEach var="domain" items="${table.domains}">${domain.domainName}<br></c:forEach></td>
											<td>
												<img class="miniButton dicas" title="Delete" onclick="deleteTable('${table.idTable}','${table.name}')" src="img/delete.png">
												<img class="miniButton dicas" title="View Data and Schema" onclick="showTable('${table.name}')" src="img/search.png">
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>

					</div>												
					
				</div>
				<div id="rightBox"> 
					<%@ include file="commonpanel.jsp" %>
				</div>
				
				
<script>

	var attrFieldCount = 1;

	function doPost() {
		
		var tableName = $("#tableName").val();
		var tableDescription = $("#tableDescription").val();
		var firstField = $("#firstField").val();
		if ( tableName.indexOf(' ') >= 0 ) {
			$("#tableName").focus();
			showMessageBox("The table name cannot have white spaces.");
			return false;
		}		

		if ( tableName.length == 0 ) {
			$("#tableName").focus();
			showMessageBox("You must provide a table name.");
			return false;
		}		

		if ( firstField.length == 0 ) {
			$("#firstField").focus();
			showMessageBox("You must provide at least one field for this table.");
			return false;
		}		

		if ( tableDescription.length == 0 ) {
			$("#tableDescription").focus();
			showMessageBox("You must provide a table description.");
			return false;
		}		
		
		$("#formPost").submit();
	}

	function cancelNewPanel() {
		$("#newPannel").css("display","none");
	}
	
	
	function activity(idWf) {
		window.location.href="actManager?idWorkflow=" + idWf;
	}
	
	function showNewPannel() {
		$("#newPannel").css("display","table");
	}

	function deleteTable(idTable, tableName) {
		showDialogBox( "The table "+tableName+" will be deleted. Are you sure?", "deleteTable?idTable=" + idTable );
	}

	function removeField( field ) {
		$("#" + field).remove();
		attrFieldCount--;
		var previous = attrFieldCount-1;
		$("#removeBtn" + previous).toggle();
		reloadDicas();
	}
	
	function addField() {
		var fieldText = '<tr id="attrFld'+attrFieldCount+'"><td class="tableCellFormLeft">Field</td><td class="tableCellFormRight">' + 
				'<input style="float:left;width:100px" name="attributes['+attrFieldCount+'].name" value="" class="tableCellFormInputText" type="text">'+
				' <select style="float:left; margin-left:5px;width:100px" name="attributes['+attrFieldCount+'].type" class="tableCellFormInputText">'+
				'<option value="INTEGER">Integer</option><option value="FILE">File</option><option value="STRING">String</option><option value="TEXT">Text</option>'+
				'<option value="FLOAT">Float</option><option value="DATE">Date</option></select>'+
				'<img id="removeBtn'+attrFieldCount+'" class="dicas" title="Remove Field" style="cursor:pointer;float:right;margin-right:5px;width:15px;height:15px" src="img/delete.png" onclick="removeField(\'attrFld'+attrFieldCount+'\')">' +
				'</td></tr>';
		var previous = attrFieldCount-1;
		$("#removeBtn" + previous).toggle();
		$('#tblAddRel tr:last').after(fieldText);
		attrFieldCount++;
		reloadDicas();
	}
	
	
	
	$(document).ready(function() {
		$('#example').dataTable({
	        "oLanguage": {
	            "sUrl": "js/pt_BR.txt"
	        },	
	        "iDisplayLength" : 10,
			"bLengthChange": false,
			"fnInitComplete": function(oSettings, json) {
				doTableComplete();
			},
			"bAutoWidth": false,
			"sPaginationType": "full_numbers",
			"aoColumns": [ 
						  { "sWidth": "20%" },
						  { "sWidth": "40%" },
						  { "sWidth": "25%" },
						  { "sWidth": "15%" }]						
		} ).fnSort( [[0,'desc']] );
	} );	
	
	
	function showTable( tblName ) {
		window.location.href="viewSql?tableName=" + tblName;
	}

</script>				
				
<%@ include file="../../footer.jsp" %>
				