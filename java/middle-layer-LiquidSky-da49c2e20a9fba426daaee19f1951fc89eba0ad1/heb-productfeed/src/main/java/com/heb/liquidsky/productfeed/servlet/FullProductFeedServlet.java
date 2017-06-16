package com.heb.liquidsky.productfeed.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.ThreadManager;
import com.heb.liquidsky.productfeed.service.ProductFeedService;

public class FullProductFeedServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		final ProductFeedService productFeedService = new ProductFeedService();

		// this will take awhile so kick off a thread and continue on
		Thread thread = ThreadManager.createBackgroundThread(new Runnable() {
			public void run() {
				productFeedService.importAllProducts();
			}
		});
		thread.start();
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.sendRedirect("/index.jsp");
	}
}
