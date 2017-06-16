<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Admin Web Interface - App Properties</title>
	<%@ include file="includes/headIncludes.jsp" %>
</head>

<body>
<%@ include file="includes/mainMenu.jsp" %>
<%@ include file="includes/userBar.jsp" %>

<div id="AppPropertiesView" class="bodyContainer">
	<h2>App Properties Configuration</h2>
	<%@ include file="includes/appPropertiesPage.jsp" %>	
</div>

<%@ include file="includes/sharedFooter.jsp" %>
</body>
</html>
