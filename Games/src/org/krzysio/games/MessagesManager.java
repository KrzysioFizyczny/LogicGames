package org.krzysio.games;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.krzysio.games.json.ChatMessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

/**
 * @author krzysztof
 * 
 */
public class MessagesManager {

	private static final MessagesManager INSTANCE = new MessagesManager();
	
	private static Logger logger = Logger.getLogger(MessagesManager.class.getName());

	public static MessagesManager getInstance() {
		return INSTANCE;
	}

	private MessagesManager() {
	}

	public String getPreviousMessages(String chatId) {
		Calendar dayBack = Calendar.getInstance();
		dayBack.add(Calendar.DATE, -1);
		Filter lastDateFilter = new Query.FilterPredicate("date", FilterOperator.GREATER_THAN_OR_EQUAL, dayBack.getTime());
		Filter chatIdFilter = new Query.FilterPredicate("chatId", FilterOperator.EQUAL, chatId);

		Filter conditions = CompositeFilterOperator.and(lastDateFilter, chatIdFilter);

		Query query = new Query("Message").setFilter(conditions);
		query.addSort("date", SortDirection.ASCENDING);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		FetchOptions fetchOptions = FetchOptions.Builder.withLimit(1000).prefetchSize(1000);
		List<Entity> messages = datastore.prepare(query).asList(fetchOptions);
		
		List<ChatMessage> responseData = new ArrayList<ChatMessage>(messages.size());
		DateFormat dateFormat = new SimpleDateFormat(ChatMessage.DATE_FORMAT);
		
		for (Entity msg : messages) {
			ChatMessage chatMessage = new ChatMessage();
			chatMessage.setChatId((String) msg.getProperty("chatId"));
			chatMessage.setDate(dateFormat.format((Date) msg.getProperty("date")));
			chatMessage.setMessage((String) msg.getProperty("htmlMsg"));
			chatMessage.setUsername((String) msg.getProperty("username"));
			
			responseData.add(chatMessage);
		}
		
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.writeValueAsString(responseData);
		} catch (IOException e) {
			logger.throwing(getClass().getName(), "getPreviousMessages", e);
			return "[]";
		}
	}
}
