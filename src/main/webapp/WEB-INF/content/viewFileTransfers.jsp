<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../../header.jsp" %>

				<div id="leftBox"> 
					<div id="bcbMainButtons" class="basicCentralPanelBar">
						<%@ include file="buttons.jsp" %>
					</div>
					
					<div id="basicCentralPanel">
					
						<div class="basicCentralPanelBar">
							<img src="img/filetransfer.png">
							<div class="basicCentralPanelBarText">File Transfer Sessions</div>
						</div>
						
						<div class="menuBarMain" style="position:relative">
							<img alt="" onclick="back();" title="Back" class="button dicas" src="img/back.png" />
						</div>	
						
						<div style="margin : 0 auto; width : 95%; margin-top:10px;"  >
							<table class="tableForm"  id="example">
								<thead>
									<tr>
										<th>Session</th>
										<th>&nbsp;</th>
										<th>&nbsp;</th>
										<th>&nbsp;</th>
										<th>&nbsp;</th>
										<th>&nbsp;</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="session" items="${sessions}">
										<tr >
											<td style="background-color:#F6F6F6;border:0px;width:20%" class="tableCellFormRight"><b>${session.sessionSerial}</b></td>
											<td style="background-color:#F6F6F6;border:0px;width:20%" class="tableCellFormRight">${session.importer.mainCsvFile.macAddress}&nbsp;</td>
											<td style="background-color:#F6F6F6;border:0px;width:15%" class="tableCellFormRight">${session.importer.mainCsvFile.fileName}&nbsp;</td>
											<td style="background-color:#F6F6F6;border:0px;width:10%" class="tableCellFormRight">${session.importer.mainCsvFile.targetTable}&nbsp;</td>
											<td colspan="2" style="background-color:#F6F6F6;border:0px;width:30%" class="tableCellFormRight">${session.importer.log}&nbsp;</td>
										</tr>
										<tr>											
											<td style="border:0px;border-bottom: 1px dotted #ADADAD;" class="tableCellFormRight">${session.importer.activity} / ${session.importer.instance} &nbsp;</td>
											<td style="border:0px;border-bottom: 1px dotted #ADADAD;" class="tableCellFormRight">${session.importer.importedLines} / ${session.importer.insertedLines}</td>
											<td style="border:0px;border-bottom: 1px dotted #ADADAD;" class="tableCellFormRight">
												<fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" type="both" timeStyle="short" value="${session.importer.startTime}"/>
											</td>
											<td style="border:0px;border-bottom: 1px dotted #ADADAD;" class="tableCellFormRight">${session.importer.hostAddress}</td>
											<td style="border:0px;border-bottom: 1px dotted #ADADAD;" class="tableCellFormRight">${session.importer.clientState}</td>
											<td style="border:0px;border-bottom: 1px dotted #ADADAD;" class="tableCellFormRight">
												<div style="width:100%"
													title="Completion: ${session.importer.clientCompletion}%"
													class="clusterCpuOut dicas">
													<div class="clusterCpuIn"
														style="width:${session.importer.clientCompletion}%">&nbsp;</div>
												</div>															
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
						  { "sWidth": "100%" }
						 ]						
		} ).fnSort( [[0,'desc']] );
		
		
	});
	
	function stop( session ) {
		$.ajax({
			type: "GET",
			url: "stopTransferSession?sessionSerial=" + session,
		}).done(function( data ) {
			// show result to user!!
		});
	}

	function back() {
		window.history.back();
	}		
</script>
				
<%@ include file="../../footer.jsp" %>
				