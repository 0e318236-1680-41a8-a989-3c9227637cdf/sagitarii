<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ include file="../../header.jsp" %>


				<div id="leftBox"> 
				
					<div id="bcbMainButtons" class="basicCentralPanelBar">
						<%@ include file="buttons.jsp" %>
					</div>
				
					<div id="basicCentralPanel">
					
						<div class="basicCentralPanelBar">
							<img alt="" src="img/sql.png" />
							<div class="basicCentralPanelBarText">View Text File ${fileName}</div>
						</div>
						
						<div class="menuBarMain" style="position:relative">
							<img alt="" onclick="back();" title="Cancel" class="button dicas" src="img/back.png" />
						</div>

							
						<div class="menuBarMain" style="display:table;height:500px;margin-top:5px">
							<div style="float:left; width:99%">
								<table style="margin-top:10px;margin-left:10px" >
									<tr>
										<td style="padding:0px;">
											<div class="menuBarMain" style="height:500px;margin-bottom:5px;font-size:11px !important;width:99%;">
												<textarea style="border:0px;height:140px" id="code" name="code">${textContent}</textarea>
											</div>
										</td>
									</tr>	
								</table>
							</div>
						</div>							


					</div>					
					
				</div>
				
				<div id="rightBox"> 
					<%@ include file="commonpanel.jsp" %>
				</div>
				
<script>

	$(document).ready(function() {

		codeMirrorEditor = CodeMirror.fromTextArea(document.getElementById("code"), { 
			mode: "xml", 
			indentWithTabs: true,
			smartIndent: true,
			matchBrackets : true,
			readOnly: true,
			lineNumbers: true,
			lineWrapping:true
        });
		
	});
	
	function back() {
		window.history.back();
	}


</script>				
				
<%@ include file="../../footer.jsp" %>
				