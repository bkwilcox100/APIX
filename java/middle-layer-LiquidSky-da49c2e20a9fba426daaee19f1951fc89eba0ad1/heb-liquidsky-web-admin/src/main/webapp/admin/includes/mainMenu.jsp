<div data-role="panel" id="MainMenu" data-position="left" data-display="overlay" data-theme="a" class="ui-panel ui-panel-position-left ui-panel-display-overlay ui-body-a ui-panel-animate ui-panel-open">
    <%@ include file="mainMenuContents.jsp" %>
</div>

<script>
	$(function () {
	    $("[data-role=panel]").panel().enhanceWithin();
	});
</script>
