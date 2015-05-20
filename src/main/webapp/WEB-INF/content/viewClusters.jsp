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
						
								<div style="position:relative" id="${cluster.macAddress}" class="clusterBar">
								
									<table style="margin-bottom: 5px;width:98%; margin-left:10px; margin-top: 5px">
										<c:if test="${not fn:contains(cluster.macAddress, 'S0-A0-G0-I0-T0-A0-R0')}">
											<tr>
												<td colspan="8" >
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
											<th style='width:100px'>Finished Tasks</th>
											<th style='width:130px'>Cluster CPU Load</th>
										</tr>
										<tr>
											<td>${cluster.soName}</td>
											<td>${cluster.machineName}</td>
											<td >${cluster.macAddress}</td>
											<td>${cluster.ipAddress}</td>
											<td>${cluster.javaVersion}</td>
											<td><span class="clusterInfo1">${fn:length(cluster.runningPipelines)}</span></td>
											<td>${cluster.processedPipes}</td>
											<td>
												<div title="${cluster.cpuLoad}%" class="clusterCpuOut">
													<div class="clusterCpuIn" style="width:${cluster.cpuLoad}%">&nbsp;</div>
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
											<th colspan="2">Last Error</th>
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
											<td colspan="2" style="color:#F90101">${cluster.lastError}&nbsp;</td>
										</tr>
										
										
									</table>

									<div style="width:99%;margin-left: 10px;">
										<c:forEach items="${cluster.runningPipelines}" var="pipeline">
											<div id="${pipeline.type}" style="margin-bottom: 5px;" class="clusterBoard" >
												<div class="userBoardT1" style="margin:2px;padding:2px;height:10px;text-align:center;width:95%">${pipeline.serial} - ${pipeline.status}</div>
												<div class="userBoardT2" style="color:#F90101;height:10px; text-align:center;width:95%">${pipeline.qtdActivations}</div>
												<div class="userBoardT3" style="height:8px;text-align:center;width:95%">${pipeline.finishedActivities}</div>
											</div>
										</c:forEach>
									</div>

									
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

	$(document).ready(function() {
		window.setInterval(reloadPage, 5000);
	});

</script>				
				
<%@ include file="../../footer.jsp" %>
				