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
							<div class="basicCentralPanelBarText">Experiments</div>
						</div>

						<div style="margin : 0 auto; width : 95%; margin-top:10px;" id="dtTableContainer">
							<table>
								<tr>
									<th style="width:100px">Experiment</th>
									<th>Workflow</th>
									<th>Created</th>
									<th>Owner</th>
									<th>Last Edit</th>
									<th>Last Run</th>
									<th>Finish Date</th>
									<th>Status</th>
									<th>&nbsp;</th>
									
								</tr>
								<c:forEach var="experiment" items="${experiments}">
									<tr>
										<td class="tableCellFormRight">${experiment.tagExec}&nbsp;</td>
										<td class="tableCellFormRight">
											<a class="dicas" title="View Workflow" href="viewWorkflow?idWorkflow=${experiment.workflow.idWorkflow}">${experiment.workflow.tag}</a>
										</td>
										<td class="tableCellFormRight">
											<fmt:formatDate type="both" timeStyle="short" value="${experiment.creationDate}"/>&nbsp;
										</td>
										<td class="tableCellFormRight">
											${experiment.owner.loginName}
										</td>
										<td class="tableCellFormRight">
											<fmt:formatDate type="both" timeStyle="short" value="${experiment.alterationDate}"/>&nbsp;
										</td>
										<td class="tableCellFormRight">
											<fmt:formatDate type="both" timeStyle="short" value="${experiment.lastExecutionDate}"/>&nbsp;
										</td>
										<td class="tableCellFormRight">
											<fmt:formatDate type="both" timeStyle="short" value="${experiment.finishDateTime}"/>&nbsp;
										</td>
										<td class="tableCellFormRight">${experiment.status}&nbsp;</td>
										<td class="tableCellFormRight">
											<c:if test="${experiment.status != 'RUNNING'}">
												<img class="miniButton dicas" title="Delete experiment" onclick="deleteExperiment('${experiment.idExperiment}','-1')"  src="img/delete.png">
											</c:if>
											<c:if test="${experiment.status == 'STOPPED'}">
												<img class="miniButton dicas" onclick="clone('${experiment.idExperiment}')" title="Clone experiment and data" src="img/clone.png">
											</c:if>
											<img class="miniButton dicas" title="View More" onclick="view('${experiment.idExperiment}')" src="img/search.png">
											<img class="miniButton dicas" title="Edit Experiment" onclick="activity('${experiment.idExperiment}')" src="img/family3.png">
											<img class="miniButton dicas" title="Edit Custom Queries" onclick="queries('${experiment.idExperiment}')" src="img/sql.png">
										</td>
									</tr>
								</c:forEach>
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

</script>				
<%@ include file="../../footer.jsp" %>
				