<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../../header.jsp" %>

				<div id="leftBoxAlter" style="width:100%; border-right:0px" > 
				
					<div class="userBoard" style="margin:0 auto;margin-top:50px; width: 300px;">
						<div class="userBoardT1" style="text-align:center;width:95%">Request New User Credentials</div>
						<div class="userBoardT2" style="text-align:center;width:95%">
							<form action="doRequestAccess" method="post" name="formRequest" id="formRequest">
								<table>
									<tr>
										<td style="width:30%">Login Name</td><td><input autocomplete="off" id="username" type="text" name="username"></td>
									</tr>
									<tr>
										<td>Full Name</td><td><input autocomplete="off" id="fullName" type="text" name="fullName"></td>
									</tr>
									<tr>
										<td>Mail Address</td><td><input autocomplete="off" id="mailAddress" type="text" name="mailAddress"></td>
									</tr>
									<tr>
										<td>Password</td><td><input autocomplete="off" id="password" type="password" name="password"></td>
									</tr>
									<tr>
										<td>Retype Password</td><td><input autocomplete="off" id="retypePassword" type="password" name="retypePassword"></td>
									</tr>
									<tr>
										<td>&nbsp;</td><td><div style="margin-right: 10px;margin-top: 0px;" class="basicButton" onclick="doRequest()">Request</div></td>
									</tr>
								</table>
							</form>
							<span>Your request will be sent to an Administrator and may take some time to process.
							The notification will be sent to the mail address informed here as soon an Administrator accept your request.</span>
						</div>
					</div>
					
					
				</div>
								
				<br><br>
				
<script>

	function doRequest() {
		var password = $("#password").val();
		var retypePassword = $("#retypePassword").val();
		var username = $("#username").val();
		var mailAddress = $("#mailAddress").val();
		var fullName = $("#fullName").val();

		if ( (fullName == '') || (mailAddress == '') || (password == '') || ( username == '' ) ) {
			showMessageBox('Please fill all required fields.');
			return;
		} 

		if ( password != retypePassword) {
			showMessageBox('Passwords do not match');
			return;
		}
		
		$("#formRequest").submit();
	}

	$(document).ready(function() {
		$("#username").focus();
	});
	
</script>				
				
				<script>
					showMessageBox( '${messageText}' );
				</script>				

				<div class="clear" />
			</div>
			
		</div>

		<div class="clear" />
		<div id="bottomBar" style="height:90px">
			
			<div class="footerDivCenter" style="position: relative;margin-top:5px;">
				<a class="dicas" title="CEFET-RJ" target="_BLANK" href="http://eic.cefet-rj.br/portal/"><img src="img/logos/cefet_badge.png" class="badge"></a>
				<a class="dicas" title="Cytoscape.js" target="_BLANK" href="http://cytoscape.github.io/cytoscape.js/"><img src="img/logos/cytoscape_badge.png" class="badge"></a>
				<a class="dicas" title="Download Code" target="_BLANK" href="https://github.com/eic-cefet-rj"><img src="img/logos/git_badge.png" class="badge"></a>
				<a class="dicas" title="Code Mirror" target="_BLANK" href="http://codemirror.net/"><img src="img/logos/codemirror_badge.png" class="badge"/></a>
				<a class="dicas" title="jQuery" target="_BLANK" href="https://jquery.com/"><img src="img/logos/jquery.png" class="badge"/></a>
				<a class="dicas" title="Hibernate" target="_BLANK" href="http://hibernate.org/"><img src="img/logos/hibernate_badge.png" class="badge"/></a>
				<a class="dicas" title="Apache Log4j" target="_BLANK" href="http://logging.apache.org/log4j/2.x/"><img src="img/logos/log4j_badge.png" class="badge"/></a>
				<a class="dicas" title="PostgreSQL" target="_BLANK" href="http://www.postgresql.org/"><img src="img/logos/postgres_badge.png" class="badge"/></a>
				<a class="dicas" title="Java" target="_BLANK" href="http://www.oracle.com/"><img src="img/logos/java_badge.png" class="badge"/></a>
				<a class="dicas" title="Eclipse Luna" target="_BLANK" href="https://www.eclipse.org/luna/"><img src="img/logos/eclipse_badge.png" class="badge"/></a>
			</div> 
			
			
			<div class="footerDivCenter" style="font-style: italic;padding-top:5px">
				"Together we're stronger"<br>
				v1.0.345
			</div> 
			
		</div>
		
	</body>
	
</html>