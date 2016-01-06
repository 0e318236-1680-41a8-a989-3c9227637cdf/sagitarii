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
									</tr>
								</thead>
								<tbody>
									<c:forEach var="session" items="${sessions}">
										<tr>
											
											<td>
												<a href="viewFileTransfersSession?sessionSerial=${session.sessionSerial}">${session.sessionSerial}</a>
												<br>
												<table class="tableForm"  id="tblSavers" style="margin:0px; border:0px; width: 100%;">
													<tr class="saverControl">
														<td style="border:0px;" class="tableCellFormRight">Savers: ${fn:length(session.savers)}</td>
													</tr>
												</table>						
											
												<table class="tableForm"  id="tblImporters" style="margin:0px; border:0px; width: 100%;">
													<c:forEach var="importer" items="${session.importers}">
														<tr class="importerControl" >
															<td style="border:0px;" class="tableCellFormRight">${importer.mainCsvFile.macAddress}&nbsp;</td>
															<td style="border:0px;" class="tableCellFormRight">${importer.mainCsvFile.fileName}&nbsp;</td>
															<td style="border:0px;" class="tableCellFormRight">${importer.importedFiles} / ${importer.totalFiles}</td>
															<td style="border:0px;" class="tableCellFormRight">${importer.importedLines}</td>
															<td style="border:0px;" class="tableCellFormRight">${importer.insertedLines}</td>											
															<td style="border:0px;" class="tableCellFormRight">${importer.mainCsvFile.targetTable}&nbsp;</td>
															<td style="border:0px;" class="tableCellFormRight">${importer.activity} / ${importer.instance} &nbsp;</td>
															<td style="border:0px;" class="tableCellFormRight">
															<fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" type="both" timeStyle="short" value="${importer.startTime}"/></td>
															<td style="border:0px;" class="tableCellFormRight">${importer.log}</td>
															
														</tr>
													</c:forEach>
												</table>						
											
											</td>
											
											<td>
												<img class="miniButton dicas" title="Cancel this session" onclick="stop('${session.sessionSerial}')" style="width:20px;height:20px;" src="img/delete.png">
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
		window.setInterval(reloadPage, 5000);
		
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
				