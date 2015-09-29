<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../../header.jsp" %>

				<div id="leftBox"> 
					<div id="bcbMainButtons" class="basicCentralPanelBar">
					</div>
					
					<div id="basicCentralPanel">
					
						<div class="basicCentralPanelBar">
							<img src="img/gears.png">
							<div class="basicCentralPanelBarText">Edit Configuration</div>
						</div>
						
						<div class="menuBarMain">
							<img alt="" onclick="login();" title="To login screen" class="button dicas" src="img/home.png" />
							<img alt="" onclick="save();" title="Save" class="button dicas" src="img/save.png" />
						</div>

						<div style="margin : 0 auto; width : 70%; margin-top:10px;"  >
							<form action="editConfig" method="POST" id="formPost">
								<input type="hidden" name="op" value="edit">
								<table class="tableForm" style="width:100%" >
									<tr>
										<th>Database Name</th>
										<th>Database User</th>
										<th>Database Password</th>
									</tr>
									<tr>
										<td><input type="text" name="databaseName" value="${config.databaseName}" ></td>
										<td><input type="text" name="userName" value="${config.userName}"></td>
										<td><input type="text" name="password" value="${config.password}"></td>
									</tr>
									<tr>
										<th>Pool Interval Seconds</th>
										<th>File Receiver Chunk Buffer Size</th>
										<th>File Receiver Port</th>
									</tr>
									<tr>
										<td><input type="text" name="poolIntervalSeconds" value="${config.poolIntervalSeconds}" ></td>
										<td><input type="text" name="fileReceiverChunkBufferSize" value="${config.fileReceiverChunkBufferSize}"></td>
										<td><input type="text" name="fileReceiverPort" value="${config.fileReceiverPort}"></td>
									</tr>
								</table>
							</form>
						</div>

					</div>												
					
				</div>
				<div id="rightBox"> 
					&nbsp;
				</div>
				
				
<script>

	function save() {
		$("#formPost").submit();
	}

	$(document).ready(function() {
		//
	} );	
	
	function login() {
		window.location.href="indexRedir";
	}
	
</script>				
				
<%@ include file="../../footer.jsp" %>
				