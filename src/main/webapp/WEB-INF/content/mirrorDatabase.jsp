<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../../header.jsp" %>

				<div id="leftBoxAlter" style="width:100%; border-right:0px" > 

					<div class="userBoard" style="margin:0 auto;margin-top:50px; width: 300px;">
						<div class="userBoardT2" style="text-align:center;width:95%">
							<div class="userBoardT1" style="text-align:center;width:95%">Mirror Database</div>
							<form action="doMirrorDatabase" method="post" name="formRequest" id="formRequest">
								<div class="userBoardT1" style="text-align:center;width:95%">Source Database</div>
							
								<table>
									<tr>
										<td style="width:30%">Host</td><td><input autocomplete="off" id="sourceHost" type="text" name="sourceHost"></td>
									</tr>
									<tr>
										<td>Database Name</td><td><input autocomplete="off" id="sourceDb" type="text" name="sourceDb"></td>
									</tr>
									<tr>
										<td>Login Name</td><td><input autocomplete="off" id="sourceUser" type="text" name="sourceUser"></td>
									</tr>
									<tr>
										<td>Password</td><td><input autocomplete="off" id="sourcePassword" type="text" name="sourcePassword"></td>
									</tr>
									<tr>
										<td>Port</td><td><input autocomplete="off" id="sourcePort" type="text" name="sourcePort"></td>
									</tr>
								</table>

								<div class="userBoardT1" style="text-align:center;width:95%">Target Database</div>


								<table>
									<tr>
										<td style="width:30%">Host</td><td><input autocomplete="off" id="targetHost" type="text" name="targetHost"></td>
									</tr>
									<tr>
										<td>Database Name</td><td><input autocomplete="off" id="targetDb" type="text" name="targetDb"></td>
									</tr>
									<tr>
										<td>Login Name</td><td><input autocomplete="off" id="targetUser" type="text" name="targetUser"></td>
									</tr>
									<tr>
										<td>Password</td><td><input autocomplete="off" id="targetPassword" type="text" name="targetPassword"></td>
									</tr>
									<tr>
										<td>Port</td><td><input autocomplete="off" id="targetPort" type="text" name="targetPort"></td>
									</tr>
									<tr>
										<td>&nbsp;</td><td><div style="margin-right: 10px;margin-top: 0px;" class="basicButton" onclick="doMirror()">Copy</div></td>
									</tr>
								</table>


							</form>
							<span>****</span>
						</div>
					</div>
					
					
				</div>
								
				<br><br>
				
<script>

	function doMirror() {
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