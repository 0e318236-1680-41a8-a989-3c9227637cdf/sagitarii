<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../../header.jsp" %>

				<div id="leftBox"> 
					
					<div id="bcbMainButtons" class="basicCentralPanelBar">
						<%@ include file="buttons.jsp" %>
					</div>
					
					<div id="basicCentralPanel">
					
						<div class="basicCentralPanelBar">
							<img src="img/gears.png">
							<div class="basicCentralPanelBarText">View Executors</div>
						</div>
						
						<div class="menuBarMain">
							<img onclick="newExecutorQuery();" title="New Executor: QUERY" class="button dicas" src="img/sql.png">
							<img onclick="newExecutorMap();" title="New Executor: MAP" class="button dicas" src="img/map.png">
							<img onclick="newExecutorReduce();" title="New Executor: REDUCE" class="button dicas" src="img/reduce.png">
							<img onclick="newExecutorSplit();" title="New Executor: SPLIT MAP" class="button dicas" src="img/split.png">
							<img onclick="newExecutorRScript();" title="New Executor: R SCRIPT" class="button dicas" src="img/r.png">
							<img onclick="newExecutorBash();" title="New Executor: Bash SCRIPT" class="button dicas" src="img/bash.png">
							<img onclick="newExecutorPython();" title="New Executor: Python" class="button dicas" src="img/python.png">
							<img onclick="newExecutorLibrary();" title="New Executor Library" class="button dicas" src="img/library.png">
						</div>

						<div style="margin : 0 auto; width : 95%; margin-top:10px;"  >
							<table class="tableForm"  id="example" >
								<thead>
									<tr>
										<th>Alias</th>
										<th>Type</th>
										<th>Wrapper</th>
										<th>SQL</th>
										<th>&nbsp;</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="executor" items="${executors}">
										<tr>
											<td>${executor.executorAlias}</td>
											<td>${executor.type}</td>
											<td>${executor.activationWrapper}&nbsp;</td>
											<td>${executor.selectStatement}&nbsp;</td>
											<td>&nbsp;
												<c:if test="${executor.type == 'PYTHON' || executor.type == 'BASH' || executor.type == 'RSCRIPT'}">
													<a href="viewText?idExecutor=${executor.idActivationExecutor}&edit=yes&fileName=repository/${executor.activationWrapper}">
														<img class="miniButton dicas" title="View" src="img/search.png">
													</a>
												</c:if>

												<c:if test="${executor.type != 'PYTHON' && executor.type != 'BASH' && executor.type != 'RSCRIPT' && executor.type != 'SELECT' || executor.type == 'LIBRARY'}">
													<a target="_BLANK" href="repository/${executor.activationWrapper}">
														<img class="miniButton dicas" title="Download Wrapper" src="img/download.png">
													</a>
												</c:if>

												<img class="miniButton dicas" title="Edit" onclick="edit('${executor.idActivationExecutor}');" src="img/edit.png">
												<img class="miniButton dicas" title="Delete" onclick="deleteExecutor('${executor.idActivationExecutor}','${executor.executorAlias}')" src="img/delete.png">
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>

					</div>												
					
				</div>
				<div id="rightBox"> 
					<%@ include file="commonpanel.jsp" %>
				</div>
				
				
<script>

	function newExecutorRScript() {
		window.location.href="newExecutorRScript";
	}
	
	function newExecutorBash() {
		window.location.href="newExecutorBash";
	}

	function newExecutorPython() {
		window.location.href="newExecutorPython";
	}

	function newExecutorLibrary() {
		window.location.href="newExecutorLibrary";
	}
	
	function newExecutorQuery() {
		window.location.href="newExecutorQuery";
	}
	
	function newExecutorMap() {
		window.location.href="newExecutorMap";
	}
	
	function newExecutorReduce() {
		window.location.href="newExecutorReduce";
	}
	
	function newExecutorSplit() {
		window.location.href="newExecutorSplit";
	}

	function deleteExecutor( idExecutor, name ) {
		showDialogBox( "Delete Activation Executor "+name+" ?", "deleteExecutor?idExecutor=" + idExecutor );
	}

	function edit( idExecutor ) {
		window.location.href="editExecutor?idExecutor=" + idExecutor;
	}

	$(document).ready(function() {
		
		$('#example').dataTable({
	        "oLanguage": {
	            "sUrl": "js/pt_BR.txt"
	        },	
	        "iDisplayLength" : 10,
			"bLengthChange": false,
			"fnInitComplete": function(oSettings, json) {
				doTableComplete();
			},
			"bAutoWidth": false,
			"sPaginationType": "full_numbers",
			"aoColumns": [ 
				  { "sWidth": "10%" },
				  { "sWidth": "10%" },
				  { "sWidth": "20%" },
				  { "sWidth": "45%" },
				  { "sWidth": "15%" }]
			
		}).fnSort( [[0,'desc']] );
		
		
	});	
	
</script>				
				
<%@ include file="../../footer.jsp" %>
				