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
	<div class="userBoardT1" style="text-align:center;width: 225px;">Sagitarii Status</div>
	<div class="userBoardT2" style="text-align:center;width: 225px;">
		<table>
			<tr>
				<td>Dynamic Load Balancer<td>${useDLB}</td>
			</tr>
			<tr>
				<td>Running<td>${fn:length(runningExperiments)}</td>
			</tr>
			<tr>
				<td>Total VM Memory</td><td>${totalMemory}Mb</td>
			</tr>		
			<tr>
				<td>System Speedup</td><td>${systemSpeedUp}</td>
			</tr>		
			<tr>
				<td>System Efficiency</td><td>${systemEfficiency}</td>
			</tr>		
		</table>
	</div>
</div>


<div class="userBoard">
	<div class="userBoardT1" style="text-align:center;width: 225px;">Virtual Machine Status</div>
	<div class="userBoardT2" style="text-align:center;width: 225px;">
		<table>
			<tr>
				<td colspan="3"><img class="dicas" title="Free Heap Memory %" id="imgRam" style="width:210px;height:40px;margin:0px;padding:0px;" src="metrics/Memory.png"></td>
			</tr>
			<tr> 
				<td colspan="3"><img class="dicas" title="CPU Load" id="imgCpu" style="width:210px;height:40px;margin:0px;padding:0px;" src="metrics/CPU.png"></td>
			</tr>
		</table>
	</div>
</div>

<script>

	function reloadImage2() {
		d = new Date();
		$("#imgRam").attr("src", "metrics/Memory.png?" + d.getTime() );
	}
	
	function reloadImage1() {
		d = new Date();
		$("#imgCpu").attr("src", "metrics/CPU.png?" + d.getTime() );
	}

	$(document).ready(function() {
		window.setInterval(reloadImage1, 4200);
		window.setInterval(reloadImage2, 4100);
	});
	
</script>