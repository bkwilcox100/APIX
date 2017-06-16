package com.heb.liquidsky.productfeed.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.heb.liquidsky.productfeed.service.ProductFeedService;

public class ProductDeltaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		final ProductFeedService productFeedService = new ProductFeedService();
		
		productFeedService.importDeltaProducts(null);
		
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.sendRedirect("/index.jsp");

	}
}
