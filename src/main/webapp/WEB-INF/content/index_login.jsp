<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../../header.jsp" %>

				<div id="leftBoxAlter" style="width:100%; border-right:0px" > 
				
					<img src="img/logos/wallpaper.png" style="width:400px; height:300px;display:block;margin:0 auto;margin-top:0px">
				
				
					<div class="userBoard" style="margin:0 auto;margin-top:5px">
						<div class="userBoardT1" style="text-align:center;width:95%">Login</div>
						<div class="userBoardT2" style="text-align:center;width:95%">
							<form action="doLogin" method="post" name="formLogin" id="formLogin">
								<table>
									<tr>
										<td style="width:50%">Username</td><td><input autocomplete="off" id="username" type="text" name="username"></td>
									</tr>
									<tr>
										<td style="width:50%">Password</td><td><input autocomplete="off" id="password" type="password" name="password"></td>
									</tr>
									<tr>
										<td style="width:50%">&nbsp;</td><td><div style="margin-right: 7px;margin-top: 0px;" class="basicButton" onclick="doLogin()">Login</div></td>
									</tr>
								</table>
							</form>
						</div>
					</div>
				</div>
								
				
<script>

	function doLogin() {
		var password = $("#password").val();
		var username = $("#username").val();
		if ( (password == '') || ( username == '' ) ) {
			showMessageBox('Please fill all required fields.');
			return;
		} 
		$("#formLogin").submit();
	}

	$(document).ready(function() {
		$("#username").focus();
		
		$("#password").keypress(function(event) {
		    if (event.which == 13) {
		        event.preventDefault();
		        doLogin();
		    }
		});

	});
	
</script>				
				
				<script>
					showMessageBox( '${messageText}' );
				</script>				

				<div class="clear" />
			</div>
			
		</div>

		<div class="clear" />
		<div id="bottomBar" style="height:35px">
			<div class="footerDivCenter" style="font-style: italic;padding-top:10px">
				"Together we're stronger"<br>
				v1.0.345
			</div> 
		</div>
		
	</body>
	
</html>