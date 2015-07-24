<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../../header.jsp" %>

				<div id="leftBox"> 
					<div id="bcbMainButtons" class="basicCentralPanelBar">
						<%@ include file="buttons.jsp" %>
					</div>
					
					<div id="basicCentralPanel">

						<div class="basicCentralPanelBar">
							<img src="img/connection.png">
							<div class="basicCentralPanelBarText">View System Metrics</div>
						</div>

						<div class="menuBarMain">
							<img alt="" onclick="back();" title="Back" class="button dicas" src="img/back.png" />
							<img onclick="reset();" title="Reset all Metrics" class="button dicas" src="img/refresh.png">
							<img onclick="nodes();" title="View Nodes Statistics" class="button dicas" src="img/node.png">
							<img onclick="activitiesTag();" title="View Activities Statistics by Tag" class="button dicas" src="img/workflow.png">
							<img onclick="activitiesType();" title="View Activities Statistics by Type" class="button dicas" src="img/settings1.png">
							<img onclick="database();" title="View Database Statistics" class="button dicas" src="img/connection.png">
							<img onclick="files();" title="View Files Statistics" class="button dicas" src="img/filetransfer.png">
						</div>						

						<div style="margin : 0 auto; width : 95%; margin-top:10px;"  >
							<c:forEach var="metric" items="${metrics}">
								<c:if test="${metric.type == type}">
								<div style="float:left">
									<img id="ID${metric.name}" style="width:280px;height:110px" src="getMetrics?metricName=${metric.name}">
								</div>
								</c:if>
							</c:forEach>
						</div>

					</div>												
					
				</div>
				<div id="rightBox"> 
					<%@ include file="commonpanel.jsp" %>
				</div>

<script>
	
	function reloadPage() {
		d = new Date();
		
		<c:forEach var="metric" items="${metrics}">
			<c:if test="${metric.type == type}">
				$("#ID${metric.name}").attr("src", "getMetrics?metricName=${metric.name}&time=ABC"+d.getTime());
			</c:if>
		</c:forEach>
		
		
	}

	$(document).ready(function() {
		window.setInterval(reloadPage, 4000);
	});
	
	function reset() {
		window.location.href="resetMetrics";
	}

	function activitiesTag() {
		window.location.href="viewMetrics?type=ACTIVITY_TAG";
	}

	function activitiesType() {
		window.location.href="viewMetrics?type=ACTIVITY_TYPE";
	}

	function nodes() {
		window.location.href="viewMetrics?type=NODE";
	}

	function database() {
		window.location.href="viewMetrics?type=ENTITY";
	}

	function files() {
		window.location.href="viewMetrics?type=FILE";
	}
	
	function back() {
		window.history.back();
	}		
	
</script>					
				
<%@ include file="../../footer.jsp" %>
				