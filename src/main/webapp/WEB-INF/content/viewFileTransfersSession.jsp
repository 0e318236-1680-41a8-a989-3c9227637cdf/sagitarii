<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../../header.jsp" %>

				<div id="leftBox"> 
					<div id="bcbMainButtons" class="basicCentralPanelBar" style="height:50px">
						<%@ include file="buttons.jsp" %>
					</div>
					
					<div id="basicCentralPanel">
					
						<div class="basicCentralPanelBar">
							<img src="img/filetransfer.png">
							<div class="basicCentralPanelBarText">File Transfer Session ${sessionSerial} Status</div>
						</div>
						
						<div style="margin : 0 auto 50px; width : 100%; margin-top:10px;height:40px;"  >
							<table class="tableForm"  id="example">
								<thead>
									<tr>
										<th>Status</th>
										<th>File Name</th>
										<th>Start Time</th>
										<th>Experiment</th>
										<th>Transferred</th>
										<th>Total</th>
										<th style="width:25%">%</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="saver" items="${savers}">
										<tr id="TR_${saver.tag}" class="saverControl">
											<td class="tableCellFormRight">${saver.status}</td>
											<td class="tableCellFormRight">${saver.fileName}</td>
											<td class="tableCellFormRight"><fmt:formatDate type="both" timeStyle="short" value="${saver.startDateTime}"/></td>
											<td class="tableCellFormRight">${saver.experimentSerial}</td>
											<td id="TB_${saver.tag}" class="tableCellFormRight">${saver.bytes}</td>
											<td id="TT_${saver.tag}" class="tableCellFormRight">${saver.totalBytes}</td>
											<td class="tableCellFormRight">
												<div title="Transfer Progress" style="width:99%" class="clusterCpuOut dicas">
													<div id="B_${saver.tag}" class="clusterCpuIn" style="background-color:#0266C8;width:${saver.percent}%; "></div>
												</div> 
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>						
						</div>


						<div class="basicCentralPanelBar">
							<img src="img/import.png">
							<div class="basicCentralPanelBarText">Data Importer for this session</div>
						</div>
						
						<div style="margin : 0 auto 50px; width : 100%; margin-top:10px;height:40px;"  >
							<table class="tableForm"  id="tblImporters">
								<thead>
									<tr>
										<th style="width:90px">Node</th>
										<th>CSV File</th>
										<th>Total Files</th>
										<th>Total Lines</th>
										<th>Inserted Lines</th>
										<th>% Inserted</th>
										<th>Target Table</th>
										<th>Owner Activity</th>
										<th>Start Time</th>
										<th style="width:15%">Log</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="importer" items="${importers}">
										<tr class="importerControl" id="TR_${importer.tag}">
											<td class="tableCellFormRight">${importer.mainCsvFile.macAddress}&nbsp;</td>
											<td class="tableCellFormRight">${importer.mainCsvFile.fileName}&nbsp;</td>
											<td class="tableCellFormRight">${importer.totalFiles}</td>
											<td class="tableCellFormRight">${importer.importedLines}</td>
											<td id="TT_${importer.tag}" class="tableCellFormRight">${importer.insertedLines}</td>											
											<td class="tableCellFormRight">
												<div id="A_${importer.tag}" title="${importer.insertedLines} of ${importer.importedLines} (${importer.percent}%)" class="clusterCpuOut dicas">
													<div id="B_${importer.tag}" class="clusterCpuIn" style="background-color:#00933B;width:${importer.percent}%; "></div>
												</div> 
											</td>
											<td class="tableCellFormRight">${importer.mainCsvFile.targetTable}&nbsp;</td>
											<td class="tableCellFormRight">${importer.mainCsvFile.activity}&nbsp;</td>
											<td class="tableCellFormRight">
											<fmt:formatDate type="both" timeStyle="short" value="${importer.startTime}"/></td>
											<td class="tableCellFormRight">${importer.log}</td>
											
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


	function removeLine( tag ) {
		var toSearch = "#TR_" + tag;
		$( toSearch ).remove();
	}

	
	function updateSaversBar() {
		$.ajax({
			type: "GET",
			url: "getPercent?sessionSerial=${sessionSerial}&type=savers",
		}).done(function( data ) {
			console.log("received");
			var quant = 0;
			for(var x in data){
				var tag = data[x].tag;
				var percent = data[x].percent;
				var total = data[x].total;
				var done = data[x].done;
				$("#B_" + tag).css("width", percent + "%");
				$("#TB_" + tag).text(done);
				$("#TT_" + tag).text(total);
				if( percent == 100 ) {
					removeLine( tag );
				} else {
					quant++;	
				}
				
			}
			updateImportersBar( quant );
		});		
	}
	
	function updateImportersBar( quantOfSavers ) {
		$.ajax({
			type: "GET",
			url: "getPercent?sessionSerial=${sessionSerial}&type=importers",
		}).done(function( data ) {
			var quantOfImporters = 0;
			for(var x in data){
				var tag = data[x].tag;
				var percent = data[x].percent;
				var total = data[x].total;
				var done = data[x].done;
				var targetTable = data[x].targetTable;
				var status = data[x].status;
				$("#B_" + tag).css("width", percent + "%");
				$("#A_" + tag).prop("title", done + " of " + total + " (" +percent+ "%)");
				$("#TT_" + tag).text(done);
				if( status == "DONE" ) {
					removeLine( tag );
				} else {
					quantOfImporters++;
				}
				reloadDicas();
			}
			
			if ( (quantOfSavers > 0) && ( $(".saverControl").length == 0 ) ) {
				location.reload();
			}

			if ( (quantOfImporters > 0) && ( $(".importerControl").length == 0 ) ) {
				location.reload();
			}

			if ( quantOfImporters == 0 ) {
				$(".importerControl").remove();
			}

			if ( quantOfSavers == 0 ) {
				$(".saverControl").remove();
			}
			
			if ( targetTable == "" ) {
				location.reload();
			}
			
		});		
	}
	
	
	function reloadPage() {
		updateSaversBar();
	}

	
	$(document).ready(function() {
		window.setInterval(reloadPage, 2000);
	});

</script>
				
<%@ include file="../../footer.jsp" %>
				