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
					
					<div id="promoBar" style="height:220px;display:table;width:95%;margin:0 auto">
						<c:forEach var="cluster" items="${clusterList}">
						
								<div onclick="showNodeDetails('${cluster.macAddress}')" id="${cluster.macAddress}" class="clusterBar">
								
									<table style="margin: 5px;width:90%;">
										<tr>
											<td colspan="3">
												<c:if test="${cluster.status == 'IDLE'}">
													<img style="width:32px;height:32px" src="img/computer.png">
												</c:if>
												<c:if test="${cluster.status == 'ACTIVE'}">
													<img style="width:32px;height:32px" src="img/computerblue.png">
												</c:if>
												<c:if test="${cluster.status == 'DEAD'}">
													<img style="width:32px;height:32px" src="img/computerred.png">
												</c:if>
												<c:if test="${cluster.status == 'TOO_BUSY'}">
													<img style="width:32px;height:32px" src="img/computergreen.png">
												</c:if>
											</td>
										</tr>
										<tr>
											<td colspan="3">
												<span style="font-weight:bold;font-size:12px">${cluster.machineName}</span>
											</td>
										</tr>
										<tr><td colspan="3">${cluster.ipAddress}</td></tr>
										<tr>
											<td colspan="3">
												<c:if test="${cluster.type != 'MAIN'}">
													${cluster.macAddress}
												</c:if>
												<c:if test="${cluster.type == 'MAIN'}">
													Internal Node
												</c:if>
											</td>
										</tr>
										<tr>
											<td><span class="dicas" title="Status">${cluster.status}</span></td>
											<td>
											<span class="dicas" title="Running / Max">${fn:length(cluster.runningInstances)} / ${cluster.maxAllowedTasks}</span></td>
											<td>
											<span class="dicas" title="Finished">${cluster.processedPipes}</span></td>
										</tr>
										<tr>
											<td>
												<div title="CPU: ${cluster.cpuLoad}%" class="clusterCpuOut dicas">
													<div class="clusterCpuIn" style="background-color:#0266C8; width:${cluster.cpuLoad}%">&nbsp;</div>
												</div> 
											</td>
											<td>
												<div title="Memory: ${cluster.memoryPercent}% of ${cluster.totalMemory}Mb" class="clusterCpuOut dicas">
													<div class="clusterCpuIn" style="width:${cluster.memoryPercent}%">&nbsp;</div>
												</div> 
											</td>
											<td>
												<div title="Disk: ${cluster.diskPercent}% of ${cluster.totalDiskSpace}Mb" class="clusterCpuOut dicas">
													<div class="clusterCpuIn" style="background-color:#00933B; width:${cluster.diskPercent}%">&nbsp;</div>
												</div> 
											</td>
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

	function clean() {
		window.location.href="cleanWorkspaces";
	}

	function reloadWrappers() {
		window.location.href="clusterControl?command=reloadWrappers";
	}


	function showNodeDetails( node ) {
		window.location.href="showNodeDetails?macAddress=" + node;
	}

	$(document).ready(function() {
		reloadDicas();
		window.setInterval(reloadPage, 5000);
		
	});

</script>				
				
<%@ include file="../../footer.jsp" %>
				