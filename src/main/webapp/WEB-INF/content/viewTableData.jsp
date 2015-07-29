<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../../header.jsp" %>

				<div id="leftBox"> 
					<div id="bcbMainButtons" class="basicCentralPanelBar">
						<%@ include file="buttons.jsp" %>
					</div>
					
					<div id="basicCentralPanel">
					
						<div class="basicCentralPanelBar">
							<img src="img/experiment.png">
							<div class="basicCentralPanelBarText">Inspect table "${tableName}" for Experiment ${experiment.tagExec}</div>
						</div>
						
						<div class="menuBarMain">
							<img onclick="back();" title="Back"	class="button dicas" src="img/back.png">
						</div>
						
						<form method="post" action="inspectExperiment" id="frmSave">
							<input type="hidden" id="sqlQuery" name="sql">
							<input type="hidden" name="idExperiment" value="${experiment.idExperiment}">
						</form>						
						
						<div id="pannel" style="width:95%; margin:0 auto;margin-top:10px;margin-bottom:5px;display:table;">
							<div style="height:60px;">
								<table style="width:100%">
									<tr>
										<td class="tableCellFormLeft">Experiment</td>
										<td class="tableCellFormRight"> 
											${experiment.tagExec} 
										</td>
										<td class="tableCellFormLeft">Status</td>
										<td style="color:#F90101" class="tableCellFormRight"> 
											${experiment.status} 
										</td>
									</tr>
	
									<tr>
										<td class="tableCellFormLeft">Workflow</td>
										<td class="tableCellFormRight"> 
											${experiment.workflow.tag} 
										</td>
										<td class="tableCellFormLeft">Fragments</td>
										<td class="tableCellFormRight"> 
											${fn:length(experiment.fragments)}
										</td>
									</tr>
	
									<tr>
										<td class="tableCellFormLeft">Start Date/Time</td>
										<td class="tableCellFormRight">
											<fmt:formatDate type="both" timeStyle="short" value="${experiment.lastExecutionDate}" />&nbsp;
										</td>
										<td class="tableCellFormLeft">Finish Date/Time</td>
										<td class="tableCellFormRight">
											<fmt:formatDate type="both" timeStyle="short" value="${experiment.finishDateTime}" />&nbsp;
										</td>
									</tr>
	
									<tr>
										<td class="tableCellFormLeft">Description</td>
										<td class="tableCellFormRight" colspan="3"> 
											${experiment.workflow.description} 
										</td>
									</tr>
								</table>
							</div>
						</div>


						<div class="basicCentralPanelBar">
							<img src="img/right.png">
							<div class="basicCentralPanelBarText">SQL Result</div>
						</div>
						
						<div   style="width:95%; margin:0 auto;margin-top:10px;margin-bottom:10px">
						
							<table style="width:99%" class="tableForm" id="example" >
								<thead>
								<c:forEach var="column" items="${result.toArray()[0].columnNames}">
									<th>${column}</th>
								</c:forEach>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
						
					</div>												
					
					
				</div>
				<div id="rightBox"> 
					<%@ include file="commonpanel.jsp" %>
					<div id="tblCanvas" class="userBoard" style="display:none; padding-bottom:5px;">
						<div class="userBoardT1" style="text-align:center;width:95%">Table "<span id='tabNme'></span>"</div>
						<div id="tableContent" class="userBoardT2" style="text-align:center;width:95%"></div>
					</div>					
					
				</div>
				
				
<script>

	function decorateRow(row) {
	    $(row).children().each(function(index, td){
	       var data = $(td).text();
	       var decodedData = window.atob( data );
	       $(td).html( decodedData );
	    });
	}

	$(document).ready(function() {
		showTable('${tableName}');
		
		$('#example').dataTable({
	        "oLanguage": {
	            "sUrl": "js/pt_BR.txt"
	        },	
			
	        "bLengthChange": false,
			"sPaginationType": "full_numbers",	
			"iDisplayLength" : 30,	
			"bAutoWidth" : false,
			"sScrollX": "100%",
	        "bProcessing": true,
	        "bServerSide": true,
	        "sAjaxSource" : "tableDataAjaxProcess?idExperiment=${experiment.idExperiment}&tableName=${tableName}", 
	        "sServerMethod": "POST",
	        "aoColumns": [
       			<c:forEach var="column" items="${result.toArray()[0].columnNames}">
       			{ "mDataProp": "${column}" },
       			</c:forEach>
	        ],

	        "fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
	            decorateRow( nRow );
                return nRow;
	        },
	        
	        "fnServerData": function ( sSource, aoData, fnCallback, oSettings ) {
	        	<c:forEach var="column" items="${result.toArray()[0].columnNames}">
		        	aoData.push( { "name": "columns", "value": "${column}" } );
	        	</c:forEach>
	        	oSettings.jqXHR = $.ajax({
	              "dataType": 'json',
	              "type": "POST",
	              "url": sSource,
	              "data": aoData,
	              "success": fnCallback
	         	}); 
	         },
	         
	        "fnInitComplete": function(oSettings, json) {
	        	doComplete();
	        }
		});	
		
	});
	
	function doComplete() {
		doTableComplete();
		$("#example_wrapper .dataTable").each(function() {
			$(this).css("padding-top","0px");
			$(this).css("margin-bottom","0px");
		});
		$(".dataTables_scroll").css("margin-bottom","10px");
		$(".dataTables_scroll").css("padding-top","10px");
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
	
	function back() {
		window.location.href="inspectExperiment?idExperiment=${experiment.idExperiment}";
	}	
	
</script>				
				
<%@ include file="../../footer.jsp" %>
				