<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../../header.jsp" %>

				<div id="leftBox"> 
					<div id="bcbMainButtons" class="basicCentralPanelBar" style="height:50px">
						<%@ include file="buttons.jsp" %>
					</div>
					
					<div id="basicCentralPanel">

						<div class="basicCentralPanelBar">
							<img src="img/connection.png">
							<div class="basicCentralPanelBarText">Active Database Connections</div>
						</div>

						<div style="margin : 0 auto; width : 95%; margin-top:10px;" id="dtTableContainer">
							<table>
								<tr>
									<th style="width:15%">Start</th>
									<th style="width:10%">Database</th>
									<th style="width:5%">State</th>
									<th style="width:15%">Application</th>
									<th style="width:50%">Query</th>
								</tr>
								<c:forEach var="connection" items="${connections}">
									<tr>
										<td class="tableCellFormRight" >${connection.table}&nbsp;</td>
										<td class="tableCellFormRight" >${connection.mode}&nbsp;</td>
										<td class="tableCellFormRight" >${connection.granted}&nbsp;</td>
										<td class="tableCellFormRight" >${connection.tuples}&nbsp;</td>
										<td class="tableCellFormRight" >${connection.query}&nbsp;</td>
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
		location.reload();
	}

	$(document).ready(function() {
		window.setInterval(reloadPage, 3000);
	});
	
</script>					
				
<%@ include file="../../footer.jsp" %>
				