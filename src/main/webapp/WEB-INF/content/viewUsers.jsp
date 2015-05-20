<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../../header.jsp" %>

				<div id="leftBox" > 
				
					<div id="bcbMainButtons" class="basicCentralPanelBar" style="height:50px">
						<%@ include file="buttons.jsp" %>
					</div>
					
					<div id="basicCentralPanel">
					
						<div class="basicCentralPanelBar">
							<img src="img/users.png">
							<div class="basicCentralPanelBarText">Manage Users</div>
						</div>
						
						
						<div class="menuBarMain">
							<img onclick="showNewPannel();" title="New User" class="button dicas" src="img/add.png">
						</div>


						<div id="newPannel" style="display:none; height:180px; width:95%; margin:0 auto;margin-top:10px;margin-bottom:10px;">
							<form action="doNewUser" method="POST" id="formPost">
								<table>
									<tr>
										<td class="tableCellFormLeft">Full Name</td>
										<td class="tableCellFormRight"> 
											<input id="fullName" name="user.fullName" class="tableCellFormInputText" type="text"> 
										</td>
									</tr>

									<tr>
										<td class="tableCellFormLeft">Login Name</td>
										<td class="tableCellFormRight"> 
											<input id="loginName" name="user.loginName" class="tableCellFormInputText" type="text"> 
										</td>
									</tr>
									<tr>
										<td class="tableCellFormLeft">eMail</td>
										<td class="tableCellFormRight"> 
											<input id="email" name="user.userMail" class="tableCellFormInputText" type="text"> 
										</td>
									</tr>
									<tr>
										<td class="tableCellFormLeft">Password</td>
										<td class="tableCellFormRight"> 
											<input id="password" name="user.password" class="tableCellFormInputText" type="password"> 
											<input id="passwordRetype" class="tableCellFormInputText" type="password"> 
										</td>
									</tr>
									<tr>
										<td class="tableCellFormLeft">User Type</td>
										<td class="tableCellFormRight">
											<select name="user.type" class="tableCellFormInputCombo">
												<option value="ADMIN">Admin</option>
												<option value="COMMON">Common User</option>
											</select>  
										</td>
									</tr>

								</table>
							</form>
							<div onclick="doPost()" class="basicButton">Send</div>							
							<div onclick="cancelNewPanel()" class="basicButton">Cancel</div>							
						
						</div>


						<div style="margin : 0 auto; width : 95%; margin-top:10px;" id="dtTableContainer">
							<table class="tableForm"  id="example" >
								<thead>
									<tr>
										<th>Full Name</th>
										<th>Login Name</th>
										<th>eMail</th>
										<th>Type</th>
										<th>&nbsp;</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="user" items="${userList}">
										<tr>
											<td>${user.fullName}</td>
											<td>${user.loginName}</td>
											<td>${user.userMail}</td>
											<td>${user.type}</td>
											<td>
												<c:if test="${user.idUser != loggedUser.idUser}">
													<img class="miniButton dicas" title="Delete" onclick="deleteUser('${user.idUser}','${user.fullName}')" src="img/delete.png">
												</c:if>
												<img class="miniButton dicas" title="Edit" onclick="editUser('${user.idUser}')" src="img/edit.png">
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

	function doPost() {
		var userName = $("#loginName").val();
		var fullName = $("#fullName").val();
		var email = $("#email").val();
		var password = $("#password").val();
		var passwordRetype = $("#passwordRetype").val();
		if ( (userName == '') || ( fullName == '' ) || ( email == '' ) || ( password == '' ) ) {
			showMessageBox('Please fill all fields.');
			return;
		} 
		
		if ( password != passwordRetype ) {
			$("#password").focus();
			showMessageBox("The password fields not matches.");
			return false;
		}
		
		if ( userName.indexOf(' ') >= 0 ) {
			$("#loginName").focus();
			showMessageBox("The login name cannot have white spaces.");
			return false;
		}
		
		if ( email.indexOf(' ') >= 0 ) {
			$("#email").focus();
			showMessageBox("The email cannot have white spaces.");
			return false;
		}		
		
		$("#formPost").submit();
	}

	
	function deleteUser( idUser, name ) {
		showDialogBox( "Delete user "+name+" ?", "deleteUser?idUser=" + idUser );
	}

	function editUser( idUser ) {
		window.location.href="editUser?idUser=" + idUser;
	}
	
	function cancelNewPanel() {
		$("#newPannel").css("display","none");
	}
	
	function showNewPannel() {
		$("#newPannel").css("display","block");
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
						  { "sWidth": "40%" },
						  { "sWidth": "20%" },
						  { "sWidth": "20%" },
						  { "sWidth": "10%" },
						  { "sWidth": "10%" }]						
		} ).fnSort( [[0,'desc']] );
	} );	
	
</script>				
				
<%@ include file="../../footer.jsp" %>
				