var tabsConfig = new Array();
var warnPanelTimeoutHandler;
var errPanelTimeoutHandler;

function showNotification(msg) {
	// TODO - provide real info panel
	showWarnPanelWithTimeout(msg, 5000);
}

function showWarnPanel(warnMsg) {
	$("#warnPanelInfo").html(warnMsg);
	$("#warnPanel").show(500);
}

function hideWarnPanel() {
	$("#warnPanelInfo").html("");
	$("#warnPanel").hide(500);
}

function showErrPanel(errMsg) {
	$("#errPanelInfo").html(errMsg);
	$("#errPanel").show(500);
}

function hideErrPanel() {
	$("#errPanelInfo").html("");
	$("#errPanel").hide(500);
}

function showWarnPanelWithTimeout(warnMsg, timeout) {
	showWarnPanel(warnMsg);
	if (timeout > 0) {
		clearTimeout(warnPanelTimeoutHandler);
		warnPanelTimeoutHandler = setTimeout("hideWarnPanel();", timeout);
	}
}

function showErrPanelWithTimeout(errMsg, timeout) {
	showErrPanel(errMsg);
	if (timeout > 0) {
		clearTimeout(errPanelTimeoutHandler);
		errPanelTimeoutHandler = setTimeout("hideErrPanel();", timeout);
	}
}

function byName(nodeName) {
	return $("[name = '" + nodeName + "']");
}

function byId(nodeId) {
	return $("#" + nodeId);
}

function sendMsgToChat(chatId, inputId) {
	var html = byId(inputId).html();
	
	$.ajax({
		url: 'ajaxHandler',
		type: 'POST',
		data: { action     : 'sendMsgToChat',
				'chatId'   : chatId,
				'html'     : html
			},
		complete: function(resp, textStatus) {
			if (textStatus == "success") {
				var result = $.parseJSON(resp.responseText);
				switch (result.status) {
					case 'OK':
						byId(inputId).val("");
						break;
					default:
						showErrPanelWithTimeout("There was an error. We are already working on it.", 10000);
				}
			} else {
				showWarnPanelWithTimeout("Connection error. Try again later.", 10000);
			}
		}
	});
}

function handleIncomingMsg(data) {
	var html = '<div class="ui-widget">' +
					'<div class="ui-crner-all" style="margin-top: 20px; padding: 0 .7em;">' +
						'<p>' + 
						'<span class="ui-icon ui-icon-comment" style="float: left; margin-right: .3em;"></span>' +
						'<span class="htmlToReplace">' +
							'<strong>${item[0]}</strong>&nbsp; &nbsp; ${item[1]}' +
							'<span>${item[2]}</span>' +
							'</span>' +
					'</div>' +
				'</div>';
				
	html = html.replace("${item[0]}", data.username);
	html = html.replace("${item[1]}", data.date);
	html = html.replace("${item[2]}", data.message);
	
	byId("brTag_for0").before(html);
	
	// ponizsze musialem zastapic powyzszym, bo mi jQuery.tabs sie wymadrzal 
	// i zmienial uklad DOM wewnatrz zwracanego html-a!
	
//	var htmlToReplace = "<strong>" + data.sender + "</strong>&nbsp; " + data.date +"<br>" + data.htmlMessage;
//	var lastNode = $("[chatId='" + data.chatId + "']").last();
//	var newNode = lastNode.clone(true);
//	$(newNode).find(".htmlToReplace").html(htmlToReplace);
//	lastNode.after(newNode);
}

function onOpened() {
	$.ajax({
		url: 'ajaxHandler',
		type: 'POST',
		data: { action     : 'sayHello',
				'username' : username
			},
		complete: function(resp, textStatus) {
			if (textStatus == "success") {
				var result = $.parseJSON(resp.responseText);
				switch (result.status) {
					case 'OK':
						break;
					default:
						showErrPanelWithTimeout("There was an error. We are already working on it.", 10000);
				}
			} else {
				showWarnPanelWithTimeout("Connection error. Try again later.", 10000);
			}
		}
	});
}

function renewChannel() {
	$.ajax({
		url: 'ajaxHandler',
		type: 'POST',
		data: { action     : 'renewChannel'
			},
		complete: function(resp, textStatus) {
			if (textStatus == "success") {
				var result = $.parseJSON(resp.responseText);
				switch (result.status) {
					case 'OK':
						initSocket(result.channelToken);
						break;
					default:
						showErrPanelWithTimeout("Connection lost and couldn't be renewed", 10000);
				}
			} else {
				showWarnPanelWithTimeout("Connection error. Try again later.", 10000);
			}
		}
	});
}

function doLogout() {
	document.location = "logout";
}

function createNewGame() {
	byId("createGameDialog").dialog('open');
}

function createNewGameOnEnter(event) {
	if (event.keyCode == 13) {
		createNewGameConfirm();
	}
}

function createNewGameConfirm() {
	var gameName = byId("newGamesName").val();
	if (!gameName || gameName.length == 0) {
		showErrPanelWithTimeout("Game's name is required.", 10000);
		return;
	}
	
	byId("createGameDialog").dialog('close');
	
	$.ajax({
		url: 'ajaxHandler',
		type: 'POST',
		data: {action: 'createNewGame',
				'gameName': gameName
			},
		complete: function(resp, textStatus) {
			if (textStatus == "success") {
				var result = $.parseJSON(resp.responseText);
				switch (result.status) {
					case 'OK':
						byId("newGamesName").val("");
						joinToGame(result.id, gameName);
						break;
					default:
						showErrPanelWithTimeout("There was an error. We are alredy working on it.", 10000);
				}
			} else {
				showWarnPanelWithTimeout("Connection error. Try again later.", 10000);
			}
		}
	});
}

function joinToGame(gameId, gameName) {
	if (selectGameIfOpen(gameId)) {
		return;
	}
	
	$.ajax({
		url: 'ajaxHandler',
		type: 'POST',
		data: {  action    : 'joinToGame',
				'gameId'   : gameId,
				'gameName' : gameName
			},
		complete: function(resp, textStatus) {
			if (textStatus == "success") {
				var result = $.parseJSON(resp.responseText);
				switch (result.status) {
					case 'OK':
						var tabIdx = byId("tabs").tabs("length");
						tabsConfig.push({'gameId' : gameId, 'tabIdx' : tabIdx});
						byId("tabs").tabs("add", "jsp/gameTab.jsp?gameId=" + gameId, gameName);
						byId("tabs").tabs("select", tabIdx);
						break;
					default:
						showErrPanelWithTimeout("There was an error. We are alredy working on it.", 10000);
				}
			} else {
				showWarnPanelWithTimeout("Connection error. Try again later.", 10000);
			}
		}
	});
}

function selectGameIfOpen(gameId) {
	for (var i = 0; i < tabsConfig.length; i++) {
		if (tabsConfig[i].gameId == gameId) {
			byId("tabs").tabs("select", tabsConfig[i].tabIdx);
			return true;
		}
	}
	
	return false;
}

function onMessage(msg) {
	if (!msg || !msg.data) {
		return;
	}
	
	var data = $.parseJSON(msg.data);
	var handler = data.handler;
	var payload = data.payload;
	
	if (!handler) {
		return;
	}
	
	if (handler == 'INFORM') {
		showNotification(payload);
	} else if (handler == 'MAIN_CHAT') {
		handleIncomingMsg(payload);
	}
}
