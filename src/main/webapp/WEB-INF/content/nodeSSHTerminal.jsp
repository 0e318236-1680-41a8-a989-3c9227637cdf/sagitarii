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
						<img onclick="back();" title="Back to Nodes List" class="menuButton dicas" src="img/back.png">
					</div>
					
					<div id="promoBar" style="height:220px;display:table;width:100%">
						
						<div id="${cluster.macAddress}" class="clusterBarMax" >
						
							<table style="margin-bottom: 5px;width:98%; margin-left:10px; margin-top: 5px">
								<tr>
									<td colspan="10" >
										<img onclick="back('${cluster.macAddress}')" class="dicas" title="Shutdown this node (no confirmation)" src="img/shutdown.png" style="width:24px;height:24px">

						<form action="nodeSSHTerminal" method="post">
							<table style="margin-top:10px;width:450px;margin-left:10px" >
								<tr>
									<td><input type="hidden" name="macAddress" value="${cluster.macAddress}"></td>
									<td><input type="text" name="user"></td>
									<td><input type="text" name="password"></td>
									<td><input type="submit" value="ok"></td>
								</tr>	
							</table>							
						</form>
										
										
									</td>
								</tr>
								<tr >
									<th style='width:90px'>O.S.</th>
									<th style='width:110px'>Machine</th>
									<th style='width:140px'>MAC Address</th>
									<th style='width:100px'>IP Address</th>
									<th style='width:60px'>Java</th>
								</tr>
								<tr>
									<td>${cluster.soName}</td>
									<td>${cluster.machineName}</td>
									<td >${cluster.macAddress}</td>
									<td>${cluster.ipAddress}</td>
									<td>${cluster.javaVersion}</td>
								</tr>
							</table>
							
						</div>
					</div>										
					
					<div id="typeSelectPanel" class="menuBarMain" style="display:table;height:150px;margin-top:5px;font-size:11px !important;">
						<form action="nodeSSHTerminal" method="post">
							<table style="margin-top:10px;width:450px;margin-left:10px" >
								<tr>
									<td>Command:</td>
									<td><input type="hidden" name="macAddress" value="${cluster.macAddress}"></td>
									<td><input type="text" name="command"></td>
									<td><input type="submit" value="Send"></td>
								</tr>	
							</table>							
						</form>
						
						<div class="menuBarMain" style="height:100px;margin-top:5px;font-size:11px !important;">
							<textarea style="border:0px;height:140px" id="code" name="code">${session.consoleOut}</textarea>
						</div>
						
					</div>
					
					
					
				</div>
				<div id="rightBox"> 

					<%@ include file="commonpanel.jsp" %>
					
				</div>
				
				
<script>
	
	function back() {
		window.location.href="viewClusters";
	}


	$(document).ready(function() {
		
		codeMirrorEditor = CodeMirror.fromTextArea(document.getElementById("code"), { 
			mode: "text/x-sql", 
			indentWithTabs: true,
			smartIndent: true,
			matchBrackets : true,
			readOnly: false,
			lineNumbers: true,
			lineWrapping:true
        });
		
	});

</script>				
				
<%@ include file="../../footer.jsp" %>
				