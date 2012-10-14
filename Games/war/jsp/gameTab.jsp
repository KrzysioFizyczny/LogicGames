<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="org.krzysio.games.MessagesManager"%>
<%@page import="org.krzysio.games.ClientContext"%>

<%
	ClientContext clientContext = (ClientContext) session.getAttribute(ClientContext.SESSION_KEY);
	pageContext.setAttribute("username", clientContext.getUsername());
	pageContext.setAttribute("channelToken", clientContext.getChannelToken());
%>

Tu na razie jest sciernisko...