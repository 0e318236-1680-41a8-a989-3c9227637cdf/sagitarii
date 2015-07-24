<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<div class="userBoard">
	<div class="userBoardT1" style="text-align:center;width: 225px;">Logged User</div>
	<div class="userBoardT2" style="text-align:center;width: 225px;">
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

<div class="userBoard">
	<div class="userBoardT1" style="text-align:center;width: 225px;">Virtual Machine Status</div>
	<div class="userBoardT2" style="text-align:center;width: 225px;">
		<table>
			<tr>
				<td>Total Memory</td>
				<td>${totalMemory}Mb</td>
			</tr>
			<tr>
				<td colspan="3"><img class="dicas" title="Free Heap Memory %" id="imgRam" style="width:210px;height:110px;margin:0px;padding:0px;" src="getMetrics?metricName=Memory"></td>
			</tr>
			<tr> 
				<td colspan="3"><img class="dicas" title="CPU Load" id="imgCpu" style="width:210px;height:110px;margin:0px;padding:0px;" src="getMetrics?metricName=CPU"></td>
			</tr>
		</table>
	</div>
</div>

<script>

	function reloadImage2() {
		d = new Date();
		$("#imgRam").attr("src", "getMetrics?metricName=Memory&time=XYZ"+d.getTime());
	}
	
	function reloadImage1() {
		d = new Date();
		$("#imgCpu").attr("src", "getMetrics?metricName=CPU&time=ABC"+d.getTime());
	}

	$(document).ready(function() {
		window.setInterval(reloadImage1, 4200);
		window.setInterval(reloadImage2, 4000);
	});
	
</script>