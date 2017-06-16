package com.heb.liquidsky.spring.web;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.heb.liquidsky.cache.HebMemoryStoreCache;
import com.heb.liquidsky.common.HebEnvironmentProperties;
import com.heb.liquidsky.data.DataStore;
import com.heb.liquidsky.trace.HebFlexTracer;
import com.heb.liquidsky.trace.HebTraceContext;

/**
 * Listener that will initialize components when a new
 * App Engine instance is created with a warmup request.
 */
@Component
public class ApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {

	private static final Logger logger = Logger.getLogger(ApplicationReadyListener.class.getName());
	private static final HebFlexTracer TRACER = HebFlexTracer.getTracer(ApplicationReadyListener.class);

	/**
	 * This is invoked as part of a warmup request, or the first user request if no warmup request was invoked.
	 */
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event){
		HebTraceContext context = TRACER.startSpan("onApplicationEvent");
		if (logger.isLoggable(Level.INFO)) {
			logger.info("New App Engine instance initializing... InstanceId: " + HebEnvironmentProperties.getInstance().getInstanceId());
		}
		// Initialize the data layer
		try {
			DataStore.getInstance().warmup();
			HebMemoryStoreCache.getInstance().warmup();
		} catch (IllegalStateException e) {
			logger.log(Level.SEVERE, "Failure during App Engine warmup", e);
		} finally {
			TRACER.endSpan(context);
		}
	}
}
