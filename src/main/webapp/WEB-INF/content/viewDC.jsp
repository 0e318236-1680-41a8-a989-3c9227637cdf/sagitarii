<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../../header.jsp" %>

				<div id="leftBox"> 
					<div id="bcbMainButtons" class="basicCentralPanelBar">
						<%@ include file="buttons.jsp" %>
					</div>
					
					<div id="basicCentralPanel">

						<div class="basicCentralPanelBar">
							<img src="img/fragment.png">
							<div class="basicCentralPanelBarText">Instance Delivery Control</div>
						</div>
						
						<div class="menuBarMain" style="position:relative">
							<img alt="" onclick="back();" title="Back" class="button dicas" src="img/back.png" />
						</div>
										
										
						<div class="basicCentralPanelBar">
							<img src="img/time.png">
							<div class="basicCentralPanelBarText">Time Control: First Run Limit: ${firstDelayLimitSeconds}</div>
						</div>
												
						<div style="margin : 0 auto; width : 95%; margin-bottom:30px; margin-top:10px;" id="dtAgeContainer">
							<table>
								<tr>
									<th style="width:5%">ID</th>
									<th style="width:15%">Instance Content</th>
									<th style="width:10%">Average Time</th>
									<th style="width:10%">Total Calculated</th>
								</tr>
								<c:forEach var="age" items="${ageStatistics}">
									<tr>
										<td class="tableCellFormRight">${age.idTimeControl}</td>
										<td class="tableCellFormRight">${age.content}</td>
										<td class="tableCellFormRight">
											<fmt:formatDate pattern="HH:mm:ss" type="time" value="${age.averageAge}" />
										</td>
										<td class="tableCellFormRight">${age.calculatedCount}</td>
									</tr>
								</c:forEach>
							</table>
						</div>

						<div class="basicCentralPanelBar">
							<img src="img/verify.png">
							<div class="basicCentralPanelBarText">Instance Delivery Control Status</div>
						</div>
						<div style="margin : 0 auto; width : 95%; margin-bottom:30px; margin-top:10px;"  >
							<table>
								<tr>
									<th style="width:10%">Instance ID</th>
									<th style="width:10%">Delivered to</th>
									<th style="width:10%">Instance Type</th>
									<th style="width:10%">Running Time</th>
									<th style="width:5%">Delayed</th>
									<th style="width:45%">Package Content</th>
								</tr>
								<c:forEach var="unit" items="${units}">
									<c:if test="${unit.delayed == 'true'}">
										<tr style="color:#F90101">
									</c:if>
									<c:if test="${unit.delayed == 'false'}">
										<tr>
									</c:if>
										<td class="tableCellFormRight">${unit.instance.serial}</td>
										<td class="tableCellFormRight">${unit.macAddress}</td>
										<td class="tableCellFormRight">${unit.instance.type}</td>
										<td class="tableCellFormRight">${unit.ageTime}</td>
										<td class="tableCellFormRight">${unit.delayed}</td>
										<td class="tableCellFormRight">
											<table>
												<tr>
													<th>Workflow</th>
													<th>Experiment</th>
													<th>Fragment</th>
													<th>Serial</th>
													<th>Command</th>
												</tr>
												<c:forEach var="activation" items="${unit.activations}">
													<tr>
														<td>${activation.workflow}</td>
														<td>${activation.experiment}</td>
														<td>${activation.fragment}</td>
														<td>${activation.activitySerial}</td>
														<td>${activation.command}</td>
													</tr>
												</c:forEach>
											</table>
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
	
	function reloadPage() {
		//location.reload();
	}

	$(document).ready(function() {
		//window.setInterval(reloadPage, 5000);
	});
	
	function back() {
		window.history.back();
	}	
	

</script>					
<%@ include file="../../footer.jsp" %>
				