<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../../header.jsp" %>

				<div id="leftBox"> 
					<div id="bcbMainButtons" class="basicCentralPanelBar" style="height:50px">
						<%@ include file="buttons.jsp" %>
					</div>
					
					<div id="basicCentralPanel">

						<div class="basicCentralPanelBar">
							<img src="img/node.png">
							<div class="basicCentralPanelBarText">View Node ${macAddress} Log</div>
						</div>

						<div style="margin : 0 auto; width : 95%; margin-top:10px;" id="dtTableContainer">
							<table class="tableForm"  id="example">
								<thead>
									<tr>
										<th>Time</th>
										<th>Node Task ID</th>
										<th>Executor</th>
										<th>Exit</th>
										<th>Wrapper Console Output</th>
									</tr>
								</thead>
								
								<tbody>	
									<c:forEach var="data" items="${log}">
										<tr>
											<td class="tableCellFormLeft">
												<fmt:formatDate type="both" timeStyle="short" value="${data.time}"/>&nbsp;
											</td>
											<td class="tableCellFormLeft"> ${data.csvDataFile.taskId}</td>
											<td class="tableCellFormLeft"> ${data.activity.executorAlias}</td>
											<td class="tableCellFormLeft"> ${data.csvDataFile.exitCode}</td>
											<td class="tableCellFormRight">
												<c:forEach var="line" items="${data.csvDataFile.console}">
													${line}<br> 
												</c:forEach> 
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
	
	function reloadPage() {
		location.reload();
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
						  { "sWidth": "13%" },
						  { "sWidth": "10%" },
						  { "sWidth": "12%" },
						  { "sWidth": "5%" },
						  { "sWidth": "60%" }]						
		} ).fnSort( [[0,'desc']] );
		
		window.setInterval(reloadPage, 4000);
	});
	
</script>					
				
<%@ include file="../../footer.jsp" %>
				