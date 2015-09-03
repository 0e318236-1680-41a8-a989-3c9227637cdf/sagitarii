<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ include file="../../header.jsp"%>

<div id="leftBox">
	<div id="bcbMainButtons" class="basicCentralPanelBar"
		style="height: 50px">
		<%@ include file="buttons.jsp"%>
	</div>

	<div id="basicCentralPanel">
		<div class="basicCentralPanelBar">
			<img src="img/node.png">
			<div class="basicCentralPanelBarText">Node ${cluster.macAddress}</div>
		</div>

		<div class="menuBarMain">
			<img onclick="back();" title="Back to Nodes List"
				class="button dicas" src="img/back.png">
		</div>

		<div id="promoBar" style="height: 220px; display: table; width: 100%;margin-top:5px">

			<div id="${cluster.macAddress}" class="clusterBarMax">

				<table style="margin-bottom: 5px; width: 98%; margin-left: 10px; margin-top: 5px">
					<tr>
						<td colspan="10">
							<c:if test="${not fn:contains(cluster.type, 'MAIN')}">
								<img onclick="shutdown('${cluster.macAddress}')" class="dicas"	title="Shutdown this node (no confirmation)" src="img/shutdown.png" style="width: 24px; height: 24px">
								<img onclick="restart('${cluster.macAddress}')" class="dicas"	title="Restart this node (no confirmation)"	src="img/refresh.png" style="width: 24px; height: 24px"> 
								<img onclick="openConsole('${cluster.macAddress}');" title="Open a SSH terminal for this node" class="dicas" src="img/bash.png" style="width: 24px; height: 24px">
							</c:if>
							<img onclick="showNodeLog('${cluster.macAddress}')" class="dicas" title="View Node Activities Log" src="img/search.png"	style="width: 24px; height: 24px"> 
						</td>
					</tr>
				
					<tr>
						<th style='width: 90px'>O.S.</th>
						<th style='width: 110px'>Machine</th>
						<th style='width: 140px'>MAC Address</th>
						<th style='width: 100px'>IP Address</th>
						<th style='width: 60px'>Java</th>
						<th style='width: 100px'>Active</th>
						<th style='width: 80px'>Finished</th>
						<th style='width: 40px'>CPU</th>
						<th style='width: 40px'>Memory</th>
						<th style='width: 40px'>Disk</th>
					</tr>
					<tr>
						<td>${cluster.soName}</td>
						<td>${cluster.machineName}</td>
						<td>${cluster.macAddress}</td>
						<td>${cluster.ipAddress}</td>
						<td>${cluster.javaVersion}</td>
						<td><span class="clusterInfo1">${fn:length(cluster.runningInstances)}</span></td>
						<td>${cluster.processedPipes}</td>
						<td>
							<div title="${cluster.cpuLoad}%" class="clusterCpuOut">
								<div class="clusterCpuIn"
									style="background-color:#0266C8; width:${cluster.cpuLoad}%">&nbsp;</div>
							</div>
						</td>
						<td>
							<div
								title="${cluster.memoryPercent}% of ${cluster.totalMemory}Mb"
								class="clusterCpuOut">
								<div class="clusterCpuIn"
									style="width:${cluster.memoryPercent}%">&nbsp;</div>
							</div>
						</td>
						<td>
							<div
								title="${cluster.diskPercent}% of ${cluster.totalDiskSpace}Mb"
								class="clusterCpuOut">
								<div class="clusterCpuIn"
									style="background-color:#00933B; width:${cluster.diskPercent}%">&nbsp;</div>
							</div>
						</td>
					</tr>

					<tr>
						<th>Last Announce</th>
						<th>Max Allowed Tasks</th>
						<th>Cores</th>
						<th>Status</th>
						<th>Signaled</th>
						<th>Age</th>
						<th colspan="4">Message</th>
					</tr>
					<tr>
						<td>${cluster.lastAnnounce}</td>
						<td>${cluster.maxAllowedTasks}</td>
						<td>${cluster.availableProcessors}</td>
						<td style="color: #F90101">${cluster.status}</td>
						<td style="color: #F90101"><c:if
								test="${cluster.restartSignal == 'TRUE' }">
													RESTART
												</c:if> <c:if test="${cluster.quitSignal == 'TRUE' }">
													QUIT
												</c:if> <c:if test="${cluster.reloadWrappersSignal == 'TRUE' }">
													RELOAD WPRS
												</c:if> <c:if test="${cluster.cleanWorkspaceSignal == 'TRUE' }">
													CLEAN WSPC
												</c:if> &nbsp;</td>
						<td>${cluster.age}&nbsp;</td>
						<td colspan="4" style="color: #F90101">${cluster.lastError}&nbsp;</td>
					</tr>
				</table>

				<c:if test="${not fn:contains(cluster.type, 'MAIN')}">
					<table	style="margin-bottom: 5px; width: 98%; margin-left: 10px; margin-top: 5px">
						<tr>
							<th colspan="3" style="text-align:center">
								CPU load (%) - RAM Usage (%) and Running Tasks (Un)
							</th>
							<th colspan="3" style="text-align:center">
								VM Current Allocated Memory (Mb)
							</th>
						</tr>
						<tr>
							<td colspan="3">
								<img class="dicas" title="CPU load (%) - RAM Usage (%) and Running Tasks (Un)" style="margin:0 auto;width:320px;height:85px" src="metrics/${cluster.macAddress}_load.png"> 
							</td>
							<td colspan="3">
								<img class="dicas" title="VM Current Allocated Memory (Mb)" style="margin:0 auto;width:320px;height:85px" src="metrics/${cluster.macAddress}_ram.png"> 
							</td>
						</tr>
					</table>
				</c:if>

				<c:if test="${not fn:contains(cluster.type, 'MAIN')}">
					<c:if test="${fn:length(cluster.progressListeners) > 0}">
						<table
							style="margin-bottom: 5px; width: 98%; margin-left: 10px; margin-top: 20px">
							<tr>
								<td style="width: 1%"><img
									style="width: 24px; height: 24px;float:left"
									onclick="clearNodeListeners('${cluster.macAddress}')"
									class="dicas miniButton" title="Clear node download info"
									src="img/clean.png"></td>
							</tr>
						</table>

						<table style="margin-bottom: 5px; width: 98%; margin-left: 10px;">
							<tr>
								<th style='width: 100px'>Serial</th>
								<th style='width: 135px'>File</th>
								<th style='width: 135px'>Percent</th>
							</tr>
							<c:forEach var="listener" items="${cluster.progressListeners}">
								<c:if test="${listener.percentage > 0}">
									<tr>
										<td>${listener.serial}&nbsp;</td>
										<td>${listener.fileName}&nbsp;</td>
										<td>${listener.percentage}</td>
									</tr>
								</c:if>
							</c:forEach>
						</table>
					</c:if>
				</c:if>


					<c:if test="${fn:length(cluster.tasks) > 0}">
						<table
							style="margin-bottom: 5px; width: 98%; margin-left: 10px; margin-top: 20px">
							<tr>
								<td style="width: 1%"><img
									style="width: 24px; height: 24px; float:left"
									onclick="clearNodeTasks('${cluster.macAddress}')" class="dicas"
									title="Clear node Tasks" src="img/clean.png"></td>
							</tr>
						</table>
						<table style="margin-bottom: 5px; width: 98%; margin-left: 10px;">
							<tr>
								<th style='width: 100px'>Workflow</th>
								<th style='width: 135px'>Experiment</th>
								<th>Task ID</th>
								<th>Executor</th>
								<th>Start Time</th>
								<th>Elapsed Time</th>
							</tr>
							<c:forEach var="task" items="${cluster.tasks}">
								<tr>
									<td>${task.workflow}</td>
									<td>${task.experiment}</td>
									<td>${task.taskId}</td>
									<td>${task.executor}</td>
									<td>${task.startTime}</td>
									<td>${task.elapsedTime}</td>
								</tr>
							</c:forEach>
						</table>
					</c:if>

					<c:if test="${fn:length(cluster.log) > 0}">
						<table
							style="margin-bottom: 5px; width: 98%; margin-left: 10px; margin-top: 20px">
							<tr>
								<td style="width: 1%"><img
									style="width: 24px; height: 24px;float:left"
									onclick="clearNodeLog('${cluster.macAddress}')" class="dicas"
									title="Clear node Log" src="img/clean.png"></td>
							</tr>
						</table>
						<table style="margin-bottom: 5px; width: 98%; margin-left: 10px;">
							<c:forEach var="line" items="${cluster.log}">
								<tr>
									<td colspan="2">${line}</td>
								</tr>
							</c:forEach>
						</table>
					</c:if>


			</div>

		</div>
	</div>
</div>
<div id="rightBox">

	<%@ include file="commonpanel.jsp"%>

</div>


<script>
	function reloadPage() {
		location.reload();
	}

	function back() {
		window.location.href = "viewClusters";
	}

	function shutdown(mac) {
		window.location.href = "clusterControl?command=quit&mac=" + mac;
	}

	function openConsole(mac) {
		window.location.href = "nodeSSHTerminal?macAddress=" + mac;
	}

	function reloadWrappers() {
		window.location.href = "clusterControl?command=reloadWrappers";
	}

	function restart() {
		window.location.href = "clusterControl?command=restart&mac=" + mac;
	}

	function showNodeLog(node) {
		window.location.href = "showNodeLog?macAddress=" + node;
	}

	function clearNodeLog(node) {
		window.location.href = "clearNodeLog?macAddress=" + node;
	}

	function clearNodeListeners(node) {
		window.location.href = "clearNodeListeners?macAddress=" + node;
	}

	function clearNodeTasks(node) {
		window.location.href = "clearNodeTasks?macAddress=" + node;
	}

	$(document).ready(function() {
		window.setInterval(reloadPage, 5000);
	});
</script>

<%@ include file="../../footer.jsp"%>
