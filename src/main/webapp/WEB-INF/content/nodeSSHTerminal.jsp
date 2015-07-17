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
			<img src="img/bash.png">
			<div class="basicCentralPanelBarText">SSH Terminal for node ${cluster.macAddress}</div>
		</div>


		<div class="menuBarMain">
			<img onclick="back();" title="Back"
				class="button dicas" src="img/back.png">
		</div>


		<div id="promoBar" style="display: table; width: 100%; margin-top:5px;">

			<form action="nodeSSHTerminal" method="post">
				<div id="${cluster.macAddress}" class="clusterBarMax">
					<input type="hidden" name="macAddress"
						value="${cluster.macAddress}">
					<table
						style="margin-bottom: 5px; width: 98%; margin-left: 10px; margin-top: 5px">
						<tr>
							<th style='width: 90px'>O.S.</th>
							<th style='width: 110px'>Machine</th>
							<th style='width: 140px'>MAC Address</th>
							<th style='width: 100px'>IP Address</th>
							<th style='width: 60px'>Java</th>
						</tr>
						<tr>
							<td>${cluster.soName}</td>
							<td>${cluster.machineName}</td>
							<td>${cluster.macAddress}</td>
							<td>${cluster.ipAddress}</td>
							<td>${cluster.javaVersion}</td>
						</tr>
					</table>
				</div>

				<c:if test="${session.alias != cluster.macAddress}">
					<div id="${cluster.macAddress}" class="clusterBarMax" style="margin-top:5px;">
						<table
							style="margin-bottom: 5px; width: 98%; margin-left: 10px; margin-top: 5px">
							<tr>
								<th style='width: 110px'>User Name</th>
								<th style='width: 110px'>Password</th>
								<th style='width: 60px'>Port</th>
								<th style='width: 60px'>&nbsp;</th>
								<th>&nbsp;</th>
							</tr>
							<tr>
								<td><input style="width: 100px" type="text" name="user"></td>
								<td><input style="width: 100px" type="text" name="password"></td>
								<td><input style="width: 50px" type="text" name="port"></td>
								<td><input style="width: 50px" type="submit" value="Login"></td>
								<td>&nbsp;</td>
							</tr>
						</table>
					</div>
				</c:if>
			</form>
		</div>

		<c:if test="${session.alias == cluster.macAddress}">
			<div id="typeSelectPanel" class="menuBarMain"
				style="display: table; height: 150px; margin-top: 5px; font-size: 11px !important;padding-bottom: 10px;">
				<form action="nodeSSHTerminal" method="post">
					<input type="hidden" name="macAddress"
						value="${cluster.macAddress}">
					<table style="margin-top: 10px; width: 450px; margin-left: 10px">
						<tr>
							<td>Command:</td>
							<td><input type="text" name="command"></td>
							<td><input type="submit" value="Send"></td>
						</tr>
					</table>
				</form>

				<div class="menuBarMain" style="height: 400px; margin-top: 5px; font-size: 11px !important;">
					<textarea style="border: 0px;" id="code" name="code"><c:forEach var="line" items="${session.consoleOut}">${line}&#13;&#10;</c:forEach></textarea>
				</div>

			</div>
		</c:if>


	</div>

</div>

<div id="rightBox">

	<%@ include file="commonpanel.jsp"%>

</div>


<script>
	
	function back() {
		window.location.href="viewClusters";
	}


	$(document).ready(function() {
		
		codeMirrorEditor = CodeMirror.fromTextArea(document.getElementById("code"), { 
			mode: "text/x-sh", 
			indentWithTabs: true,
			smartIndent: true,
			matchBrackets : true,
			readOnly: false,
			lineNumbers: true,
			lineWrapping:true
        });
		
	});

</script>

<%@ include file="../../footer.jsp"%>
