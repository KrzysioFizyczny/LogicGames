package org.krzysio.games.servlets;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.krzysio.games.ClientContext;
import org.krzysio.games.WebSocketManager;
import org.krzysio.games.enums.JsMessageHandler;
import org.krzysio.games.json.ChatMessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

/**
 * @author Chris
 */
public class AjaxHandlerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		ClientContext clientContext = (ClientContext) req.getSession().getAttribute(ClientContext.SESSION_KEY);
		String action = req.getParameter("action");
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> ajaxResponseMap = new HashMap<String, String>();
		ajaxResponseMap.put("status", "OK");

		if (StringUtils.isEmpty(action)) {

		} else if (action.equals("sendMsgToChat")) {
			String htmlMsg = req.getParameter("html");
			ChatMessage mainChatMessage = saveMessage(clientContext, null, htmlMsg);
			
			WebSocketManager.getInstance().broadcast(JsMessageHandler.MAIN_CHAT, mainChatMessage);
		} else if (action.equals("sayHello")) {
			String username = req.getParameter("username");
			WebSocketManager.getInstance().broadcast(String.format("User <strong>%s</strong> has joined", username));
		} else if (action.equals("renewChannel")) {
			String channelToken = clientContext.renewChannelToken();
			ajaxResponseMap.put("channelToken", channelToken);
		} else if (action.equals("createNewGame")) {
			String gameName = req.getParameter("gameName");
			ajaxResponseMap.put("id", "12");
		} else if (action.equals("joinToGame")) {
			String gameId = req.getParameter("gameId");
			String gameName = req.getParameter("gameName");
			gameId.length();
		}
		
		
		String result = mapper.writeValueAsString(ajaxResponseMap);
		resp.getWriter().write(result);
	}
	
	private ChatMessage saveMessage(ClientContext clientContext, String chatId, String htmlMsg) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Date now = new Date();
		DateFormat dateFormat = new SimpleDateFormat(ChatMessage.DATE_FORMAT);
		
		Entity message = new Entity("Message");
		message.setProperty("chatId", chatId);
		message.setProperty("username", clientContext.getUsername());
		message.setProperty("htmlMsg", htmlMsg);
		message.setProperty("date", now);
		
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setChatId(chatId);
		chatMessage.setUsername(clientContext.getUsername());
		chatMessage.setMessage(htmlMsg);
		chatMessage.setDate(dateFormat.format(now));
		
		datastore.put(message);
		
		return chatMessage;
	}

}
