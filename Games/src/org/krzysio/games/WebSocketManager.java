package org.krzysio.games;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.krzysio.games.enums.JsMessageHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

/**
 * @author krzysztof
 */
public class WebSocketManager {

	private static final WebSocketManager INSTANCE = new WebSocketManager();
	
	private static Logger logger = Logger.getLogger(WebSocketManager.class.getName());

	/**
	 * {clientID, ChannelInfo} pairs
	 */
	private final Map<String, ChannelInfo> CHANNELS = new HashMap<String, ChannelInfo>();

	private WebSocketManager() {
	}

	public static WebSocketManager getInstance() {
		return INSTANCE;
	}

	public ChannelInfo addChannelInfo(String clientID, String token) {
		ChannelInfo channelInfo = new ChannelInfo();
		channelInfo.setClientID(clientID);
		channelInfo.setToken(token);

		CHANNELS.put(clientID, channelInfo);

		return channelInfo;
	}
	
	private synchronized List<String> getAllChatIds() {
		return new ArrayList<String>(CHANNELS.keySet());
	}
	
	public void broadcast(String message) {
		broadcast(JsMessageHandler.INFORM, message);
	}
	
	public void broadcast(JsMessageHandler handler, Object payload) {
		sendMessage(getAllChatIds(), handler, payload);
	}

	public void sendMessage(Collection<String> clientIds, JsMessageHandler handler, Object payload) {
		if (CollectionUtils.isEmpty(clientIds)) {
			return;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("payload", payload);
		map.put("handler", handler);
		
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(map);
			ChannelService channelService = ChannelServiceFactory.getChannelService();
			
			for (String clientId : clientIds) {
				channelService.sendMessage(new ChannelMessage(clientId, json));
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Can't create or send message to WebSocket", e);
		}
		
	}

}
