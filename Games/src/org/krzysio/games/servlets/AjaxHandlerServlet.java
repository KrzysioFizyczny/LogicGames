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
import org.krzysio.games.json.MainChatMessage;

import com.fasterxml.jackson.databind.ObjectMapper;

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
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");
			
			MainChatMessage mainChatMessage = new MainChatMessage();
			mainChatMessage.setUsername(clientContext.getUsername());
			mainChatMessage.setMessage(htmlMsg);
			mainChatMessage.setDate(dateFormat.format(new Date()));
			
			WebSocketManager.getInstance().broadcast(JsMessageHandler.MAIN_CHAT, mainChatMessage);
			
		} else if (action.equals("sayHello")) {
			String username = req.getParameter("username");
			WebSocketManager.getInstance().broadcast(String.format("User <strong>%s</strong> has joined", username));
		} else if (action.equals("renewChannel")) {
			String channelToken = clientContext.renewChannelToken();
			ajaxResponseMap.put("channelToken", channelToken);
		}
		
		
		String result = mapper.writeValueAsString(ajaxResponseMap);
		resp.getWriter().write(result);

	}

}
