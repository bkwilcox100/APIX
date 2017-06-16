package com.heb.liquidsky.spring;

import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * This base class should be extended by all HEB Spring boot
 * applications that are implemented as an executable WAR.
 */
public abstract class HebSpringWarApplication extends SpringBootServletInitializer implements HebSpringApplication {

}
