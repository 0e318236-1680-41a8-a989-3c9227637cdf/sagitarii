<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../../header.jsp" %>

				<div id="leftBox"> 
					<div id="bcbMainButtons" class="basicCentralPanelBar">
						<%@ include file="buttons.jsp" %>
					</div>
					
					<div id="basicCentralPanel">

						<div class="basicCentralPanelBar">
							<img src="img/table.png">
							<div class="basicCentralPanelBarText">Orchestrator Buffer Status</div>
						</div>
						
						<div class="menuBarMain" style="position:relative">
							<img alt="" onclick="back();" title="Back" class="button dicas" src="img/back.png" />
						</div>
												
						<div style="margin : 0 auto; width : 95%; margin-top:10px;margin-bottom:50px;" >
							<table>
								<tr>
									<th>Instances in output buffer</th>
									<th>SELECT type Instances in output buffer</th>
									<th>Instances with nodes</th>
									<th>Buffers capacity</th>
									<th>&nbsp;</th>
								</tr>
								<tr>
									<td>${fn:length(instanceInputBuffer)}</td>
									<td>${fn:length(instanceJoinInputBuffer)}</td>
									<td>${fn:length(instanceOutputBuffer)}</td>
									<td>${maxBufferCapacity}</td>
								</tr>
							</table>
						</div>


						<div class="basicCentralPanelBar">
							<img src="img/fragment.png">
							<div class="basicCentralPanelBarText">Checkout Register ( Instances Delivered to Nodes )</div>
						</div>
						<div style="margin : 0 auto; width : 95%; margin-top:10px;margin-bottom: 20px;"  >
							<table>
								<tr>
									<th style="width:100px">Serial</th>
									<th style="width:100px">Times Tried</th>
									<th style="width:100px">Node</th>
									<th >Finished</th>
								</tr>
								<c:forEach var="instance" items="${instanceOutputBuffer}">
									<tr>
										<td>${instance.serial}</td>
										<td>${instance.timesTried}</td>
										<td>${instance.executedBy}</td>
										<td>${instance.finishedActivities}&nbsp;</td>
									</tr>
								</c:forEach>
							</table>
						</div>

					
						<div class="basicCentralPanelBar">
							<img src="img/running.png">
							<div class="basicCentralPanelBarText">Experiments Running</div>
						</div>

						<div style="margin : 0 auto; width : 95%; margin-top:10px;"  >
							<table>
								<tr>
									<th style="width:100px">Experiment</th>
									<th style="width:480px">Fragments</th>
									<th style="width:90px">Created</th>
									<th style="width:90px">Started</th>
									<th style="width:40px">Status</th>
									<th style="width:90px">&nbsp;</th>
								</tr>
								<c:forEach var="experiment" items="${runningExperiments}">
									<tr id="${experiment.tagExec}">
										<td class="tableCellFormRight">${experiment.tagExec}&nbsp;</td>
										<td class="tableCellFormRight">
											<table style="width:100%">
												<tr>
													<th style="width:50px">Serial</th>
													<th style="width:100px">Status</th>
													<th style="width:100px">Total</th>
													<th style="width:100px">Remaining</th>
													<th style="width:200px">Activities</th>
												</tr>
												<c:forEach var="fragment" items="${experiment.fragments}">
													<tr>
														<td>${fragment.serial}</td>
														<td>${fragment.status}</td>
														<td>${fragment.totalInstances}</td>
														<td>${fragment.remainingInstances}</td>
														<td>
															<c:forEach var="fragActivity" items="${fragment.activities}">
																${fragActivity.tag}&nbsp;
															</c:forEach>
														</td>
													</tr>
												</c:forEach>
											</table>						
										</td>
										
										
										<td class="tableCellFormRight">
											<fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" type="both" timeStyle="short" value="${experiment.creationDate}"/><br>by ${experiment.owner.loginName}
										</td>
										<td class="tableCellFormRight">
											<fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" type="both" timeStyle="short" value="${experiment.lastExecutionDate}"/>&nbsp;
										</td>
										<td class="tableCellFormRight">${experiment.status}&nbsp;</td>
										<td class="tableCellFormRight">
											<c:if test="${experiment.status == 'RUNNING' }">
												<img class="miniButton dicas" title="Pause"  onclick="pause('${experiment.idExperiment}')" src="img/pause.png">
											</c:if>
											<c:if test="${experiment.status == 'PAUSED' }">
												<img class="miniButton dicas" title="Resume" onclick="resume('${experiment.idExperiment}')" src="img/start.png">
											</c:if>
											<img class="miniButton dicas" title="Inspect" onclick="view('${experiment.idExperiment}')" src="img/search.png">
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
		window.location.href="inspectExperiment?idExperiment=" + idExp;
	}

	function pause(idExp) {
		window.location.href="pauseExperiment?idExperiment=" + idExp;
	}

	
	function restoreFromMemory() {
		window.location.href="instanceBuffer?op=restore";
	}
	
	function saveToMemory() {
		window.location.href="instanceBuffer?op=save";
	}

	function resume(idExp) {
		window.location.href="resumeExperiment?idExperiment=" + idExp;
	}

	function back() {
		window.history.back();
	}	
	
</script>					
				
<%@ include file="../../footer.jsp" %>
				