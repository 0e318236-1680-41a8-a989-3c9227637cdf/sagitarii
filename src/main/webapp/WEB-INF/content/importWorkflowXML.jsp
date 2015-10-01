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
							<img alt="" src="img/map.png" />
							<div class="basicCentralPanelBarText">Import XML Workflow</div>
						</div>
						
						<div class="menuBarMain" style="position:relative">
							<img alt="" onclick="back();" title="Cancel" class="button dicas" src="img/back.png" />
							<img alt="" onclick="save();" title="Upload" class="button dicas" src="img/upload.png" />
						</div>

						<form method="post" action="doImportXmlWorkflow" id="frmSave" enctype="multipart/form-data">
							<div class="menuBarMain" style="display:table;height:52px;margin-top:5px;">
								<table style="margin-top:10px;width:450px;margin-left:10px" >
									<tr>
										<td style="width:25%">XML Table file</td>
										<td>
											<input id="tableFile" name="tableFile" style="width:98%" class="tableCellFormInput" type="file" >
										</td>
									</tr>	
								</table>
							</div>
						</form>
						
						
					</div>					
					
				</div>
				
				
				<div id="rightBox"> 
					<%@ include file="commonpanel.jsp" %>
				</div>
				
<script>


	$(document).ready(function() {
		
	});
	
	
	function back() {
		window.history.back();
	}
	

	// Save all
	function save() {
		$("#frmSave").submit();
	}

</script>				
				
<%@ include file="../../footer.jsp" %>
				