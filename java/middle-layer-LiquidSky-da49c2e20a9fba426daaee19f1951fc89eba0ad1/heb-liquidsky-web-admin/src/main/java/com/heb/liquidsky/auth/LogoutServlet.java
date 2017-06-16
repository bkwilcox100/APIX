package com.heb.liquidsky.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class LogoutServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		
		HttpSession session = req.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		// rebuild session
		req.getSession();

		UserService userService = UserServiceFactory.getUserService();
		resp.sendRedirect(userService.createLogoutURL("/index.jsp"));
	}
}
