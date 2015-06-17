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

<div class="userBoard">
	<div class="userBoardT1" style="text-align:center;width:95%">Virtual Machine Status</div>
	<div class="userBoardT2" style="text-align:center;width:95%">
		<table>
			<tr>
				<td>VM RAM</td>
				<td>
					<div class="clusterCpuOut" style="width:90px">
						<div class="clusterCpuIn" style="width:${memoryPercent}%">&nbsp;</div>
					</div> 
				</td>
				<td>${memoryPercent}% / ${totalMemory}Mb</td>
			</tr>
			<tr>
				<td colspan="3"><img style="width:210px;height:110px;margin:0px;padding:0px;" src="getMetrics?metricName=Memory"></td>
			</tr>
			<tr>
				<td>CPU</td>
				<td>
					<div class="clusterCpuOut" style="width:90px">
						<div class="clusterCpuIn" style="background-color:#0266C8; width:${cpuLoad}%">&nbsp;</div>
					</div> 
				</td>
				<td>${cpuLoad}%</td>
			</tr>
			<tr> 
				<td colspan="3"><img style="width:210px;height:110px;margin:0px;padding:0px;" src="getMetrics?metricName=CPU"></td>
			</tr>
		</table>
	</div>
</div>
