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
			<img onclick="back();" title="Back"	class="button dicas" src="img/back.png">
		</div>


		<div id="promoBar" style="display: table; width: 100%; margin-top:5px;">

			<form action="nodeSSHTerminal" method="post">
				<input type="hidden" name="macAddress"	value="${cluster.macAddress}">
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
								<td><input style="width: 100px" autocomplete="off" type="text" name="user"></td>
								<td><input style="width: 100px" type="password" name="password"></td>
								<td><input style="width: 50px" autocomplete="off" type="text" name="port"></td>
								<td><input style="width: 50px" type="submit" value="Login"></td>
								<td>&nbsp;</td>
							</tr>
						</table>
					</div>
					
					<div class="clusterBarMax" style="margin-top:5px;background-color:white;border:0px;color:#F90101">
						WARNING: This is a experimental feature and may not work as expected.<br><br>
						Things you CAN do:<br>
						* Simple commands like cp, mkdir, rm, chdir, who, cat, etc ...<br><br>
						Things you CANNOT do:<br>
						* You cannot start programs like vim, vi, nano and all that starts a screen. If you do something like, 
						you may cause the web terminal to crash and will need to start again other terminal, 
						needing to kill the crashed one since it will be connected anyway.
						<br>
						* You cannot send control keys or combos ( CTRL + K, SHIFT + ALT, etc... ).  
						<br>
						* You cannot start programs that takes the console forever ( you cannot send CTRL+C ).   
						<br><br>
						Scripts can be executed if they give you the console again, but its not been tested.    
					</div>
					
				</c:if>
			</form>
		</div>

		<c:if test="${session.alias == cluster.macAddress}">
			<div id="typeSelectPanel" class="menuBarMain"
				style="display: table; height: 150px; margin-top: 5px; font-size: 11px !important;padding-bottom: 10px;">
				<form action="nodeSSHTerminal" id="frmCommand" method="post">
					<input type="hidden" name="macAddress"	value="${cluster.macAddress}">
					<table style="margin-top: 10px; width:98%; margin-left: 10px">
						<tr>
							<td style="width:200px">Command</td>
							<td style="width:80px">Hide command</td>
							<td style="width:70px">&nbsp;</td>
						</tr>
						<tr>
							<td ><input id="command" autocomplete="off" type="text" name="command"></td>
							<td ><input id="hideCommand" type="checkbox" style="margin-left: 0px;width:20px;text-align:left;" name="hideCommand"> </td>
							<td>
								<img class="miniButton dicas" title="Logout SSH session" src="img/shutdown.png" onclick="logout()">
								<img class="miniButton dicas" title="Upload a file" src="img/upload.png" onclick="upload()">
								<img class="miniButton dicas" title="Download a file" src="img/download.png" onclick="download()">
							</td>
						</tr>
					</table>
				</form>

				<form action="nodeSSHUpload" id="frmUpload" method="post" enctype="multipart/form-data">
					<input type="hidden" name="macAddress"	value="${cluster.macAddress}">
					<table id="uploadBar" style="margin-top: 10px; width:600px; margin:0 auto; display:none">
						<tr>
							<td style="width:150px">File to Upload</td>
							<td style="width:330px">
							<input style="width:100%;" type="file" name="fileUpload"></td>
						</tr>
						<tr>
							<td>Target Path</td>
							<td><input style="width:100%;" type="text" name="targetPath"></td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td>
								<input style="width:145px; float:left" type="button" onclick="uploadCancel()" value="Cancel">
								<input style="width:145px; float:right" type="button" onclick="uploadFile()" value="Upload">
							</td>
						</tr>
					</table>
				</form>


				<form action="nodeSSHDownload" id="frmDownload" method="post">
					<input type="hidden" name="macAddress"	value="${cluster.macAddress}">
					<table id="downloadBar" style="margin-top: 10px; width:600px; margin:0 auto; display:none">
						<tr>
							<td style="width:150px">Source File ( and path )</td>
							<td style="width:330px">
							<input style="width:100%;" type="text" name="sourceFile"></td>
						</tr>
						<tr>
							<td>&nbsp;</td><td>
								<input style="width:145px; float:left" type="button" onclick="downloadCancel()" value="Cancel">
								<input style="width:145px; float:right" type="button" onclick="downloadFile()" value="Download">
							</td>
						</tr>
					</table>
				</form>



				<div class="menuBarMain" style="height: 300px; margin-top: 5px; font-size: 11px !important;">
					<textarea style="border: 0px;" id="code" name="code"><c:forEach var="line" items="${session.consoleOut}">${line}&#13;&#10;</c:forEach></textarea>
				</div>

			</div>
		</c:if>

	</div>

</div>

<div id="rightBox">

	<div id="tblIds" class="userBoard" style="padding-bottom:5px;">
		<div class="userBoardT1" style="text-align:center;width:95%">Host Detail</div>
		<div class="userBoardT2" style="text-align:center;width:95%">
			<table>
				<tr>
					<td style='width: 90px'>O.S.</td><td>${cluster.soName}</td>
				</tr>
				<tr>
					<td>Machine</td><td>${cluster.machineName}</td>
				</tr>
				<tr>
					<td>MAC Address</td><td>${cluster.macAddress}</td>
				</tr>
				<tr>
					<td>IP Address</td><td>${cluster.ipAddress}</td>
				</tr>
				<tr>
					<td>Java</td><td>${cluster.javaVersion}</td>
				</tr>
			</table>
		</div>
	</div>	


	<div id="tblIds" class="userBoard" style="padding-bottom:5px;">
		<div class="userBoardT1" style="text-align:center;width:95%">History : Click to repeat</div>
		<div class="userBoardT2" style="text-align:center;width:95%">
			<table>
				<c:forEach var="command" varStatus="index" items="${session.lastCommands}">
					<tr style="cursor:pointer" onclick="repeat('${command}')"><td>${command}</td></tr>
				</c:forEach>
			</table>
		</div>
	</div>	

</div>


<script>

	function upload() {
		downloadCancel();
		$("#uploadBar").css("display","block");
	}
	
	function uploadFile() {
		$("#uploadBar").css("display","none");
		$("#frmUpload").submit();
	}

	function uploadCancel() {
		$("#uploadBar").css("display","none");
	}

	function download() {
		uploadCancel();
		$("#downloadBar").css("display","block");
	}
	
	function downloadFile() {
		$("#downloadBar").css("display","none");
		$("#frmDownload").submit();
	}

	function downloadCancel() {
		$("#downloadBar").css("display","none");
	}
	
	function repeat( command ) {
		$("#command").val( command );
		$("#frmCommand").submit();
	}

	function logout( ) {
		$("#command").val( "logout" );
		$("#frmCommand").submit();
	}
	
	function callSudo( ) {
		$("#command").val( "sudo" );
		$("#frmCommand").submit();
	}
	
	function back() {
		window.location.href="viewClusters";
	}


	$(document).ready(function() {
		
		$("#command").focus();
		
		$("#command").keypress( function(event) {
		    if (event.which == 13) {
		        event.preventDefault();
		        $("#frmCommand").submit();
		    }
		});
		

		$("#hideCommand").change( function(event) {
			if ( $('#hideCommand').is(":checked") )	{
				var pass = document.getElementById('command');
				pass.type = 'password';
			} else {
				var pass = document.getElementById('command');
				pass.type = 'text';
			}
		});
		
		
		codeMirrorEditor = CodeMirror.fromTextArea(document.getElementById("code"), { 
			mode: "shell", 
			indentWithTabs: true,
			smartIndent: true,
			matchBrackets : true,
			readOnly: true,
			lineNumbers: true,
			lineWrapping:true
        });
		
		
		$('.CodeMirror-scroll').scrollTop($('.CodeMirror-scroll')[0].scrollHeight);
	});

</script>

<%@ include file="../../footer.jsp"%>
