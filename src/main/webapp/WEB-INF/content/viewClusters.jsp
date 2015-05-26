<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ include file="../../header.jsp" %>

				<div id="leftBox"> 
					<div id="bcbMainButtons" class="basicCentralPanelBar" style="height:50px">
						<%@ include file="buttons.jsp" %>
					</div>


				<div id="pageId" style="display:none">index</div>
					<div class="basicCentralPanelBar" style="height:50px">
						<img onclick="clean();" title="Clean all workspaces" class="menuButton dicas" src="img/clean.png">
						<img onclick="reloadWrappers();" title="Force nodes to reload all wrappers" class="menuButton dicas" src="img/refresh.png">
					</div>
					
					<div id="promoBar" style="height:220px;display:table;width:100%">
						<c:forEach var="cluster" items="${clusterList}">
						
								<div onclick="showNodeLog('${cluster.macAddress}')" style="position:relative" id="${cluster.macAddress}" class="clusterBar">
								
									<table style="margin-bottom: 5px;width:98%; margin-left:10px; margin-top: 5px">
										<c:if test="${not fn:contains(cluster.macAddress, 'S0-A0-G0-I0-T0-A0-RI')}">
											<tr>
												<td colspan="9" >
													<img onclick="shutdown('${cluster.macAddress}')" class="dicas" title="Shutdown this node (no confirmation)" src="img/shutdown.png" style="width:24px;height:24px">
													<img onclick="restart('${cluster.macAddress}')" class="dicas" title="Restart this node (no confirmation)" src="img/refresh.png" style="width:24px;height:24px">
												</td>
											</tr>
										</c:if>
										<tr >
											<th style='width:100px'>Operational System</th>
											<th style='width:130px'>Machine Name</th>
											<th style='width:130px'>MAC Address / Serial</th>
											<th style='width:100px'>IP Address</th>
											<th style='width:60px'>Java</th>
											<th style='width:100px'>Active Tasks</th>
											<th style='width:80px'>Finished Tasks</th>
											<th style='width:80px'>CPU Load</th>
											<th style='width:80px'>Memory Use (VM)</th>
										</tr>
										<tr>
											<td>${cluster.soName}</td>
											<td>${cluster.machineName}</td>
											<td >${cluster.macAddress}</td>
											<td>${cluster.ipAddress}</td>
											<td>${cluster.javaVersion}</td>
											<td><span class="clusterInfo1">${fn:length(cluster.runningInstances)}</span></td>
											<td>${cluster.processedPipes}</td>
											<td>
												<div title="${cluster.cpuLoad}%" class="clusterCpuOut">
													<div class="clusterCpuIn" style="background-color:#0266C8; width:${cluster.cpuLoad}%">&nbsp;</div>
												</div> 
											</td>
											<td>
												<div title="${cluster.memoryPercent}% of ${cluster.totalMemory}Mb" class="clusterCpuOut">
													<div class="clusterCpuIn" style="width:${cluster.memoryPercent}%">&nbsp;</div>
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
											<th colspan="3">Last Error</th>
										</tr>
										<tr>
											<td>${cluster.lastAnnounce}</td>
											<td>${cluster.maxAllowedTasks}</td>
											<td>${cluster.availableProcessors}</td>
											<td style="color:#F90101">${cluster.status}</td>
											<td style="color:#F90101">
											
												<c:if test="${cluster.restartSignal == 'TRUE' }">
													RESTART
												</c:if>
												<c:if test="${cluster.quitSignal == 'TRUE' }">
													QUIT
												</c:if>
												<c:if test="${cluster.reloadWrappersSignal == 'TRUE' }">
													RELOAD WPRS
												</c:if>
												<c:if test="${cluster.cleanWorkspaceSignal == 'TRUE' }">
													CLEAN WSPC
												</c:if>
												&nbsp;
											
											</td>
											<td>${cluster.age}&nbsp;</td>
											<td colspan="3" style="color:#F90101">${cluster.lastError}&nbsp;</td>
										</tr>
										
										
									</table>
									
								</div>
						
						</c:forEach>

						
						
					</div>										
					
				</div>
				<div id="rightBox"> 

					<%@ include file="commonpanel.jsp" %>
					
				</div>
				
				
<script>
	
	function reloadPage() {
		location.reload();
	}

	function restart(mac) {
		window.location.href="clusterControl?command=restart&mac=" + mac;
	}

	function clean() {
		window.location.href="cleanWorkspaces";
	}

	function shutdown(mac) {
		window.location.href="clusterControl?command=quit&mac=" + mac;
	}

	function reloadWrappers() {
		window.location.href="clusterControl?command=reloadWrappers";
	}

	function showNodeLog( node ) {
		window.location.href="showNodeLog?macAddress=" + node;
	}

	$(document).ready(function() {
		window.setInterval(reloadPage, 5000);
	});

</script>				
				
<%@ include file="../../footer.jsp" %>
				