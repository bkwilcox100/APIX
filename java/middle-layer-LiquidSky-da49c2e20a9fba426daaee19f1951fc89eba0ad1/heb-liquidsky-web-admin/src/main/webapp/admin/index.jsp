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

<%@ include file="includes/userBar.jsp" %>
<h1 id="MainHeading">Admin Web Interface - Home</h1>

<%@ include file="includes/mainMenu.jsp" %>
<div id="HomeView" class="bodyContainer">
	<p>
		Welcome to the Admin Web interface for HEB Middle Layer Services.  All activity is logged.
	</p>
	<%@ include file="includes/mainMenuContents.jsp" %>
</div>
<%@ include file="includes/sharedFooter.jsp" %>
</body>
</html>
