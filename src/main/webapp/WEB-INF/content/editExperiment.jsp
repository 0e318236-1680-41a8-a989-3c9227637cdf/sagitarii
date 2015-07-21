<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../../header.jsp" %>

				<div id="leftBox"> 
				
					<div id="bcbMainButtons" class="basicCentralPanelBar">
						<%@ include file="buttons.jsp" %>
					</div>
					
					<div id="basicCentralPanel">
					
						<div class="basicCentralPanelBar">
							<img src="img/workflow.png">
							<div class="basicCentralPanelBarText">Manage Activities for Experiment ${experiment.tagExec} Workflow "${experiment.workflow.description}"</div>
						</div>

						<div class="menuBarMain">
							<img onclick="back()" title="Back" class="button dicas" src="img/back.png"> 
							<img style="display:none;" id="btnSaveAll" onclick="salvarTudo()" title="Save Activities" class="button dicas" src="img/save.png"> 
							<img style="display:none;" id="btnNewActivity" onclick="showInsertBox()" title="New Activity" class="button dicas" src="img/workflow.png"> 
						</div>

						<div id="promoBar" style="height: 220px; display: table; width: 95%; margin: 0 auto">
							<div style="height:450px; position:relative;float:left; width:55%;margin-top:10px;" >
								<img onclick="zoomIn()" class="dicas" title="More Zoom" src="img/zoom-in.png" style="z-index:1000;cursor:pointer;position:absolute;right:10px;top:10px;width:24px;height:20px;opacity:0.5">
								<img onclick="zoomOut()" class="dicas" title="Less Zoom" src="img/zoom-out.png" style="z-index:1000;cursor:pointer;position:absolute;right:35px;top:10px;width:24px;height:20px;opacity:0.5">
								<div style="border:1px solid #dadada; width:100%; height:450px" id="cy"></div>
							</div>


							<div style="height:450px; position:relative;float:right; width:300px;margin-top:10px;" >
								<div id="insertBox" class="userBoard" style="float:right;display: none;overflow: hidden;width:290px;margin:0px">
									<div id="newPannel" style="height:160px; width:95%; margin:0 auto;margin-top:10px;margin-bottom:10px;">
									<div class="userBoardT1" style="text-align:center;width:95%; padding-bottom: 5px;">Insert New Activity</div>
										<table style="margin-top:10px;">
											<tr>
												<td class="tableCellFormLeft">Tag</td>
												<td class="tableCellFormRight"> 
													<input maxlength="10" style="width: 150px;" name="act.tag" id="actTag"  class="tableCellFormInputText" type="text"> 
												</td>
											</tr>
			
											<tr>
												<td class="tableCellFormLeft">Description</td>
												<td class="tableCellFormRight"> 
													<input style="width: 150px;" id="actDescription" name="act.description"  class="tableCellFormInputText" type="text"> 
												</td>
											</tr>
										
											<tr>
												<td class="tableCellFormLeft">Type</td>
												<td class="tableCellFormRight"> 
													<select style="width: 150px;" class="tableCellFormInputCombo" name="act.type" id="actType">
														<option value="MAP">Map</option>
														<option value="REDUCE">Reduce</option>
														<option value="SPLIT_MAP">Split Map</option>
														<option value="SELECT">Select</option>
													</select>
												</td>
											</tr>
			
											<tr>
												<td class="tableCellFormLeft">Wrapper or Criteria</td>
												<td class="tableCellFormRight">
													<select id="cmbWrappers" style="width: 150px;" class="tableCellFormInputCombo"></select>
												</td>
											</tr>
			
			
											<tr>
												<td class="tableCellFormLeft">Consume</td>
												<td class="tableCellFormRight"> 
													<select multiple="false" style="height:120px; width: 150px;" class="tableCellFormInputCombo" id="relInput" name="relInput" >
														<c:forEach items="${tables}" var="table">
															<option value="${table.idTable}">${table.name}</option>
														</c:forEach>
													</select>
												</td>
											</tr>
			
											<tr>
												<td class="tableCellFormLeft">Produce</td>
												<td class="tableCellFormRight"> 
													<select style="width: 150px;" class="tableCellFormInputCombo" id="relOutput" name="relOutput" >
														<option value="-1">-- Produce Relation --</option>
														<c:forEach items="${tables}" var="table">
															<option value="${table.idTable}">${table.name}</option>
														</c:forEach>
													</select>
												</td>
											</tr>
			
			
										</table>
										<div style="min-width: 90px;" onclick="insere()" class="basicButton">Insert</div>							
										<div style="min-width: 90px;display:none;" onclick="cancela()" class="basicButton">Cancel</div>							
									</div>
								</div>
							</div>


							
						</div>


					</div>												
					
				</div>
				<div id="rightBox"> 

					<div class="userBoard" id="actDependencyDetailBox" style="display:none">
						<img id="removeButton" class="dicas" onclick="removeDep();" title="Remove Dependency Connector" style="cursor:pointer; position:absolute;right:10px; width:24px;height:24px;margin-top:4px" src="img/delete.png">
						<div class="userBoardT1" style="text-align:center;width:95%" id="dspDepTag">Dependency Connection</div>
						<div class="userBoardT2" style="text-align:left;width:95%">
							<div class="activityInfo" style="color:#00933B; font-weight:bold" id="dspDepSource"></div>
							<div class="activityInfo" style="color:#F90101; font-weight:bold" id="dspDepTarget"></div>
						</div>
					</div>

					<div class="userBoard" id="tblDetailBox" style="display:none">
						<img id="removeButton" class="dicas" onclick="removeTable();" title="Delete Table" style="cursor:pointer; position:absolute;right:10px; width:24px;height:24px;margin-top:4px" src="img/delete.png">
						<div id="newPannel" style="width:95%; margin:0 auto;margin-top:10px;margin-bottom:10px;">
						<div class="userBoardT1" style="text-align:center;width:95%; padding-bottom: 5px;">Selected Table Info</div>
							<table>
								<tr>
									<td style="width: 30%;" class="tableCellFormLeft">Name</td>
									<td class="tableCellFormRight"> 
										<span id="dspTagSTI"></span> 
									</td>
								</tr>
							</table>
						</div>
					</div>


					<div class="userBoard" id="actDetailBox" style="height: 230px; display:none; position:relative">
						<img id="removeButton" class="dicas" onclick="hideBar();" title="Delete Activity" style="cursor:pointer; position:absolute;right:10px; width:24px;height:24px;margin-top:4px" src="img/delete.png">
						<div id="newPannel" style="height:190px; width:95%; margin:0 auto;margin-top:10px;margin-bottom:10px;">
						<div class="userBoardT1" style="text-align:center;width:95%; padding-bottom: 5px;">Selected Activity Info</div>

							<table>
								<tr>
									<td style="width: 30%;" class="tableCellFormLeft">Tag</td>
									<td class="tableCellFormRight"> 
										<span id="dspTagSAI"></span> 
									</td>
								</tr>

								<tr>
									<td class="tableCellFormLeft">Description</td>
									<td class="tableCellFormRight"> 
										<span id="dspDescription"></span>
									</td>
								</tr>
							
								<tr>
									<td class="tableCellFormLeft">Type</td>
									<td class="tableCellFormRight"> 
										<span id="dspType"></span>
									</td>
								</tr>

								<tr>
									<td class="tableCellFormLeft">Wrapper or Criteria</td>
									<td class="tableCellFormRight"> 
										<span id="dspActivation"></span>
									</td>
								</tr>


								<tr id="joinLine" style="display: none;">
									<td class="tableCellFormLeft">Consume</td>
									<td class="tableCellFormRight"> 
										<span id="dspInputJoin"></span>
									</td>
								</tr>

								<tr id="unoLine">
									<td class="tableCellFormLeft">Consume</td>
									<td class="tableCellFormRight"> 
										<span id="dspInput"></span>
									</td>
								</tr>

								
								<tr>
									<td class="tableCellFormLeft">Produce</td>
									<td class="tableCellFormRight"> 
										<span id="dspOutput"></span>
									</td>
								</tr>

								<tr>
									<td class="tableCellFormLeft">Create Join</td>
									<td class="tableCellFormRight"> 
										<select style="width: 150px;" id="selectConnect" class="tableCellFormInputCombo"></select>
									</td>
								</tr>

							</table>
						</div>
					</div>

					<div id="criteriaContentPanel" class="userBoard" style="height: 150px;display:none">
						<div class="userBoardT1" style="text-align:center;width:95%; padding-bottom: 5px;">Select Criteria SQL Script</div>
						<div id="criteriaContent" class="tableCellFormInputText" style="border:0px; width:97%;margin-top: 3px;"></div>
					</div>
					
					<div class="userBoard" style="height: 150px;display:none;">
						<img onclick="viewImageCanvas()" class="dicas" title="Update Image" src="img/refresh.png" style="cursor:pointer;position:absolute;right:5px;top:0px;width:24px;height:20px;opacity:0.5;">
						<div id="imageCanvas" style="background-color:#FFFFFF;height:100%;width:100%"></div>
					</div>



					<form id="formPost" method="POST" action="saveExperiment">
						<input type="hidden" name="idExperiment" value="${experiment.idExperiment}">
						<textarea style="display:none" id="txtArea" name="actJson"></textarea>
						<textarea style="display:none" id="txtImage" name="imagePreviewData"></textarea>
					</form>
					
				</div>
				
				
<script>

	var wrappers = new Array();
	var wrapper = new Object();
	
	<c:forEach var="criteria" items="${criterias}">
		wrapper = ["${criteria.type}","${criteria.executorAlias}","${criteria.executorAlias}"];		
		wrappers.push( wrapper );
	</c:forEach>

	
	function back() {
		window.location.href = "viewExperiments";
	}
	
	
	function zoomIn() {
		zoomLevel = zoomLevel + 0.1;		
		cy.zoom({
			  level: zoomLevel, // the zoom level
			  position: { x: 0, y: 0 }
		});
		refreshIntervalId = window.setInterval(viewImageCanvas, 500);

	}
	
	function zoomOut() {
		zoomLevel = zoomLevel - 0.1;		
		cy.zoom({
			  level: zoomLevel, // the zoom level
			  position: { x: 0, y: 0 }
		});
		refreshIntervalId = window.setInterval(viewImageCanvas, 500);
		
	}
	
	
	function hideBar() {
		$("#actDetailBox").css("display", "none");
		cy.remove( selectedNode );
		selectedNode = null;
		
		if ( cy.elements('*').size() == 0 ) {
			$("#btnSaveAll").css("display","none");
			$('#relInput').prop('disabled', false);
		}

	}
	
	function removeTable() {
		$("#tblDetailBox").css("display", "none");
		cy.remove( selectedTable );
		selectedTable = null;
	}
	
	function removeDep() {
		$("#actDependencyDetailBox").css("display", "none");
		cy.remove( selectedDependency );
		selectedDependency = null;
		refreshIntervalId = window.setInterval(viewImageCanvas, 500);
	}

	function cancela() {
		$("#insertBox").css("display", "none");
		$("#criteriaContentPanel").css("display","none");
	}
	
	
	function insere() {
		var tag = $("#actTag").val();
		var type = $("#actType").val();
		
		var textColorBlock = '#4D7A93';
		var inputTableColor = '#4D7A93';
		var outputTableColor = '#4D7A93';
		
		if( type == 'SELECT') {
			textColorBlock = '#F90101';
		}
		if( type == 'REDUCE') {
			textColorBlock = '#00933B';
		}
		if( type == 'SPLIT_MAP') {
			textColorBlock = '#F2B50F';
		}
		
		var actActivation = $("#cmbWrappers").val();
		var actDescription = $("#actDescription").val();
		
		var relOutput = $("#relOutput option:selected").html();
		var relOutputVal = $("#relOutput").val();
		
		var relInput = "";
		var relInputVal = "";
		$("#relInput option:selected").each(function() {
			relInput = relInput + this.text + " ";
			relInputVal = relInputVal + this.value + ",";
		});
		// =========================================================
		

		if( (relInputVal == -1) && ( (type != "SELECT") && (type != "REDUCE") )) { 
			showMessageBox("Please select a Consumption Relation for this activity.");
			return;
		}
		if( relOutputVal == -1 ) { 			
			showMessageBox("Please select a Produce Relation for this Activity.");
			return;
		}
		
		var nodeColor = "#F6F6F6";
		
		var testNode = cy.filter('node[id = "'+tag+'"]');
		if ( testNode.data('name') != null ) {
			showMessageBox("This Activity Tag already exists.");
			return;
		}
		
		$("#btnSaveAll").css("display","block");
		//$("#insertBox").css("display", "none");

		$('#actTag').val("");
		$('#actDescription').val("");
		
		
		if ( selectedNode == null ) {
			var eles = cy.add([
              { group: "nodes", data: { inputJoinId : -1, inputJoin : '', outputId : relOutputVal, inputId : relInputVal, output : relOutput, input : relInput, description: actDescription, activation: actActivation, id: tag, name: type, weight: 450, textColor : textColorBlock, faveColor: nodeColor, faveShape: 'rectangle'}, position: { x: 10, y: 10 } }
            ]);
		} 
		
		if( selectedNode != null ) {
			var sourceId = selectedNode.data('id');
			var eles = cy.add([
              { group: "nodes", data: { inputJoinId : -1, inputJoin : '', outputId : relOutputVal, inputId : relInputVal, output : relOutput, input : relInput, description: actDescription, activation: actActivation, id: tag, name: type, weight: 450, textColor : textColorBlock, faveColor: nodeColor, faveShape: 'rectangle' }, position: { x: 10, y: 10 } },
              { group: "edges", data: { source: sourceId, target: tag, faveColor: '#666666', strength: 1 } }
            ]);
		}
		
		// ============ EXIBIR AS TABELAS DE ENTRADA / SAIDA ================
		// Entrada
		$("#relInput option:selected").each(function() {
			var inputTable = this.text;
			var idTag = inputTable;
			var eles = cy.add([
              { group: "nodes", classes: "table", data: { inputJoinId : -1, inputJoin : '', outputId : "", inputId : "", output : "" , input : "", description: inputTable, activation: "", id: idTag , name: "SRCTABLE", weight: 450, textColor : textColorBlock, faveColor: inputTableColor, faveShape: 'ellipse' }, position: { x: 10, y: 10 } },
              { group: "edges", classes: "table", data: { source: idTag, target: tag, faveColor: '#666666', strength: 1 } }
            ]);
		});		

		
		// Saida
		//var idTag = tag + "." + relOutput;
		var idTag = relOutput;
		var eles = cy.add([
          { group: "nodes", classes: "table", data: { inputJoinId : -1, inputJoin : '', outputId : "", inputId : "", output : "" , input : "", description: relOutput, activation: "", id: idTag , name: "TRGTABLE", weight: 450, textColor : textColorBlock, faveColor: outputTableColor, faveShape: 'ellipse' }, position: { x: 10, y: 10 } },
          { group: "edges", classes: "table", data: { source: tag, target: idTag, faveColor: '#666666', strength: 1 } }
        ]);
		// ===================== FIM ============================
		
		isFirstNode = false;
		cy.load( cy.elements('*').jsons() );
		cy.pan({ x: 0, y: 0 });
		refreshIntervalId = window.setInterval(viewImageCanvas, 500);
	}

	
	function salvarTudo() {
		var data = eval( cy.elements('*').jsons() );
		var allNodesAsJson = JSON.stringify( data );
		$("#txtArea").val( allNodesAsJson );
		var cyImage = cy.png( {'bg':'white','full':true,'scale':5} ); 
		$("#txtImage").val( cyImage );
		$("#formPost").submit();
	}

	
	function preLoad() {
		var jsonActivities = '${experiment.activitiesSpecs}';
		if ( jsonActivities != '' ) {
			var jsonObj = JSON.parse( jsonActivities );
			cy.load(jsonObj);
			cy.elements('*').unselect();
			if ( cy.elements('*').size() > 0 ) {
				$("#btnSaveAll").css("display","block");
			} 
		} 
		clearInterval(refreshIntervalId);
		refreshIntervalId = window.setInterval(viewImageCanvas, 500);
	}	

	function viewImageCanvas() {
		var cyImage = cy.png( {'bg':'white','full':true,'scale':5} );
		if ( cyImage != 'data:,') {
		    var image = "<img name='compman' style='border-radius:5px;margin:0px;height: 150px;width:100%' src='"+cyImage+"' />";
		    $("#imageCanvas").html(image);
		    $("#txtImage").val( cyImage );
		}
	    clearInterval(refreshIntervalId);
	}
	
	function showInsertBox() {
		$("#insertBox").css("height", "320px");
		$("#insertBox").css("display", "block");
		$('#actType').trigger('change');
		$('#actTag').val("");
		$('#actDescription').val("");
	}
	
	function filterWrappers( type ) {
		
		$("#cmbWrappers").find('option').remove();
		for(var x=0; x<wrappers.length; x++  ) {
			if( ( wrappers[x][0] == type ) || ( (type=="SPLIT_MAP") && ( wrappers[x][0] == "RSCRIPT") ) || ( (type=="MAP") && ( wrappers[x][0] == "RSCRIPT") ) || ( (type=="REDUCE") && ( wrappers[x][0] == "RSCRIPT") ) ) {
				$("#cmbWrappers").append(
					$('<option></option>').val( wrappers[x][1] ).html( wrappers[x][2] )
				);
			}
		}
	}
	
	function getCriteriaScript( alias ) {
		
		$("#criteriaContent").text();
		$.ajax({
			type: "GET",
			url: "getExecutorScript",
			data: { executorAlias: alias }
		}).done(function( script ) {
			if ( script != "" ) {
				$("#criteriaContentPanel").css("display","table");
			} else {
				$("#criteriaContentPanel").css("display","none");
			} 
			$("#criteriaContent").text( script );
		});
	}

	
	$(document).ready(function() {
		refreshIntervalId = window.setInterval(preLoad, 500);

		$("#actType").change( function(){
			$("#criteriaContentPanel").css("display","none");
			var wrapperType = $("#actType").val();
			filterWrappers( wrapperType );
			
			if( (wrapperType == "MAP") || (wrapperType == "SPLIT_MAP")) {
				$("#relInput").removeAttr("multiple");
				$("#relInput").css("height","");
			} else {
				$("#relInput").attr("multiple", "multiple");
				$("#relInput").css("height","120px");
			}

			if( (wrapperType == "SELECT") || (wrapperType == "REDUCE") ) {
				var executorAlias = $("#cmbWrappers").val();
				getCriteriaScript( executorAlias );
			}
		});

		$("#cmbWrappers").change( function() {
			var wrapperType = $("#actType").val();
			if( (wrapperType == "SELECT") || (wrapperType == "REDUCE") ) {
				var executorAlias = $(this).val();
				getCriteriaScript( executorAlias );				
			}
		});

		filterWrappers( $("#actType").val() );		
		
		$("#selectConnect").change( function(){
			var idTarget = $("#selectConnect").val();
			if( idTarget == "-1" ) {
				return;
			}
			
			var idSource = selectedNode.data('id');
			
			outTable = selectedNode.data('output');
			outTableId = selectedNode.data('outputId');

			var eles = cy.add([
				{ group: "edges", data: { source: idSource, target: idTarget, faveColor: '#666666', strength: 1 } }
			]);
			
			
			
			var inTables = ""; var inTablesId = "";
			$.each( cy.filter('edge'), function(){
				if ( this.data('target') == idTarget ) {
					var sourceJoinNome = cy.elements("node[id='"+this.data('source')+"']");
					inTables = inTables + sourceJoinNome.data("output") + " ";
					inTablesId = inTablesId + sourceJoinNome.data("outputId") + ",";
				}
			});

			
			var targetNode = cy.elements("node[id='"+idTarget+"']");
			
			var oldInput = targetNode.data('input');
			var oldInputId = targetNode.data('inputId');
			
			targetNode.data('inputJoin', inTables);
			targetNode.data('inputJoinId', inTablesId);
			
		});
		
		showInsertBox();
		
	});

</script>				
				
<%@ include file="../../footer.jsp" %>
				