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
							<div class="basicCentralPanelBarText">Experiment ${experiment.tagExec}</div>
						</div>
						
						<div class="menuBarMain" style="position:relative">
							<img alt="" onclick="back();" title="Back" class="button dicas" src="img/back.png" />
							<img alt="" onclick="viewWf('${experiment.workflow.idWorkflow}')" title="Workflow" class="button dicas" src="img/workflow.png" />
							<img alt="" onclick="queries('${experiment.idExperiment}')" title="Custom Queries" class="button dicas" src="img/sql.png" />

							<c:if test="${experiment.availability == 'FALSE' }">
								<img style="opacity:0.5" alt="" title="Cannot run: Missing source data" class="button dicas" src="img/start.png" />
							</c:if>

							<c:if test="${experiment.availability == 'TRUE' }">
								<c:if test="${experiment.status == 'RUNNING' }">
									<img alt="" onclick="pause('${experiment.idExperiment}')" title="Pause Experiment" class="button dicas" src="img/pause.png" />
								</c:if>
								<c:if test="${experiment.status == 'STOPPED' }">
									<img alt="" onclick="run('${experiment.idExperiment}')" title="Start Experiment" class="button dicas" src="img/start.png" />
								</c:if>
								<c:if test="${experiment.status == 'PAUSED' }">
									<img alt="" onclick="resume('${experiment.idExperiment}')" title="Resume Experiment" class="button dicas" src="img/start.png" />
								</c:if>

								<c:if test="${experiment.status != 'STOPPED' }">
									<img alt="" onclick="inspect('${experiment.idExperiment}')" title="Inspect Experiment Data" class="button dicas" src="img/search.png" />
								</c:if>
								
							</c:if>

						</div>
						
						<div id="pannel" style="width:95%; margin:0 auto;margin-top:10px;margin-bottom:60px;">
							<div style="height:130px;">
								<table style="width:100%">
									<tr>
										<td class="tableCellFormLeft">Label</td>
										<td class="tableCellFormRight"> 
											${experiment.tagExec} 
										</td>
									</tr>
	
									<tr>
										<td class="tableCellFormLeft">Workflow</td>
										<td class="tableCellFormRight"> 
											${experiment.workflow.tag} 
										</td>
									</tr>
	
									<tr>
										<td class="tableCellFormLeft">Description</td>
										<td class="tableCellFormRight"> 
											${experiment.workflow.description} 
										</td>
									</tr>
								
									<tr>
										<td class="tableCellFormLeft">Status</td>
										<td style="color:#F90101" class="tableCellFormRight"> 
											${experiment.status} 
										</td>
									</tr>

									<tr>
										<td class="tableCellFormLeft">Start Date/Time</td>
										<td class="tableCellFormRight">
											<fmt:formatDate type="both" timeStyle="short" value="${experiment.lastExecutionDate}" />&nbsp;
										</td>
									</tr>
									<tr>
										<td class="tableCellFormLeft">Finish Date/Time</td>
										<td class="tableCellFormRight">
											<fmt:formatDate type="both" timeStyle="short" value="${experiment.finishDateTime}" />&nbsp;
										</td>
									</tr>
									<tr>
										<td class="tableCellFormLeft">Elapsed Time</td>
										<td class="tableCellFormRight">${experiment.elapsedTime}</td>
									</tr>

									<tr>
										<td class="tableCellFormLeft">Serial Time</td>
										<td class="tableCellFormRight">${experiment.serialTime}</td>
									</tr>

									<tr>
										<td class="tableCellFormLeft">Speedup Ratio</td>
										<td class="tableCellFormRight">${experiment.speedUp}</td>
									</tr>
									
									<tr>
										<td class="tableCellFormLeft">Parallel Efficiency</td>
										<td class="tableCellFormRight">${experiment.parallelEfficiency}</td>
									</tr>

									<tr>
										<td class="tableCellFormLeft">Fragments</td>
										<td class="tableCellFormRight"> 
											${fn:length(experiment.fragments)}
										</td>
									</tr>
									
									<tr>
										<td class="tableCellFormLeft">Activities</td>
										<td class="tableCellFormRight"> 
											${fn:length(activities)}
										</td>
									</tr>
								</table>
							</div>

						</div>

						<div class="basicCentralPanelBar">
							<img src="img/family3.png">
							<div class="basicCentralPanelBarText">Activities</div>
						</div>

						<div style="margin : 10px auto 20px; width : 95%;" id="dtRelationContainer">
							<table>
								<thead>
									<tr>
										<th style="width:5%">Serial</th>
										<th style="width:20%">Activity</th>
										<th style="width:5%">Type</th>
										<th style="width:10%">Executor Alias</th>
										<th style="width:30%">Consume</th>
										<th style="width:30%">Produce</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="activity" items="${activities}">
										<tr id="${activity.serial}">
											<td class="tableCellFormRight">${activity.serial}&nbsp;</td>
											<td class="tableCellFormRight">${activity.tag}&nbsp;</td>
											<td class="tableCellFormRight">${activity.type}&nbsp;</td>
											<td class="tableCellFormRight">${activity.executorAlias}&nbsp;</td>
											<td class="tableCellFormRight">
												<c:if test="${fn:length(activity.previousActivities) == 0}">
													<c:if test="${activity.inputRelation.isEmpty == 'TRUE' }">
														<span style="color:#F90101">Startup relations <br> 
															<c:forEach var="table" items="${activity.inputRelations}"> 
																<span class="showTable" onclick="showTable('${table.name}')">${table.name}</span> ( no data ) <br>
															</c:forEach>
														
														</span>													
													</c:if>
													<c:if test="${activity.inputRelation.isEmpty == 'FALSE' }">
														<span style="color:#0266C8">Startup relations<br>
															<c:forEach var="table" items="${activity.inputRelations}"> 
																<span class="showTable" onclick="showTable('${table.name}')">${table.name}</span> <br>
															</c:forEach>
														</span>
													</c:if>
												</c:if>
												
												<c:forEach var="dependency" items="${activity.previousActivities}">
													${dependency.tag} ( <span class="showTable" onclick="showTable('${dependency.outputRelation.name}')">${dependency.outputRelation.name}</span> ) <br>
												</c:forEach>&nbsp;
											</td>
											
											<td class="tableCellFormRight">
												<c:if test="${fn:length(activity.nextActivities) == 0}">
													<span style="color:#F90101">Endpoint relation <span class="showTable" onclick="showTable('${activity.outputRelation.name}')">${activity.outputRelation.name}</span></span>
												</c:if>
												<c:if test="${fn:length(activity.nextActivities) > 0}">
													Relation <span class="showTable" onclick="showTable('${activity.outputRelation.name}')">${activity.outputRelation.name}</span> for: <br />
													<c:forEach var="dependency" items="${activity.nextActivities}">
														${dependency.tag}<br>
													</c:forEach>&nbsp;
												</c:if>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>						
						</div>

						<div class="basicCentralPanelBar">
							<img src="img/tables.png">
							<div class="basicCentralPanelBarText">Tables</div>
						</div>
						<div style="margin : 10px auto 20px; width : 95%;" id="dtRelationContainer">
							<table>
								<tr>
									<th style="width:50%">Name</th>
									<th style="width:30%">Description</th>
									<th style="width:10%">&nbsp;</th>
								</tr>
								<c:forEach var="table" items="${experiment.usedTables}">
									<tr>
										<td>${table.name}</td>
										<td>${table.description}&nbsp;</td>
										<td>
											<a href="getTableFull?tableName=${table.name}&idExperiment=${experiment.idExperiment}">
												<img title="Get full table CSV data" class="miniButton dicas" src="img/download.png")">
											</a>
											<a href="getTableSample?tableName=${table.name}">
												<img title="Get table sample CSV data" class="miniButton dicas" src="img/csv.png")">
											</a>
										</td>
									</tr>
								</c:forEach>
							</table>
						</div>
										
						<div class="basicCentralPanelBar">
							<img src="img/fragment.png">
							<div class="basicCentralPanelBarText">Fragments</div>
						</div>

						<div style="margin : 10px auto 20px; width : 95%;" id="dtRelationContainer">
							<table>
								<tr>
									<th style="width:5%">Serial</th>
									<th style="width:5%">Order</th>
									<th style="width:10%">Type</th>
									<th style="width:10%">Status</th>
									<th style="width:10%">Instances</th>
									<th>Activities ( not in order )</th>
								</tr>
								<c:forEach var="fragment" items="${experiment.fragments}">
									<tr>
										<td>${fragment.serial}</td>
										<td>${fragment.indexOrder}</td>
										<td>${fragment.type}</td>
										<td>${fragment.status}</td>
										<td>${fragment.remainingInstances}</td>
										<td>
											<c:forEach var="fragActivity" items="${fragment.activities}">
												<span style="cursor:pointer" onmouseover="higlight('${fragActivity.serial}')" >${fragActivity.tag}</span>&nbsp;
											</c:forEach>
										</td>
									</tr>
								</c:forEach>
							</table>						
						</div>
						
						
						<div class="basicCentralPanelBar">
							<img src="img/sql.png">
							<div class="basicCentralPanelBarText">Custom Queries</div>
						</div>

						<div style="margin : 10px auto 20px; width : 95%;" id="dtRelationContainer">
							<table>
								<tr>
									<th style="width:100px">Name</th>
									<th>SQL</th>
								</tr>
								<c:forEach var="query" items="${queries}">
									<tr>
										<td>${query.name}</td>
										<td>${query.query}</td>
									</tr>
								</c:forEach>
							</table>
						</div>
						

					</div>												
					
				</div>
				<div id="rightBox"> 
					<%@ include file="commonpanel.jsp" %>
					<div id="imageCanvas" class="userBoard" style="width: 240px;height:180px"></div>	
					
					<div id="tblCanvas" class="userBoard" style="display:none; padding-bottom:5px;">
						<div class="userBoardT1" style="text-align:center;width: 225px;">Table "<span id='tabNme'></span>"</div>
						<div id="tableContent" class="userBoardT2" style="text-align:center;width: 225px;"></div>
					</div>					
									
				</div>
				
				
<script>

	function viewImageCanvas() {
		var cyImage = "${experiment.imagePreviewData}";
	    var image = "<img name='compman' style='border-radius:5px;margin:0px;height:180px;width:100%' src='"+cyImage+"' />";
	    $("#imageCanvas").html(image);
	}
	
	$(document).ready(function() {
		viewImageCanvas();		
	});

	function higlight( what ) {
		$("#dtRelationContainer td ").css("background-color","#FFFFFF");
		$("#" + what + " td").css("background-color","#F6F6F6");
	}
	
	function viewWf(idWf) {
		window.location.href="viewWorkflow?idWorkflow=" + idWf;
	}

	function uploadTo(table, experiment) {
		window.location.href="uploadTableData?table=" + table + "&experiment=" + experiment;
	}
	
	function run(idWf) {
		showMessageBox("Please wait. Sagitarii is processing your request. You will be redirected soon...");
		window.location.href="runExperiment?idExperiment=" + idWf;
	}
	
	function inspect(idWf) {
		window.location.href="inspectExperiment?idExperiment=" + idWf;
	}

	function pause(idExp) {
		window.location.href="pauseExperiment?idExperiment=" + idExp;
	}

	function resume(idExp) {
		window.location.href="resumeExperiment?idExperiment=" + idExp;
	}
	
	function queries(idExp) {
		window.location.href="viewQueries?idExperiment=" + idExp;
	}
	
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
		window.history.back();
	}	

</script>				
				
<%@ include file="../../footer.jsp" %>
				