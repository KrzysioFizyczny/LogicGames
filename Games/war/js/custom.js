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

function typicalAjax(dataIn, onOkFunction) {
	$.ajax({
		url: 'ajaxHandler',
		type: 'POST',
		data: dataIn,
		complete: function(resp, textStatus) {
			if (textStatus == "success") {
				var result = $.parseJSON(resp.responseText);
				switch (result.status) {
					case 'OK':
						if (onOkFunction) {
							onOkFunction(result);
						}
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

function sendMsgToChat(chatId, inputId) {
	var html = byId(inputId).html();
	var dataIn = {action     : 'sendMsgToChat',
				  'chatId'   : chatId,
				  'html'     : html
				 };
	var onOk = function(result) {
		byId(inputId).val("");
	};
	
	typicalAjax(dataIn, onOk);
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
	var dataIn = { action    : 'sayHello',
				  'username' : username
				 };
	typicalAjax(dataIn, null);
}

function renewChannel() {
	var dataIn = {action : 'renewChannel'};
	var onOk = function(result) {
		initSocket(result.channelToken);
	};
	
	typicalAjax(dataIn, onOk);
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
	var dataIn = { action: 'createNewGame',
				  'gameName': gameName
				 };
	var onOk = function(result) {
		byId("newGamesName").val("");
		joinToGame(result.id, gameName);
	}
	
	typicalAjax(dataIn, onOk);
}

function joinToGame(gameId, gameName) {
	if (selectGameIfOpen(gameId)) {
		return;
	}
	
	var dataIn = { action    : 'joinToGame',
				  'gameId'   : gameId,
				  'gameName' : gameName
				 };
	var onOk = function(result) {
		var tabIdx = byId("tabs").tabs("length");
		tabsConfig.push({'gameId' : gameId, 'tabIdx' : tabIdx});
		byId("tabs").tabs("add", "jsp/gameTab.jsp?gameId=" + gameId, gameName);
		byId("tabs").tabs("select", tabIdx);
	}
	
	typicalAjax(dataIn, onOk);
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
