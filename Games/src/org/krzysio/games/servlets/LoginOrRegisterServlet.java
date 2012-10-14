package org.krzysio.games.servlets;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.krzysio.games.ClientContext;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;

/**
 * @author kbarczynski
 */
public class LoginOrRegisterServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private static String SALT = "chfbcUYTzdgneessssffffAAADDDdd987@#$$%MD887";
	
	private static Logger logger = Logger.getLogger(LoginOrRegisterServlet.class.getName());

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
		if (isNewUser(request)) {
			createNewUser(request);
		} else {
			loginUser(request);
		}
		
		resp.sendRedirect("/");
	}
	
	private boolean isNewUser(HttpServletRequest request) {
		String newUser = request.getParameter("newUser");
		return newUser != null && newUser.equalsIgnoreCase("on");
	}

	private void createNewUser(HttpServletRequest request) {
		logger.entering(getClass().getName(), "createNewUser");
		
		String username = request.getParameter("username");
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity user = findUser(datastore, username);
		
		if (user != null) {
			logger.warning("User " + username + " not allowed");
			request.setAttribute("ERR_MSG", "Entered Username is not allowed");
			return;
		}
		
		
		String pass = request.getParameter("pass");
		String confpass = request.getParameter("confpass");
		
		if (StringUtils.isBlank(pass) || !pass.equalsIgnoreCase(confpass)) {
			request.setAttribute("ERR_MSG", "Incorrect password");
			request.setAttribute("username_bak", username);
			request.setAttribute("newUser_bak", Boolean.TRUE);
			logger.warning("User password doesn't match its confirmation value.");
			return;
		}
		
		logger.info("Creating user " + username);
		user = new Entity("User");
		user.setProperty("username", username);
		user.setProperty("pass", generatePassHash(pass));
		user.setProperty("createdAt", new Date());
		
		datastore.put(user);
		
		logger.info(String.format("User % has been created", username));
		request.getSession().setAttribute(ClientContext.SESSION_KEY, new ClientContext(username));
	}
	
	private void loginUser(HttpServletRequest request) {
		String username = request.getParameter("username");
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity user = findUser(datastore, username);
		
		if (user == null) {
			request.setAttribute("ERR_MSG", "Entered Username is not correct");
			return;
		}
		
		String pass = request.getParameter("pass");
		pass = generatePassHash(pass);
		String dbPass = (String) user.getProperty("pass");
		
		if (!dbPass.equals(pass)) {
			request.setAttribute("ERR_MSG", "Incorrect password");
			request.setAttribute("username_bak", username);
			return;
		}
		
		request.getSession().setAttribute(ClientContext.SESSION_KEY, new ClientContext(username));
	}
	
	private Entity findUser(DatastoreService datastore, String username) {
		Filter usernameFilter = new Query.FilterPredicate("username", FilterOperator.EQUAL, username);

		Query query = new Query("User").setFilter(usernameFilter);
		return datastore.prepare(query).asSingleEntity();
	}
	
	private String generatePassHash(String pass) {
		return DigestUtils.sha1Hex(SALT + pass);
	}
	
}
