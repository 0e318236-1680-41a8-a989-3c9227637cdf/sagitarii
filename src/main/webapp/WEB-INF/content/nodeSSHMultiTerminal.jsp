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
			<div class="basicCentralPanelBarText">SSH Synch Terminal</div>
		</div>

		<div class="menuBarMain">
			<img onclick="back();" title="Back"
				class="button dicas" src="img/back.png">
		</div>
		
		<div id="typeSelectPanel" class="menuBarMain"
			style="display: table; height: 30px; margin-top: 5px; font-size: 11px !important;">
			<form action="nodeSSHMultiTerminal" id="frmCommand" method="post">
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
							<img class="miniButton dicas" title="Upload a file" src="img/upload.png" onclick="upload()">
						</td>
					</tr>
				</table>
			</form>
		</div>	

		<c:forEach var="session" items="${sessions}">
			<div id="${session.alias}" class="menuBarMain host"
				style="display: table;  margin-top: 5px; font-size: 11px !important;padding-bottom: 10px;">

				<table style="margin-top: 10px; width:95%; margin: 0 auto">
					<tr>
						<th style="width:100px">Node</th>
						<th style="width:100px">Host</th>
						<th style="width:100px">User</th>
					</tr>
					<tr>
						<td>${session.alias}</td>
						<td>${session.host}</td>
						<td>${session.user}</td>
					</tr>
				</table>
				
				<form action="nodeSSHUpload" id="frmUpload" method="post" enctype="multipart/form-data">
					<input type="hidden" name="option"	value="multi">
					<table id="uploadBar" style="width:600px; margin:0 auto; margin-top: 10px; display:none">
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
				
				<div class="menuBarMain" style="height: 300px; margin-top: 5px; font-size: 11px !important;">
					<textarea name="tx${session.alias}" id="tx${session.alias}" style="border: 0px;"><c:forEach var="line" items="${session.consoleOut}">${line}&#13;&#10;</c:forEach></textarea>
				</div>

			</div>
		</c:forEach>

	</div>

</div>

<div id="rightBox">

	<div id="tblIds" class="userBoard" style="padding-bottom:5px;">
		<div class="userBoardT1" style="text-align:center;width:95%">Connected Hosts</div>
		<div class="userBoardT2" style="text-align:center;width:95%">
			<table>
				<c:forEach var="session" varStatus="index" items="${sessions}">
					<tr style="cursor:pointer" onclick="show('${session.alias}')"><td>${session.machineName}</td><td>${session.host}</td></tr>
				</c:forEach>
			</table>
		</div>
	</div>	

	<div id="tblIds" class="userBoard" style="padding-bottom:5px;">
		<div class="userBoardT1" style="text-align:center;width:95%">History : Click to repeat</div>
		<div class="userBoardT2" style="text-align:center;width:95%">
			<table>
				<c:forEach var="command" varStatus="index" items="${lastMultiCommands}">
					<tr style="cursor:pointer" onclick="repeat('${command}')"><td>${command}</td></tr>
				</c:forEach>
			</table>
		</div>
	</div>	

</div>


<script>

	function upload() {
		$("#uploadBar").css("display","block");
	}
	
	function uploadFile() {
		$("#uploadBar").css("display","none");
		$("#frmUpload").submit();
	}
	
	function uploadCancel() {
		$("#uploadBar").css("display","none");
	}

	function show( host ) {
		$(".host").css("display","none");
		$("#" + host).css("display","table");
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

	function textAreaAdjust(o) {
	    o.style.height = "1px";
	    o.style.height = (25+o.scrollHeight)+"px";
	}
	
	
	function formatTa( id ) {
		CodeMirror.fromTextArea( document.getElementById(id) , { 
			mode: "shell", 
			indentWithTabs: true,
			smartIndent: true,
			matchBrackets : true,
			readOnly: true,
			lineNumbers: true,
			lineWrapping:true
        });		
	}

	$(document).ready(function() {
		
		$("#command").focus();
		
		$("#command").keypress(function(event) {
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
		
		var lastId = "";
		<c:forEach var="session" varStatus="index" items="${sessions}">
			formatTa("tx${session.alias}");
			lastId = "${session.alias}";
		</c:forEach>

		show( lastId );
		
		$('.CodeMirror-scroll').scrollTop($('.CodeMirror-scroll')[0].scrollHeight);
	
	});

</script>

<%@ include file="../../footer.jsp"%>
