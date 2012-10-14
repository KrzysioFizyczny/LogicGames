<%@page import="org.krzysio.games.MessagesManager"%>
<%@page import="org.krzysio.games.ClientContext"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%
	ClientContext clientContext = (ClientContext) session.getAttribute(ClientContext.SESSION_KEY);
	pageContext.setAttribute("username", clientContext.getUsername());
	pageContext.setAttribute("channelToken", clientContext.getChannelToken());
	
	String previousMsgs = MessagesManager.getInstance().getPreviousMessages(null);
	pageContext.setAttribute("previousMsgs", previousMsgs);
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<jsp:include page="headerSection.jsp"></jsp:include>
</head>

<body>
	<jsp:include page="errWarnPanels.jsp">
		<jsp:param value="${username}" name="username" />
	</jsp:include>
	
	<h1 style="top: 50px; position: absolute;">Welcome to Smart Games site!</h1>
	
	<div id="tabs">
	    <ul>
	        <li><a href="#tabs-1">Main chat</a></li>
	    </ul>
	    <div id="tabs-1">
			<br id="brTag_for0">
		
			<textarea id="mainChat" name="mainChat" rows="15" cols="80" style="width: 50%;" 
					class="tinymce" placeholder="Enter message text">
			</textarea>
			<input type="button" value="Send" onclick="sendMsgToChat('', 'mainChat');">
	    </div>
	</div>
	
	<%-- Helper dialogs  --%>
	<div id="createGameDialog" title="Enter game's name">
	    <p><input type="text" id="newGamesName" placeholder="New game's name" onkeypress="createNewGameOnEnter(event);"></p>
	    <input type="button" value="OK" onclick="createNewGameConfirm();">
	</div>

<script type="text/javascript" language="javascript">
var username = '${username}';
var socket;

function initSocket(channelToken) {
	var channel = new goog.appengine.Channel(channelToken);
	socket = channel.open();
	socket.onopen = onOpened;
	socket.onmessage = onMessage;
	socket.onerror = function(errObj) {
		showErrPanelWithTimeout(errObj.code + ": "
				+ errObj.description, 20000);
	};
	socket.onclose = function() {
		renewChannel();
	};
}

$(function() {
	var previousMsgs = ${previousMsgs};
	var i;
	for (i = 0; i < previousMsgs.length; i++) {
		handleIncomingMsg(previousMsgs[i]);
	}

	byId("tabs").tabs();
	byId("createGameDialog").dialog({
		autoOpen: false,
		modal: true,
		width: 160,
		height: 110
	});
	
	initSocket('${channelToken}');
});

</script>
</body>
</html>