package com.heb.liquidsky.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ApplicationContext;

/**
 * This is the main entry point to the service.  When the JAR is deployed,
 * the main method of this class is what gets invoked.
 */
@SpringBootApplication
@ServletComponentScan
public class Application extends HebSpringJarApplication {

	private static final String SERVICE_DESCRIPTION = "HEB task queue service, used to offload slow tasks from other services";
	private static final String SERVICE_TITLE = "HEB Task Queue Service";
	private static final String SERVICE_VERSION = "1.0.0";

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		ApplicationContext ctx = SpringApplication.run(Application.class, args);
	}

	@Override
	public String getApplicationDescription() {
		return SERVICE_DESCRIPTION;
	}

	@Override
	public String getApplicationTitle() {
		return SERVICE_TITLE;
	}

	@Override
	public String getApplicationVersion() {
		return SERVICE_VERSION;
	}
}
