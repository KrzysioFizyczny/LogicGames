<%@page import="org.krzysio.games.ClientContext"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="org.krzysio.games.WebSocketManager"%>
<%@page import="com.google.appengine.api.channel.ChannelServiceFactory"%>
<%@page import="com.google.appengine.api.channel.ChannelService"%>
<%@page import="java.util.Random"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
	String errMessage = (String) request.getAttribute("ERR_MSG");
	String username_bak = (String) request.getAttribute("username_bak");
	Boolean newUser_bak = (Boolean) request.getAttribute("newUser_bak");
	
	if (StringUtils.isNotEmpty(errMessage)) {
		pageContext.setAttribute("errMessage", errMessage);
	}
	
	if (StringUtils.isNotEmpty(username_bak)) {
		pageContext.setAttribute("username_bak", username_bak);
	}
	
	if (newUser_bak != null && newUser_bak) {
		pageContext.setAttribute("newUser_bak", newUser_bak);
	}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<jsp:include page="jsp/headerSection.jsp"></jsp:include>
	
<style type="text/css">
fieldset input {
	margin: 5px;
	width: 180px;
}
</style>

<script type="text/javascript">
function toggleConfPassField() {
	var checked = byId("newUser").attr("checked");
	if (checked) {
		byName("confpass").show();
	} else {
		byName("confpass").hide();
	}
}

$(function() {
	var errMessage = '${errMessage}';
	var newUser_bak = '${newUser_bak}';
	
	if (newUser_bak && newUser_bak == "true") {
		byId("newUser").attr("checked", 'checked');
	} else {
		byId("newUser").removeAttr("checked");
	}
	
	if (errMessage.length > 0) {
		showErrPanel(errMessage);
	}
	
	toggleConfPassField();
});
</script>
</head>

<body>
	<jsp:include page="jsp/errWarnPanels.jsp">
		<jsp:param value="${username}" name="username"/>
	</jsp:include>
	
	<p style="font-weight: bold; margin: 2em 0 1em; font-size: 2.3em;">Welcome to the finest registration in the World :)</p>

	<!-- Accordion -->
	<h3 class="demoHeaders">
		Enter Your username and password.<br>
		If You are a new User please click on "New user" and confirm Your password - new account will be created on the fly.
	</h3>
	<div style="height: 130px;">
	<div id="accordion">
		<div>
			<form action="/loginOrRegister" method="post">
				<fieldset>
					<input type="text" placeholder="Username" name="username" id="aliasID" required="required" maxlength="50" value="${username_bak}">
					<input type="checkbox" id="newUser" name="newUser" style="width: 5px;" onclick="toggleConfPassField();" value="on">
					<label for="newUser">New user</label>
					<br>
					<input type="password" name="pass" placeholder="Password" required="required">
					<input type="password" name="confpass" placeholder="Confirm password" style="display: none;">
					<br>
					<input type="submit" value="Login" style="width: 55px;">
				</fieldset>
			</form>
		</div>
	</div>
	</div>
</body>
</html>
