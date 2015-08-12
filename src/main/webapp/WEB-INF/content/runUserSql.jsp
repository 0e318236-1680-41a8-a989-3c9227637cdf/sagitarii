<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../../header.jsp" %>

				<div id="leftBox"> 
					<div id="bcbMainButtons" class="basicCentralPanelBar">
						<%@ include file="buttons.jsp" %>
					</div>
					
					<div id="basicCentralPanel">
					
						<div class="basicCentralPanelBar">
							<img src="img/experiment.png">
							<div class="basicCentralPanelBarText">Interactive SQL</div>
						</div>
						
						<div class="menuBarMain">
							<img onclick="back();" title="Back"	class="button dicas" src="img/back.png">
						</div>
						
						<form method="post" action="runUserSql" id="frmSave">
							<input type="hidden" id="sqlQuery" name="sql">
						</form>						
						
						<div class="menuBarMain" style="display:table;height:52px;margin-top:5px">
							<div style="float:left; width:85%">
								<table style="margin-top:10px;margin-left:10px" >
									<tr>
										<td>
											<div class="menuBarMain" style="height:100px;margin-bottom:5px;font-size:11px !important;width:99%;">
												<textarea style="border:0px;height:140px" id="code" name="code">${sql}</textarea>
											</div>
										</td>
									</tr>	
								</table>
							</div>
							<div style="float:right; width:90px;margin-right:10px;padding-top:6px;">
								<img style="float:right;" alt="" onclick="run();" title="Execute Query" class="button dicas" src="img/start.png" />
							</div>
							
						</div>							

						<div class="basicCentralPanelBar">
							<img src="img/right.png">
							<div class="basicCentralPanelBarText">SQL Result</div>
						</div>

						
						<div   style="width:95%; margin:0 auto;margin-top:10px;margin-bottom:10px">
							<table style="width:99%" class="tableForm" id="example" >
								<thead>
								<c:forEach var="column" items="${result.toArray()[0].columnNames}">
									<th>${column}&nbsp;</th>
								</c:forEach>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
						
					</div>												
					
					
				</div>
				<div id="rightBox"> 
					<%@ include file="commonpanel.jsp" %>
				</div>
				
				
<script>

	function run() {
		var query = codeMirrorEditor.getDoc().getValue();
		$("#sqlQuery").val( query );
		$("#frmSave").submit();
	}

	function decorateRow(row) {
	    $(row).children().each(function(index, td){
           var data = $(td).text();
           var decodedData = window.atob( data );
           $(td).html( decodedData );
	    });
	}

	$(document).ready(function() {
		
		
		codeMirrorEditor = CodeMirror.fromTextArea(document.getElementById("code"), { 
			mode: "text/x-sql", 
			indentWithTabs: true,
			smartIndent: true,
			matchBrackets : true,
			readOnly: false,
			lineNumbers: true,
			lineWrapping:true
        });
		
		
		
		
		$('#example').dataTable({
	        "oLanguage": {
	            "sUrl": "js/pt_BR.txt"
	        },	
			
	        "bLengthChange": false,
			"sPaginationType": "full_numbers",	
			"iDisplayLength" : 25,	
			"bAutoWidth" : false,
			"sScrollX": "100%",
	        "bProcessing": true,
	        "bServerSide": true,
	        "sAjaxSource" : "userSqlAjaxProcess?sql=${sql}", 
	        "sServerMethod": "POST",
	        "aoColumns": [
       			<c:forEach var="column" items="${result.toArray()[0].columnNames}">
       			{ "mDataProp": "${column}" },
       			</c:forEach>
	        ],

	        "fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
	            decorateRow( nRow );
                return nRow;
	        },
	        
	        "fnServerData": function ( sSource, aoData, fnCallback, oSettings ) {
	        	<c:forEach var="column" items="${result.toArray()[0].columnNames}">
		        	aoData.push( { "name": "columns", "value": "${column}" } );
	        	</c:forEach>
	        	oSettings.jqXHR = $.ajax({
	              "dataType": 'json',
	              "type": "POST",
	              "url": sSource,
	              "data": aoData,
	              "success": fnCallback
	         	}); 
	         },
	         
	        "fnInitComplete": function(oSettings, json) {
	        	doComplete();
	        }
		});	
		
	});
	
	function doComplete() {
		doTableComplete();
		$("#example_wrapper .dataTable").each(function() {
			$(this).css("padding-top","0px");
			$(this).css("margin-bottom","0px");
		});
		$(".dataTables_scroll").css("margin-bottom","10px");
		$(".dataTables_scroll").css("padding-top","10px");
	}

			
	function back() {
		//
	}	
	
</script>				
				
<%@ include file="../../footer.jsp" %>
				