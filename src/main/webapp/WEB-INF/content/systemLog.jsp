<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../../header.jsp" %>

				<div id="leftBox"> 
					<div id="bcbMainButtons" class="basicCentralPanelBar">
						<%@ include file="buttons.jsp" %>
					</div>
					
					<div id="basicCentralPanel">

						<div class="basicCentralPanelBar">
							<img src="img/node.png">
							<div class="basicCentralPanelBarText">View Node Log</div>
						</div>
						
						<div class="menuBarMain">
							<img onclick="back();" title="Back"	class="button dicas" src="img/back.png">
						</div>
						

						<div style="margin : 0 auto; width : 95%; margin-top:10px;"  >
							<table class="tableForm"  id="example">
								<thead>
									<tr>
										<th>Time</th>
										<th>Type</th>
										<th>Node</th>
										<th>Action</th>
									</tr>
								</thead>
								
								<tbody>	
									<c:forEach var="data" items="${log}">
										<tr>
											<td class="tableCellFormLeft" style="font-weight:normal">
												<fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" type="both" timeStyle="short" value="${data.dateTime}"/>&nbsp;
											</td>
											<td class="tableCellFormLeft" style="font-weight:normal">${data.type}</td>
											<td class="tableCellFormLeft" style="font-weight:normal">${data.node}</td>
											<td class="tableCellFormLeft" style="font-weight:normal">${data.log}</td>
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
	
	function back() {
		window.location.href="showNodeDetails?macAddress=${macAddress}";
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
						  { "sWidth": "10%" },
						  { "sWidth": "10%" },
						  { "sWidth": "15%" },					
						  { "sWidth": "65%" }]						
		} ).fnSort( [[0,'desc']] );
		
		window.setInterval(reloadPage, 4000);
	});
	
</script>					
				
<%@ include file="../../footer.jsp" %>
				