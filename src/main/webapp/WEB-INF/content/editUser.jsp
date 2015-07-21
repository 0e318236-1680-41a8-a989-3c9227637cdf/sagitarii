<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../../header.jsp" %>

				<div id="leftBox" > 
				
					<div id="bcbMainButtons" class="basicCentralPanelBar">
						<%@ include file="buttons.jsp" %>
					</div>
					
					<div id="basicCentralPanel">
					
						<div class="basicCentralPanelBar">
							<img src="img/users.png">
							<div class="basicCentralPanelBarText">Edit User</div>
						</div>
						
						
						<div id="newPannel" style="height:180px; width:95%; margin:0 auto;margin-top:10px;margin-bottom:10px;">
							<form action="doEditUser" method="POST" id="formPost">
							<input value="${user.idUser}" name="user.idUser" type="hidden">
								<table>
									<tr>
										<td class="tableCellFormLeft">Full Name</td>
										<td class="tableCellFormRight"> 
											<input value="${user.fullName}" id="fullName" name="user.fullName" class="tableCellFormInputText" type="text"> 
										</td>
									</tr>

									<tr>
										<td class="tableCellFormLeft">Login Name</td>
										<td class="tableCellFormRight"> 
											<input value="${user.loginName}" id="loginName" name="user.loginName" class="tableCellFormInputText" type="text"> 
										</td>
									</tr>
									<tr>
										<td class="tableCellFormLeft">eMail</td>
										<td class="tableCellFormRight"> 
											<input value="${user.userMail}" id="email" name="user.userMail" class="tableCellFormInputText" type="text"> 
										</td>
									</tr>
									<tr>
										<td class="tableCellFormLeft">Password<br>
										<i>Leave blank if not change</i></td>
										<td class="tableCellFormRight"> 
											<input value="${user.password}" id="password" name="user.password" class="tableCellFormInputText" type="password"> 
										</td>
									</tr>
									
									<c:if test="${user.idUser == loggedUser.idUser}">
										<tr>
											<td class="tableCellFormLeft">User Type</td>
											<td class="tableCellFormRight">
												${user.type}
												<input value="${user.type}" name="user.type" type="hidden">	
											</td>
										</tr>
									</c:if>
									
									<c:if test="${user.idUser != loggedUser.idUser}">
										<tr>
											<td class="tableCellFormLeft">User Type</td>
											<td class="tableCellFormRight">
												<select name="user.type" class="tableCellFormInputCombo">
													<option <c:if test="${user.type == 'ADMIN'}">selected="selected"</c:if> value="ADMIN">Admin</option>
													<option <c:if test="${user.type == 'COMMON'}">selected="selected"</c:if>value="COMMON">Common User</option>
												</select>  
											</td>
										</tr>
									</c:if>

								</table>
							</form>
							<div onclick="doPost()" class="basicButton">Send</div>							
							<div onclick="cancelNewPanel()" class="basicButton">Cancel</div>							
						
						</div>


					</div>												
					
				</div>
				
				<div id="rightBox"> 
					<%@ include file="commonpanel.jsp" %>
				</div>
				
				
<script>

	function doPost() {
		var userName = $("#userName").val();
		var fullName = $("#fullName").val();
		var email = $("#email").val();
		if ( (userName == '') || ( fullName == '' ) || ( email == '' ) ) {
			showMessageBox('Please fill all fields.');
			return;
		} 
		$("#formPost").submit();
	}
	
	function cancelNewPanel() {
		window.location.href="viewUsers";
	}

</script>				
				
<%@ include file="../../footer.jsp" %>
				