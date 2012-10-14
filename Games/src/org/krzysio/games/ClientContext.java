package org.krzysio.games;

import java.io.Serializable;
import java.util.Random;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

/**
 * @author krzysztof
 */
public class ClientContext implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String SESSION_KEY = "CLIENTCONTEXT";

	private final String username;
	private final String clientID;
	private String channelToken;
	private ChannelInfo channelInfo;
	
	public ClientContext(String username) {
		this.username = username;
		clientID = "CL_ID_" + System.currentTimeMillis() + "_" + Math.abs(new Random().nextInt());
		renewChannelToken();
	}
	
	public String renewChannelToken() {
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		channelToken = channelService.createChannel(clientID);
		channelInfo = WebSocketManager.getInstance().addChannelInfo(clientID, channelToken);
		return channelToken;
	}

	public String getUsername() {
		return username;
	}

	public String getClientID() {
		return clientID;
	}

	public String getChannelToken() {
		return channelToken;
	}

	public ChannelInfo getChannelInfo() {
		return channelInfo;
	}

	public void setChannelToken(String channelToken) {
		this.channelToken = channelToken;
	}

	public void setChannelInfo(ChannelInfo channelInfo) {
		this.channelInfo = channelInfo;
	}
	
}
