package com.heb.liquidsky.spring;

/**
 * This abstract class provides a common base class for all HEB
 * Spring Boot applications.
 */
public interface HebSpringApplication {

	/**
	 * Return a value used in the OpenAPI spec as the description
	 * for the application.
	 */
	public String getApplicationDescription();

	/**
	 * Return a value used in the OpenAPI spec as the title for
	 * the application.
	 */
	public String getApplicationTitle();

	/**
	 * Return a value used in the OpenAPI spec as the version
	 * for the application.
	 */
	public String getApplicationVersion();
}
