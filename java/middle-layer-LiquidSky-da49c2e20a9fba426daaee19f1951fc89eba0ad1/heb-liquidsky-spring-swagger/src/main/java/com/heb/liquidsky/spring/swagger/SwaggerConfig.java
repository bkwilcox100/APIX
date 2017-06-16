package com.heb.liquidsky.spring.swagger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Predicate;
import com.heb.liquidsky.spring.HebSpringApplication;

import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * To enable Swagger, this spring-swagger dependency must be imported into
 * the module, and the Application class must specify the @EnableSwagger2
 * annotation.
 */
@Configuration
public class SwaggerConfig {

	@Autowired
	public HebSpringApplication application;

	/**
	 * This method returns the package containing publicly exposed
	 * endpoints.
	 */
	private Predicate<RequestHandler> swaggerApis() {
		return RequestHandlerSelectors.basePackage("com.heb.liquidsky.spring.web");
	}

	/**
	 * Generate Swagger docs for the following endpoints:
	 * 
	 * <dl>
	 * <dt>/swagger-ui.html</dt>
	 * <dd>Swagger docs</dd>
	 * <dt>/v2/api-docs</dt>
	 * <dd>OpenAPI JSON specification</dd>
	 * </dl>
	 */
	@Bean
	public Docket swaggerDocket() { 
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(this.swaggerApis())
				.paths(PathSelectors.any())
				.build()
				.apiInfo(this.swaggerMetadata(application));
	}

	/**
	 * Implement metadata exposed in Swagger docs.
	 */
	private ApiInfo swaggerMetadata(HebSpringApplication application) {
		return new ApiInfoBuilder()
				.title(application.getApplicationTitle())
				.description(application.getApplicationDescription())
				.version(application.getApplicationVersion())
				.build();
	}
}
