<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../../header.jsp" %>

				<div id="leftBox"> 
					<div id="bcbMainButtons" class="basicCentralPanelBar" style="height:50px">
						<%@ include file="buttons.jsp" %>
					</div>
					
					<div id="basicCentralPanel">
					
						<div class="basicCentralPanelBar">
							<img src="img/experiment.png">
							<div class="basicCentralPanelBarText">Inspect table "${tableName}"</div>
						</div>

						<div class="menuBarMain" style="position:relative">
							<img alt="" onclick="back();" title="Back" class="button dicas" src="img/back.png" />
						</div>
						
						
						<div id="dtTableContainer" style="width:95%; margin:0 auto;margin-top:10px;margin-bottom:10px">
						
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
			"bFilter": false,
			"sScrollX": "100%",

	        "bProcessing": true,
	        "bServerSide": true,
	        "sAjaxSource" : "SqlAjaxProcess?tableName=${tableName}", 
	        "sServerMethod": "POST",
	        "aoColumns": [
       			<c:forEach var="column" items="${result.toArray()[0].columnNames}">
       			{ "mDataProp": "${column}" },
       			</c:forEach>
	        ],

	        
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

	function back() {
		window.location.href="tablesmanager";
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
				