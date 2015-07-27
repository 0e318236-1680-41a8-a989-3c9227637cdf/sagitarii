<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../../header.jsp" %>

				<div id="leftBox"> 
					<div id="bcbMainButtons" class="basicCentralPanelBar">
						<%@ include file="buttons.jsp" %>
					</div>
					
					<div id="basicCentralPanel">

						<div class="basicCentralPanelBar">
							<img src="img/connection.png">
							<div class="basicCentralPanelBarText">View Instance ${instance.serial}</div>
						</div>
						
						<div class="menuBarMain" style="position:relative">
							<img alt="" onclick="back();" title="Back" class="button dicas" src="img/back.png" />
						</div>

						
						<div id="pannel" style="width:95%; margin:0 auto;margin-top:10px;margin-bottom:20px;">
							<div>
								<table style="width:100%">
									<tr>
										<td class="tableCellFormLeft">Serial Number</td>
										<td class="tableCellFormRight"> 
											${instance.serial}
										</td>
									</tr>
									<tr>
										<td class="tableCellFormLeft">Start</td>
										<td class="tableCellFormRight"> 
											${instance.startDateTime}
										</td>
									</tr>
									<tr>
										<td class="tableCellFormLeft">Finish</td>
										<td class="tableCellFormRight"> 
											${instance.finishDateTime}
										</td>
									</tr>
								</table>
							</div>
						</div>

						<div class="basicCentralPanelBar" style="background-color:#f6f6f6">
							<img src="img/right.png">
							<div class="basicCentralPanelBarText">Source data (consumption)</div>
						</div>
						<div style="margin : 0 auto; width : 95%; margin-top:10px"  >
							<table style="width:99%;" class="tableForm" id="conTable" >
								<thead>
									<tr><c:forEach var="column" items="${consumptions.toArray()[0].columnNames}"><th>${column}</th></c:forEach></tr>
								</thead>
								<tbody>
									<c:forEach var="row" items="${consumptions}"><tr><c:forEach var="value" items="${row.dataValues}"><td>${value}&nbsp;</td></c:forEach></tr></c:forEach>
								</tbody>
							</table>
						</div>


						<div class="basicCentralPanelBar" style="background-color:#f6f6f6;margin-top:50px;">
							<img src="img/right.png">
							<div class="basicCentralPanelBarText">Target data (product)</div>
						</div>
						<div style="margin : 0 auto; width : 95%; margin-top:10px;"  >
							<table style="width:99%;" class="tableForm" id="example" >
								<thead>
									<tr><c:forEach var="column" items="${products.toArray()[0].columnNames}"><th>${column}</th></c:forEach></tr>
								</thead>
								<tbody>
									<c:forEach var="row" items="${products}"><tr><c:forEach var="value" items="${row.dataValues}"><td>${value}</td></c:forEach></tr></c:forEach>
								</tbody>
							</table>
						</div>

					</div>												
					
				</div>
				<div id="rightBox"> 
					<%@ include file="commonpanel.jsp" %>
				</div>
		
<script>

$(document).ready(function() {

	$('#conTable').dataTable({
        "oLanguage": {
            "sUrl": "js/pt_BR.txt"
        },	
        "bLengthChange": false,
		"fnInitComplete": function(oSettings, json) {
			doComplete();
		},
		"sPaginationType": "full_numbers",	
		"iDisplayLength" : 20,	
		"bAutoWidth" : false,
		"sScrollX": "100%",
		"bScrollCollapse": true
	});	

	
	$('#example').dataTable({
        "oLanguage": {
            "sUrl": "js/pt_BR.txt"
        },	
        "bLengthChange": false,
		"fnInitComplete": function(oSettings, json) {
			doComplete();
		},
		"sPaginationType": "full_numbers",	
		"iDisplayLength" : 20,	
		"bAutoWidth" : false,
		"sScrollX": "100%",
		"bScrollCollapse": true
	});	
	
});		

function doComplete() {
	doTableComplete();
	
	$(".dataTable").each(function() {
		$(this).css("padding-top","0px");
		$(this).css("margin-bottom","0px");
	});
	$(".dataTables_scroll").css("margin-bottom","10px");
	$(".dataTables_scroll").css("padding-top","10px");
	
}


function viewData(idExp,tbl) {
	window.location.href="viewTableData?idExperiment=" + idExp + "&tableName=" + tbl;
}

function back() {
	window.history.back();
}

</script>
				
<%@ include file="../../footer.jsp" %>
				