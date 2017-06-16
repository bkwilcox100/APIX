<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Admin Web Interface</title>
	<%@ include file="includes/headIncludes.jsp" %>
</head>

<style>
.custom-corners .ui-bar {
	-webkit-border-top-left-radius: inherit;
	border-top-left-radius: inherit;
	-webkit-border-top-right-radius: inherit;
	border-top-right-radius: inherit;
	text-align: center;
}
.custom-corners .ui-body {
	border-top-width: 0;
	-webkit-border-bottom-left-radius: inherit;
	border-bottom-left-radius: inherit;
	-webkit-border-bottom-right-radius: inherit;
	border-bottom-right-radius: inherit;
	text-align: center;
}

.ui-bar h1{
    font-size: xx-large;
    text-align: center;
}
</style>

<body>
	
<div class="ui-corner-all custom-corners">
	<div class="ui-bar ui-bar-a">
		<h1 class="ui-bar ui-corner-all">Admin Web Interface</h1>
	</div>
	<div class="ui-body ui-body-a">
		<h2>Authorized Users Only</h2>
		<p> 
			This is a protected system.  All activity is logged.
		</p>
	</div>
</div>
	
	<a href="/login" class="ui-btn">
		Log In
	</a>
	

</body>
</html>
