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
							<div class="basicCentralPanelBarText">Experiments</div>
						</div>

						<div class="menuBarMain" style="position:relative">
							<img alt="" onclick="back();" title="Back" class="button dicas" src="img/back.png" />
						</div>						

						<div style="margin : 0 auto; width : 95%; margin-top:10px;"  >
							<table class="tableForm"  id="example">
								<thead>
									<tr>
										<th>Experiment</th>
										<th>Workflow</th>
										<th>Description</th>
										<th>Finish Date</th>
										<th>Elapsed Time</th>
										<th>Status</th>
										<th>&nbsp;</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="experiment" items="${experiments}">
										<tr>
											<td class="tableCellFormRight">${experiment.tagExec}&nbsp;</td>
											<td class="tableCellFormRight">
												<a class="dicas" title="View Workflow" href="viewWorkflow?idWorkflow=${experiment.workflow.idWorkflow}">${experiment.workflow.tag}</a>
											</td>
											<td class="tableCellFormRight">
												${experiment.description}
											</td>
											<td class="tableCellFormRight">
												<fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" type="both" timeStyle="short" value="${experiment.finishDateTime}"/>&nbsp;
											</td>
											<td class="tableCellFormRight">${experiment.elapsedTime}&nbsp;</td>
											<td class="tableCellFormRight">${experiment.status}&nbsp;</td>
											<td class="tableCellFormRight">
												<c:if test="${experiment.status != 'RUNNING'}">
													<img class="miniButton dicas" title="Delete experiment" onclick="deleteExperiment('${experiment.idExperiment}','-1')"  src="img/delete.png">
												</c:if>
												<c:if test="${experiment.status == 'STOPPED'}">
													<!-- 
														<img class="miniButton dicas" onclick="clone('${experiment.idExperiment}')" title="Clone experiment and data" src="img/clone.png">
													-->
												</c:if>
												<img class="miniButton dicas" title="View More" onclick="view('${experiment.idExperiment}')" src="img/search.png">
												<img class="miniButton dicas" title="Edit Experiment" onclick="activity('${experiment.idExperiment}')" src="img/family3.png">
												<img class="miniButton dicas" title="Edit Custom Queries" onclick="queries('${experiment.idExperiment}')" src="img/sql.png">
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

	function view(idExp) {
		window.location.href="viewExperiment?idExperiment=" + idExp;
	}

	function clone(idExp) {
		window.location.href="cloneExperiment?idExperiment=" + idExp;
	}

	function queries(idExp) {
		window.location.href="viewQueries?idExperiment=" + idExp;
	}
	
	function pause(idExp) {
		window.location.href="pauseExperiment?idExperiment=" + idExp;
	}

	function resume(idExp) {
		window.location.href="resumeExperiment?idExperiment=" + idExp;
	}
	
	function run(idExp) {
		window.location.href="runExperiment?idExperiment=" + idExp;
	}
	
	function activity(idWf) {
		window.location.href="editExperiment?idExperiment=" + idWf;
	}
	
	function deleteExperiment(idExp, idWf) {
		showDialogBox( "This will delete Experiment and all its related data.<br><br>ARE YOU SURE?", "deleteExperiment?idExperiment=" + idExp + "&idWorkflow=" + idWf );
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
						  { "sWidth": "30%" },
						  { "sWidth": "15%" },
						  { "sWidth": "10%" },
						  { "sWidth": "5%" },
						  { "sWidth": "20%" }]						
		} ).fnSort( [[0,'desc']] );
	} );	
	
	function back() {
		window.history.back();
	}	
		
	
</script>				
<%@ include file="../../footer.jsp" %>
				