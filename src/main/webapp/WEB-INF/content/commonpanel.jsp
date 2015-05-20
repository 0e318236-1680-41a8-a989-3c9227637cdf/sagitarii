<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<div class="userBoard">
	<div class="userBoardT1" style="text-align:center;width:95%">Logged User</div>
	<div class="userBoardT2" style="text-align:center;width:95%">
		<table>
			<tr>
				<td style="text-align:center">${loggedUser.fullName}</td>
			</tr>
			<tr>
				<td style="text-align:center">${loggedUser.userMail}</td>
			</tr>
		</table>
	</div>
</div>

