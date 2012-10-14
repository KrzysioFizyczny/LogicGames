package org.krzysio.games.json;

/**
 * @author krzysztof
 */
public class ChatMessage {
	
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:MM:ss";

	private String chatId;
	private String username;
	private String message;
	private String date;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

}
