package com.heb.liquidsky.spring.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.heb.liquidsky.common.ConfigurationConstants;
import com.heb.liquidsky.common.HebEnvironmentProperties;

@Configuration
public class HebWebConfig extends WebMvcConfigurerAdapter {

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				if(HebEnvironmentProperties.getInstance().isLocalInstance()) {
					registry.addMapping("/**")
							.allowedMethods("*")
							.allowedHeaders("*")
							.allowedOrigins("*");
				} else {
					registry.addMapping("/**")
							.allowedMethods(ConfigurationConstants.CORS_ALLOWED_METHODS.split(","))
							.allowedHeaders(ConfigurationConstants.CORS_ALLOWED_HEADERS.split(","))
							.allowedOrigins(ConfigurationConstants.CORS_ALLOWED_ORIGINS.split(","));
				}
			}
		};
	}
}
