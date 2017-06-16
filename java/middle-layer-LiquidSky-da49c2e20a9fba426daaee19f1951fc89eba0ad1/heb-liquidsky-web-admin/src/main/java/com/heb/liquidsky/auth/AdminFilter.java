package com.heb.liquidsky.auth;

import java.io.IOException;
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

public class AdminFilter implements Filter {

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest servletReq, ServletResponse servletResp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) servletReq;
		HttpServletResponse resp = (HttpServletResponse) servletResp;

		if (req.getSession().getAttribute("token") == null) {
			// if there is no token, then redirect them to the login page.
			req.setAttribute("loginDestination", "/admin/index.jsp");
			resp.sendRedirect("/login");
		} else {
		    UserService userService = UserServiceFactory.getUserService();
			User user = userService.getCurrentUser();
			
			if (user != null){
				if (!userService.isUserAdmin()) {
					resp.sendRedirect("/unauthorized.jsp");
				} else {
					req.setAttribute("userServiceuserEmail", user.getEmail());
			    	req.setAttribute("userServiceuserId", user.getUserId());
			    	req.setAttribute("userServiceuserNickname", user.getNickname());
			    	req.setAttribute("userServiceuserFederatedIdentity", user.getFederatedIdentity());
			    	req.setAttribute("userServicelogOutUrl", userService.createLogoutURL("/index.jsp"));
			    	req.setAttribute("userServiceuserObject", user);
			    	req.setAttribute("userServiceisUserAdmin", userService.isUserAdmin());
				}
			} else {

				// if we are running locally, we have to do the silly login.
				if (System.getenv("GCLOUD_PROJECT") == null) {
					resp.sendRedirect(userService.createLoginURL("/admin/index.jsp"));
				}
				
				req.setAttribute("userServiceuserEmail", "user is null");
		    	req.setAttribute("userServiceuserId", "user is null");
		    	req.setAttribute("userServiceuserNickname", "user is null");
		    	req.setAttribute("userServiceuserFederatedIdentity", "user is null");
		    	req.setAttribute("userServicelogOutUrl", userService.createLogoutURL("/index.jsp"));
		    	req.setAttribute("userServiceuserObject", "user is null");
		    	req.setAttribute("userServiceisUserAdmin", "user is null");
			}
		}
		
		chain.doFilter(servletReq, servletResp);
	}

	@Override
	public void destroy() {
	}
}
