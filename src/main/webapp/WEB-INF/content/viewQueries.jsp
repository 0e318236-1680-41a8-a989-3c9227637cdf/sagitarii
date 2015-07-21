<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../../header.jsp" %>

				<div id="leftBox" > 
				
					<div id="bcbMainButtons" class="basicCentralPanelBar">
						<%@ include file="buttons.jsp" %>
					</div>
					
					<div id="basicCentralPanel">
					
						<div class="basicCentralPanelBar">
							<img src="img/sql.png">
							<div class="basicCentralPanelBarText">Custom Queries</div>
						</div>
				
						<div class="menuBarMain">
							<img onclick="view('${idExperiment}');" title="Back to Experiment" class="button dicas" src="img/experiment.png">
							<img onclick="showNewPannel();" title="New Custom Query" class="button dicas" src="img/add.png">
						</div>
						<div id="newPannel" style="display:none; height:200px; width:95%; margin:0 auto;margin-top:10px;margin-bottom:10px;">
							<form action="doNewQuery" method="POST" id="formPost">
								<input type="hidden" id="codeSql" name="query.query">
								<input type="hidden" name="idExperiment" value="${idExperiment}">
								<table>
									<tr>
										<td class="tableCellFormLeft">Name</td>
										<td class="tableCellFormRight"> 
											<input id="name" name="query.name" class="tableCellFormInputText" type="text"> 
										</td>
									</tr>
									<tr>
										<td class="tableCellFormLeft">SQL</td>
										<td>
											<div class="menuBarMain" style=" width: 100%;height:100px;margin-top:5px;font-size:11px !important;">
												<textarea style="border:0px;height:140px" id="code" name="code"></textarea>
											</div>
										</td>										
									</tr>
									
								</table>
							</form>
							<div onclick="doPost()" class="basicButton">Send</div>							
							<div onclick="cancelNewPanel()" class="basicButton">Cancel</div>							
						</div>

						<div style="margin : 0 auto; width : 95%; margin-top:10px;"  >
							<table class="tableForm"  id="example" >
								<thead>
									<tr>
										<th>Name</th>
										<th>SQL</th>
										<th>&nbsp;</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="query" items="${queries}">
										<tr>
											<td>${query.name}</td>
											<td>${query.query}</td>
											<td>
												<img class="miniButton dicas" title="Delete Query (No confirmation)" onclick="deleteQuery('${query.experiment.idExperiment}','${query.idCustomQuery}')" src="img/delete.png">
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
								<tr><td>Table Name</td><td>%TBL_NME%</td></tr>
							</table>
						</div>
					</div>	
					
				</div>
				
				
<script>

	function doPost() {
		var name = $("#name").val();
		var sql = $("#sql").val();
		if ( (name == '') || ( sql == '' ) ) {
			showMessageBox('Please fill all fields.');
			return;
		} 

		$("#codeSql").val( codeMirrorEditor.getDoc().getValue()  );
		$("#formPost").submit();
	}

	function cancelNewPanel() {
		$("#newPannel").css("display","none");
	}
	
	function showNewPannel() {
		$("#newPannel").css("display","block");
	}

	function view(idExp) {
		window.location.href="viewExperiment?idExperiment=" + idExp;
	}	

	function deleteQuery(idExp, idQuery) {
		window.location.href="deleteQuery?idQuery=" + idQuery + "&idExperiment=" + idExp;
	}	

	
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
	        "iDisplayLength" : 10,
			"bLengthChange": false,
			"fnInitComplete": function(oSettings, json) {
				doTableComplete();
			},
			"bAutoWidth": false,
			"sPaginationType": "full_numbers",
			"aoColumns": [ 
						  { "sWidth": "15%" },
						  { "sWidth": "70%" },
						  { "sWidth": "15%" }
						  ]						
		} ).fnSort( [[0,'desc']] );
	} );	
	
	function save() {
		$("#frmSave").submit();
	}
	
</script>				
				
<%@ include file="../../footer.jsp" %>
				