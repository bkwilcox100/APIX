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

	private static final String SERVICE_DESCRIPTION = "HEB e-commerce {{serviceName}} service endpoints";
	private static final String SERVICE_TITLE = "HEB {{serviceName}} Endpoints";
	private static final String SERVICE_VERSION = "{{serviceVersion}}";

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
