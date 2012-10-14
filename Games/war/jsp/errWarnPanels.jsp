<%@ page language="java" contentType="text/html; charset=UTF-8"pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div style="position: fixed; top: 5; right: 200; z-index: 10;">
<div id="warnPanel" class="ui-widget" style="display: none;" 
	align="center" onclick="hideWarnPanel();">
	<div class="ui-state-highlight ui-corner-all" style="padding: 0 .7em;">
		<p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
		<span id="warnPanelInfo"><strong>Hey!</strong> Sample ui-state-highlight style.</span></p>
	</div>
</div>
</div>
<br/>
<div style="position: fixed; top: 5; left: 200; z-index: 10;">
<div id="errPanel" class="ui-widget" style="display: none;" 
	align="center" onclick="hideErrPanel();">
	<div class="ui-state-error ui-corner-all" style="padding: 0 .7em;">
		<p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
		<span id="errPanelInfo"><strong>Hey!</strong> Sample ui-state-highlight style.</span></p>
	</div>
</div>
</div>

<section id="content">
	<div align="right">
		<h2 onclick="byId('effect').toggle('fold', {}, 500);" style="cursor: pointer;"><c:out value="${param.username}"/></h2>
		<div class="toggler">
		    <div id="effect" class="ui-widget-content ui-corner-all" style="display: none;">
		        <h3 class="ui-widget-header ui-corner-all" style="cursor: pointer;" onclick="doLogout();">Logout</h3>
		        <h3 class="ui-widget-header ui-corner-all" style="cursor: pointer;" onclick="createNewGame();">Create new game</h3>
		    </div>
		</div>
	</div>
</section>