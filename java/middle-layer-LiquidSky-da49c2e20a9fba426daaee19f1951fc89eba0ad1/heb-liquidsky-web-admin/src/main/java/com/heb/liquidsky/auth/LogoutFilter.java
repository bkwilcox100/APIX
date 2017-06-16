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

public class LogoutFilter implements Filter {

  private static final Logger logger = Logger.getLogger(LogoutFilter.class.getName());

  @Override
  public void init(FilterConfig config) throws ServletException { }

  @Override
  public void doFilter(ServletRequest servletReq, ServletResponse servletResp, FilterChain chain)
      throws IOException, ServletException {

    chain.doFilter(servletReq, servletResp);

  }

  @Override
  public void destroy() {
    logger.log(Level.INFO, "destroy called in LogoutFilter");
  }
}
