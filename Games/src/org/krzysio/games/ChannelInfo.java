package org.krzysio.games;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author krzysztof
 * 
 */
public class ChannelInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String clientID;
	private String token;
	
	private Set<String> groups;
	
	public boolean addGroup(String group) {
		if (groups == null) {
			groups = new HashSet<String>();
		}
		
		return groups.add(group);
	}
	
	public boolean removeGroup(String group) {
		if (groups != null) {
			return groups.remove(group);
		}
		
		return false;
	}
	
	public boolean containsGroup(String group) {
		return groups != null && groups.contains(group);
	}

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
}
