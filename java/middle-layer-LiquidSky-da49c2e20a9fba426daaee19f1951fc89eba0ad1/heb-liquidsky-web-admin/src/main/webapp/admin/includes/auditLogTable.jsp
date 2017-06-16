<script>
$(document).ready(function(){
	populateAuditLogTable();
})
</script>

<span id="refreshAuditLogTableButton" class="adminButton refreshButton">
	Refresh
</span>
<table id="AuditLogTable">
	<thead>
		<tr>
			<%-- Table Header Row --%>
			<td class="logTableHeader">
				<span> changeId </span>
			</td>
			<td class="logTableHeader">
				<span> dataItemType </span>
			</td>
			<td class="logTableHeader">
				<span> userId </span>
			</td>
			<td class="logTableHeader">
				<span> ItemId </span>
			</td>
			<td class="logTableHeader">
				<span> operation </span>
			</td>
			<td class="logTableHeader">
				<span> lastModifiedDate </span>
			</td>
			<td class="logTableHeader">
				<span> creationDate </span>
			</td>
			<td class="logTableHeader">
				<span> jsonResponse </span>
			</td>
		</tr>
	</thead>
	
	<%-- Table Body.  this will be populated with JS on document ready --%>
	<tbody>
	</tbody>
</table>

<template id="AuditLogEntryTemplate">
	<tr>
		<td class="logTD">
			<span class="ale_changeId"> {{changeId}} </span>
		</td>
		<td class="logTD">
			<span class="ale_dataItemType"> {{dataItemType}} </span>
		</td>
		<td class="logTD">
			<span class="ale_userId"> {{userId}} </span>
		</td>
		<td class="logTD">
			<span class="ale_ItemId"> {{itemId}} </span>
		</td>
		<td class="logTD">
			<span class="ale_operation"> {{operation}} </span>
		</td>
		<td class="logTD">
			<span class="ale_lastModifiedDate"> {{lastModifiedDate}} </span>
		</td>
		<td class="logTD">
			<span class="ale_creationDate"> {{creationDate}} </span>
		</td>
		<td class="logTD">
			<span class="ale_jsonResponse"> {{jsonResponse}} </span>
		</td>	
	</tr>
</template>