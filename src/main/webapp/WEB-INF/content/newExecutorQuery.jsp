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
							<div class="basicCentralPanelBarText">Create new QUERY Executor</div>
						</div>
						
						<div class="menuBarMain" style="position:relative">
							<img alt="" onclick="back();" title="Cancel" class="button dicas" src="img/back.png" />
							<img alt="" onclick="save();" title="Generate SQL and Save Executor" class="button dicas" src="img/save.png" />
						</div>

						<div class="menuBarMain" style="position:relative;margin-top:5px;display:table;padding-bottom:5px">
							<table>
								<tr><th>Query Explain</th></tr>
								<tr><td><div id="explainContent">&nbsp;</div></td></tr>
							</table>
						</div>

						<form method="post" action="saveExecutor" id="frmSave" enctype="multipart/form-data">
							<input type="hidden" value="SELECT" name="executorTarget">
							<input type="hidden" id="stmtField" name="executor.selectStatement">
							
							<div class="menuBarMain" style="display:table;height:52px;margin-top:5px">
								<div style="float:left; width:85%">
									<table style="margin-top:10px;margin-left:10px" >
										<tr>
											<td style="width:50px">Alias</td>
											<td style="width:110px"><input maxlength="20" id="executorAlias" name="executorAlias" style="width:98%" class="tableCellFormInput" type="text" ></td>
											<td>
												<div class="menuBarMain" style="height:100px;margin-bottom:5px;font-size:11px !important;width:99%;">
													<textarea style="border:0px;height:140px" id="code" name="code"></textarea>
												</div>
											</td>
										</tr>	
									</table>
								</div>
								<div style="float:right; width:90px;margin-right:10px;padding-top:6px;">
									<img style="float:right;" alt="" onclick="showSourcePanel();" title="Configure Source Table(s)" class="button dicas" src="img/source.png" />
									<img style="float:right;" alt="" onclick="showTargetPanel();" title="Configure Target Table" class="button dicas" src="img/target.png" />
									<img style="float:right;" alt="" onclick="clean();" title="Clear all" class="button dicas" src="img/clean.png" />
								</div>
							</div>							
						</form>




						
						<div id="targetPanel" class="menuBarMain" style="display:none;height:150px;margin-top:5px;">
							<div style="float:left; width:90%">
								<table style="margin-top:10px;margin-left:10px;margin-bottom:10px" >
									<tr>
										<td style="width:60px">Target Table</td>
										<td style="width:100px">
											<select id="selectTarget" class="tableCellFormInputCombo" style="width:90%">
												<option value="">-- Select One --</option>
												<c:forEach var="table" items="${customTables}"><option value="${table.name}">${table.name}</option></c:forEach>
											</select>
										</td>
										<td style="width:60px">Attributes</td>
										<td style="width:200px">
											<div id="targetTableContent"> &nbsp; </div>
										</td>
									</tr>	
								</table>
							</div>
						</div>


						<div id="sourcePanel" class="menuBarMain" style="display:none;height:150px;margin-top:5px;font-size:11px !important;">
							<div style="float:left; width:90%">
								<table style="margin-top:10px;margin-left:10px;margin-bottom:10px" >
									<tr>
										<td style="width:60px">Source Tables</td>
										<td style="width:100px">
											<select id="selectSource" class="tableCellFormInputCombo" style="margin-bottom:5px;width:90%">
												<option value="">-- Select One --</option>
												<c:forEach var="table" items="${customTables}"><option value="${table.name}">${table.name}</option></c:forEach>
											</select>
										</td>
										<td style="width:60px">Attributes</td>
										<td style="width:200px">
											<div id="sourceTableContent"> &nbsp; </div>
										</td>
									</tr>	
								</table>
							</div>
							<div style="float:right; margin-right:10px;">
								<img style="float:right;" alt="" onclick="doSource();" title="Apply Source Config" class="button dicas" src="img/save.png" />
							</div>
						</div>


					</div>					
					
				</div>
				
				
				<div id="rightBox"> 
					<%@ include file="commonpanel.jsp" %>
					
					<div id="tblIds" class="userBoard" style="padding-bottom:5px;">
						<div class="userBoardT1" style="text-align:center;width:95%">System ID Tags</div>
						<div class="userBoardT2" style="text-align:center;width:95%">
							<table>
								<tr><td>Workflow ID</td><td>%ID_WFL%</td></tr>
								<tr><td>Experiment ID</td><td>%ID_EXP%</td></tr>
								<tr><td>Activity ID</td><td>%ID_ACT%</td></tr>
								<tr><td>Instance ID</td><td>%ID_PIP%</td></tr>
							</table>
						</div>
					</div>
					
					
					<div id="srcTbls" class="userBoard" style="padding-bottom:5px;">
						<div class="userBoardT1" style="text-align:center;width:95%">Source Tables</div>
						<div class="userBoardT2" style="text-align:center;width:95%">
							<table style="margin-bottom:3px;" id="srcTbl">
							</table>
							Click the field to remove it
						</div>
					</div>	
					
						
				</div>
				
<script type="text/javascript" src="js/newExecutorQuery.js?sdsd=34"></script>
<script>

	$(document).ready(function() {

		$("#selectTarget").change( function(){
			var tableName = $("#selectTarget").val();
			showTargetTable( tableName );
			targetRelation = tableName;
			targetFields = [];
		});

		
		$("#selectSource").change( function(){
			var tableName = $("#selectSource").val();
			showSourceTable( tableName );
			tempSourceRelation = tableName;
			tempSourceFields = [];
		});
		
		codeMirrorEditor = CodeMirror.fromTextArea(document.getElementById("code"), { 
			mode: "text/x-sql", 
			indentWithTabs: true,
			smartIndent: true,
			matchBrackets : true,
			readOnly: false,
			lineNumbers: true,
			lineWrapping:true
        });
		
		codeMirrorEditor.on("change", function(cm, change) { 
			explainQuery( codeMirrorEditor.getDoc().getValue() );
		});

		
		showTargetPanel();
	});
	
	function clean() {
		targetFields = [];
		codeMirrorEditor.getDoc().setValue("");
		temSourceRelation = null;
		tempSourceFields = [];
		sourceRelations = [];
		sourceFields = [];
		targetRelation = null;
		$("#srcTbl").find("tr").remove();
		$('#targetPanel option:eq(0)').prop('selected', true)
		$('#sourcePanel option:eq(0)').prop('selected', true)
	}
	
	function showTargetPanel() {
		$("#targetPanel").css("display","table");
		$("#sourcePanel").css("display","none");
		whatPanel = "T";
	}
	
	function back() {
		window.location.href="viewExecutors";
	}

	function showSourcePanel() {
		$("#targetPanel").css("display","none");
		$("#sourcePanel").css("display","table");
		whatPanel = "S";
	}
	
	function doSource() {
		if ( (tempSourceFields.length > 0) && ( tempSourceRelation != null  ) ) {
			storeSourceRelation( tempSourceRelation, tempSourceFields );
			tempSourceFields = [];
			generateSql();
		} else {
			showMessageBox("Please select a table name and fields before apply changes.");
		}
	}
	
	function doFieldClick( fieldName ) {
		if ( whatPanel == "T" ) {
			if ( targetRelation != null ) {
				addTargetField(fieldName);
				generateSql();
			} else {
				showMessageBox("Please select the target table first.");
			}
		}
		
		if ( whatPanel == "S" ) {
			if ( targetRelation != null ) {
				addSourceField(fieldName);
				generateSql();
			} else {
				showMessageBox("Please select the target table and fields first.");
			}
		}
	}
	
	
	function save() {
		var query = codeMirrorEditor.getDoc().getValue();
		
		if ( query.indexOf( "(id_instance, id_experiment, id_activity," ) == -1) {
			showMessageBox( "You must keep the system attributes 'id_instance, id_experiment, id_activity' as untouchables in your query." );
			return;
		}

		if ( query.indexOf( "(id_instance, id_experiment, id_activity," ) == -1) {
			showMessageBox( "You must keep the system tags '%ID_PIP%, %ID_EXP%, %ID_ACT%' as untouchables in your query." );
			return;
		}
		
		if ( $("#executorAlias").val() == "" ) {
			showMessageBox( "You must provide an alias before save." );
			$("#executorAlias").focus();
			return;
		}
		$("#stmtField").val( query );
		$("#frmSave").submit();
	}

	function showTargetTable( tblName ) {
		$.ajax({
			type: "GET",
			url: "showtable",
			data: { tableName: tblName }
		}).done(function( table ) {
			$("#targetTableContent").html( table );
		});
	}

	function showSourceTable( tblName ) {
		$.ajax({
			type: "GET",
			url: "showtable",
			data: { tableName: tblName }
		}).done(function( table ) {
			$("#sourceTableContent").html( table );
		});
	}

</script>				
				
<%@ include file="../../footer.jsp" %>
				