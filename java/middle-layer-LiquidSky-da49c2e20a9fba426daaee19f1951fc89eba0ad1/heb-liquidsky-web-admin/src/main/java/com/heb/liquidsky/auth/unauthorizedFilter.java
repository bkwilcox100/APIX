package com.heb.liquidsky.auth;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class unauthorizedFilter implements Filter {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest servletReq, ServletResponse servletResp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) servletReq;
		HttpServletResponse resp = (HttpServletResponse) servletResp;

	    UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

    	req.setAttribute("logOutUrl", "/logout");
    	req.setAttribute("logInUrl", "/login");
    	
		if (user == null){
			// redirect to the login page if they are not logged in.
			resp.sendRedirect(userService.createLoginURL("/admin/index.jsp"));
		} else {
			req.setAttribute("userServiceuserEmail", user.getEmail());
	    	req.setAttribute("userServiceuserId", user.getUserId());
	    	req.setAttribute("userServiceuserNickname", user.getNickname());
	    	req.setAttribute("userServiceuserFederatedIdentity", user.getFederatedIdentity());
	    	req.setAttribute("userServiceuserObject", user);
	    	req.setAttribute("userServiceisUserAdmin", userService.isUserAdmin());
	    	logger.log(Level.INFO, "userEmail " + user.getEmail());
		}

	    chain.doFilter(servletReq, servletResp);
	}

	@Override
	public void destroy() {
	}
}
