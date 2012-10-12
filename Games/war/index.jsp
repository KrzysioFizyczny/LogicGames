<%@page import="org.krzysio.games.ClientContext"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="org.krzysio.games.WebSocketManager"%>
<%@page import="com.google.appengine.api.channel.ChannelServiceFactory"%>
<%@page import="com.google.appengine.api.channel.ChannelService"%>
<%@page import="java.util.Random"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
	ClientContext clientContext = (ClientContext) session.getAttribute(ClientContext.SESSION_KEY);
	
	if (clientContext != null) {
		pageContext.setAttribute("loggedIn", "true");
	}
%>

<c:choose>
<c:when test="${loggedIn}">
	<jsp:include page="jsp/chatPage.jsp"></jsp:include>
</c:when>
<c:otherwise>
	<jsp:include page="login.jsp"></jsp:include>
</c:otherwise>
</c:choose>
