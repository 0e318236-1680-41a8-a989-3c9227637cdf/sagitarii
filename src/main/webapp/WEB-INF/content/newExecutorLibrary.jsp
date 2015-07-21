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
							<img alt="" src="img/library.png" />
							<div class="basicCentralPanelBarText">Create new Library for Executor support</div>
						</div>
						
						<div class="menuBarMain" style="position:relative">
							<img alt="" onclick="back();" title="Cancel" class="button dicas" src="img/back.png" />
							<img alt="" onclick="save();" title="Save Library" class="button dicas" src="img/save.png" />
						</div>

						<form method="post" action="saveExecutor" id="frmSave" enctype="multipart/form-data">
							<input type="hidden" id="stmtField" name="executor.selectStatement">
							<input type="hidden" name="executorTarget" value="LIBRARY">
							<div class="menuBarMain" style="display:table;height:52px;margin-top:5px;">
								<table style="margin-top:10px;width:450px;margin-left:10px" >
									<tr>
										<td style="width:25%">Alias</td>
										<td><input maxlength="20" size="22" id="executorAlias" name="executorAlias" style="width:98%" class="tableCellFormInput" type="text" ></td>
									</tr>	
									<tr>
										<td style="width:25%">Library File</td>
										<td>
											<input id="wrapperFile" name="wrapperFile" style="width:98%" class="tableCellFormInput" type="file" >
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

	function back() {
		window.location.href="viewExecutors";
	}
	

	function save() {
		if ( $("#executorAlias").val() == "" ) {
			showMessageBox( "You must provide an alias before save." );
			$("#executorAlias").focus();
			return;
		}
		
		$("#frmSave").submit();
		
	}

</script>				
				
<%@ include file="../../footer.jsp" %>
				