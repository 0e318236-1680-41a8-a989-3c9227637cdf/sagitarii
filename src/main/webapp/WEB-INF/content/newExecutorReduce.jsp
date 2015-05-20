<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ include file="../../header.jsp" %>


				<div id="leftBox"> 
				
					<div id="bcbMainButtons" class="basicCentralPanelBar" style="height:50px">
						<%@ include file="buttons.jsp" %>
					</div>
				
					<div id="basicCentralPanel">
					
						<div class="basicCentralPanelBar">
							<img alt="" src="img/table.png" />
							<div class="basicCentralPanelBarText">Create new Executor REDUCE</div>
						</div>
						
						<div class="menuBarMain" style="position:relative">
							<img alt="" onclick="back();" title="Cancel" class="button dicas" src="img/back.png" />
							<img alt="" onclick="save();" title="Save Executor" class="button dicas" src="img/save.png" />
						</div>

						<form method="post" action="saveExecutor" id="frmSave" enctype="multipart/form-data">
							<input type="hidden" id="stmtField" name="executor.selectStatement">
							<input type="hidden" name="executorTarget" value="REDUCE">

							<div id="typeMapPanel" class="menuBarMain" style="display:table;height:52px;margin-top:5px;">
								<table style="margin-top:10px;width:450px;margin-left:10px" >
									<tr>
										<td style="width:25%">Alias</td>
										<td><input maxlength="20" size="22" id="executorAlias" name="executorAlias" style="width:98%" class="tableCellFormInput" type="text" ></td>
									</tr>	
									<tr>
										<td style="width:25%">Wrapper Application</td>
										<td>
											<input id="wrapperFile" name="wrapperFile" style="width:98%" class="tableCellFormInput" type="file" >
										</td>
									</tr>	
								</table>							
							</div>
						</form>
						
						<div id="typeSelectPanel" class="menuBarMain" style="display:table;height:150px;margin-top:5px;font-size:11px !important;">
							<table style="margin-top:10px;width:450px;margin-left:10px" >
								<tr>
									<td>Grouping Attributes (comma separated attributes)</td>
								</tr>	
							</table>							
							
							<div class="menuBarMain" style="height:100px;margin-top:5px;font-size:11px !important;">
								<textarea style="border:0px;height:140px" id="code" name="code"></textarea>
							</div>
							
						</div>

						<div style="margin : 0 auto; height:910px; width : 95%; margin-top:10px;" id="dtTableContainer">
							<table class="tableForm"  id="example" >
								<thead>
									<tr>
										<th>Name</th>
										<th>Description</th>
										<th>&nbsp;</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="table" items="${customTables}">
										<tr>
											<td>${table.name}</td>
											<td>${table.description}</td>
											<td>
												<img class="miniButton dicas" title="View Schema" onclick="showTable('${table.name}')" src="img/search.png">
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
					
					<div id="tblIds" class="userBoard" style="padding-bottom:5px;">
						<div class="userBoardT1" style="text-align:center;width:95%">System ID Tags</div>
						<div class="userBoardT2" style="text-align:center;width:95%">
							<table>
								<tr><td>Workflow ID</td><td>%ID_WFL%</td></tr>
								<tr><td>Experiment ID</td><td>%ID_EXP%</td></tr>
								<tr><td>Activity ID</td><td>%ID_ACT%</td></tr>
								<tr><td>Instance ID</td><td>%ID_PIP%</td></tr>
							</table>
						</div>
					</div>	
					
					<div id="tblCanvas" class="userBoard" style="display:none; padding-bottom:5px;">
						<div class="userBoardT1" style="text-align:center;width:95%">Table "<span id='tabNme'></span>"</div>
						<div id="tableContent" class="userBoardT2" style="text-align:center;width:95%"></div>
					</div>					

				</div>
				
<script>


	$(document).ready(function() {
				
		codeMirrorEditor = CodeMirror.fromTextArea(document.getElementById("code"), { 
			mode: "text/x-sql", 
			indentWithTabs: true,
			smartIndent: true,
			matchBrackets : true,
			readOnly: false,
			lineNumbers: true,
			lineWrapping:true
        });

		
		$('#example').dataTable({
	        "oLanguage": {
	            "sUrl": "js/pt_BR.txt"
	        },	
	        "iDisplayLength" : 5,
			"bLengthChange": false,
			"fnInitComplete": function(oSettings, json) {
				doTableComplete();
			},
			"bAutoWidth": false,
			"sPaginationType": "full_numbers",
			"aoColumns": [ 
						  { "sWidth": "10%" },
						  { "sWidth": "30%" },
						  { "sWidth": "10%" }]						
		} ).fnSort( [[0,'desc']] );		
		
	});
	
	
	function back() {
		window.location.href="viewExecutors";
	}
	

	// Save all
	function save() {
		
		if ( $("#executorAlias").val() == "" ) {
			showMessageBox( "You must provide an alias before save." );
			$("#executorAlias").focus();
			return;
		}
		
		$("#stmtField").val( codeMirrorEditor.getDoc().getValue()  );
		$("#frmSave").submit();
		
	}

	// Show table schema on the right side panel
	function showTable( tblName ) {
		$.ajax({
			type: "GET",
			url: "showtable",
			data: { tableName: tblName }
		}).done(function( table ) {
			$("#tableContent").html( table );
			$("#tblCanvas").css("display","table");
			$("#tabNme").text( tblName );
		});
	}


</script>				
				
<%@ include file="../../footer.jsp" %>
				