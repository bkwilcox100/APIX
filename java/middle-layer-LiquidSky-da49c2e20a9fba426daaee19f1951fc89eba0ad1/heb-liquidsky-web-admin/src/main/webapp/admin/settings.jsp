<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Admin Web Interface - Home</title>
	<%@ include file="includes/headIncludes.jsp" %>
</head>

<body>

<script>
$(document).ready(function(){
	loadAdminWebProperties();
	populateAdminWebPropertiesForm();

	$("#saveAdminWebProperties").click(function() {
		event.preventDefault();
		saveAdminWebProperties();
		$("#savedAdminWebProperties").show();
		$("#savedAdminWebProperties").fadeOut(2000);
		return false;
	});
})

function populateAdminWebPropertiesForm(){
	$("#saveApiKeyInput").val(adminWebProperties.apiKey);
	$("#saveHostInput").val(adminWebProperties.restHost);
	$("#saveProtocolInput").val(adminWebProperties.restHostProtocol);
}

function saveAdminWebProperties(){
	var jsonObject = {};
	$("#adminWebPropertiesForm").find("input").each(function(index) {
		jsonObject[$(this).attr("name")] = $(this).val();
	});
	savedAdminWebProperties(jsonObject);
	loadAdminWebProperties();
}

</script>

<%@ include file="includes/userBar.jsp" %>
<h1 id="MainHeading">Admin Web Interface - Settings</h1>

<%@ include file="includes/mainMenu.jsp" %>

<div id="SettingsContainer" class="bodyContainer">
	<form id="adminWebPropertiesForm" name="adminWebProperties" class="alignedForm">
	
		<label for="saveApiKeyInput"> API Key </label>
		<input id="saveApiKeyInput" name="apiKey" value="" /><br/>
		
		<label for="saveHostInput"> REST Host </label>
		<input id="saveHostInput" name="host" value="" /><br/>
		
		<label for="saveProtocolInput"> REST Host Protocol (http or https) </label>
		<input id="saveProtocolInput" name="protocol" value="" /><br/>
		
		<button id="saveAdminWebProperties">Save Admin Web Settings</button>
		<span id="savedAdminWebProperties" style="display: none;">Saved...</span>
	</form>
</div>

<%@ include file="includes/sharedFooter.jsp" %>
</body>
</html>
