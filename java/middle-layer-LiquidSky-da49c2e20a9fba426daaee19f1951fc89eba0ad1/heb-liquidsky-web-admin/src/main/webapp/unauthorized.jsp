<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Admin Web Interface - Unauthorized</title>
	<%@ include file="includes/headIncludes.jsp" %>
</head>

<style>
.ui-bar {
	text-align: center;
}
.ui-body {
	text-align: center;
}

.ui-bar h1{
    font-size: xx-large;
    text-align: center;
}

ul{
	list-style: none;
}
</style>

<body>
	
<div class="ui-corner-all">
	<div class="ui-bar ui-bar-a">
		<h1 class="ui-bar ui-corner-all">Admin Web Interface</h1>
	</div>
	<div class="ui-body ui-body-a ui-corner-all">
		<h1>Unauthorized</h1>
		<p>
			Your failed attempt to access this system has been logged.
		</p>
		<div class="ui-body ui-body-a">
			<ul>
				<li>
					<img src="${userImageUrl}" alt="${userId}" />
				</li>
				<li>
					${userName}
				</li>
				<li>
					${userEmail}
				</li>
				<li>
					${userId}
				</li>
			</ul>
		</div>
	</div>
</div>
	
	<a href="/login" class="ui-btn">
		Log In
	</a>
	
	<a href="/logout" class="ui-btn">
		Log Out
	</a>


</body>
</html>
