<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../../header.jsp" %>

				<div id="leftBox" > 
				
					<div id="bcbMainButtons" class="basicCentralPanelBar">
						<%@ include file="buttons.jsp" %>
					</div>
					
					<div id="basicCentralPanel">
					
						<div class="basicCentralPanelBar">
							<img src="img/workflow.png">
							<div class="basicCentralPanelBarText">New Experiment</div>
						</div>
						

						<div id="newPannel" style="height:100px; width:95%; margin:0 auto;margin-top:10px;margin-bottom:10px;">
							<form action="doNewExperiment" method="POST" id="formPost">
								<input type="hidden" value="${workflow.idWorkflow}" name="idWorkflow">
								<table>
									<tr>
										<td class="tableCellFormLeft">Workflow</td>
										<td class="tableCellFormRight"> 
											${workflow.tag} - ${workflow.description} 
										</td>
									</tr>

									<tr>
										<td class="tableCellFormLeft">Description</td>
										<td class="tableCellFormRight"> 
											<input id="exDescription" name="description" class="tableCellFormInputText" type="text"> 
										</td>
									</tr>
									
									
								</table>
							</form>
							<div onclick="doPost()" class="basicButton">Create</div>							
							<div onclick="cancel()" class="basicButton">Cancel</div>							
						
						</div>


					</div>												
					
				</div>
				
				<div id="rightBox"> 
					<%@ include file="commonpanel.jsp" %>
				</div>
				
				
<script>

	function doPost() {
		var desc = $("#exDescription").val();
		if ( ( desc == '' ) ) {
			showMessageBox('Please fill all required fields.');
			return;
		} 
		$("#formPost").submit();
	}

	function doPostEdit() {
		var desc = $("#exDescription").val();
		if ( (tag == '') || ( desc == '' ) ) {
			showMessageBox('Please fill all required fields.');
			return;
		} 
		$("#formPostEdit").submit();
	}

	function cancel() {
		window.location.href="viewWorkflow?idWorkflow=${workflow.idWorkflow}";
	}
	
	$(document).ready(function() {
	
	});	
	
</script>				
				
<%@ include file="../../footer.jsp" %>
				