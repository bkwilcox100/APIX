package com.heb.liquidsky.spring.web;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

	private static final Logger logger = Logger.getLogger(HealthCheckController.class.getName());
	/* do not log memory usage details if memory usage is below this percentage usage */
	private static final double MEMORY_USAGE_IGNORE_THRESHOLD = 0.35;
	/* log memory usage at info level if memory usage is below this percentage usage */
	private static final double MEMORY_USAGE_INFO_THRESHOLD = 0.5;
	/* log memory usage at warning level if memory usage is below this percentage usage */
	private static final double MEMORY_USAGE_WARNING_THRESHOLD = 0.8;

	/**
	 * <a href="https://cloud.google.com/appengine/docs/flexible/java/how-instances-are-managed#health_checking">
	 * App Engine health checking</a> requires responding with 200 to {@code /_ah/health}.
	 */
	@GetMapping("/_ah/health")
	public String healthy() {
		this.logMemoryUsage();
		// Message body required though ignored
		return "Still surviving.";
	}

	/**
	 * If memory usage is getting high record some numbers for debugging
	 * purposes.
	 */
	private void logMemoryUsage() {
		Runtime instance = Runtime.getRuntime();
		long total = instance.totalMemory();
		long free = instance.freeMemory();
		long used = (total - free);
		double percentUsed = used / total;
		if (percentUsed < MEMORY_USAGE_IGNORE_THRESHOLD) {
			return;
		}
		long max = instance.maxMemory();
		int mb = 1024 * 1024;
		String msg = "Memory Used: " + percentUsed;
		msg += " / Total Memory: " + (total / mb);
		msg += " / Free Memory: " + (free / mb);
		msg += " / Used Memory: " + (used / mb);
		msg += " / Max Memory: " + (max / mb);
		Level level = Level.FINE;
		if (percentUsed > MEMORY_USAGE_WARNING_THRESHOLD) {
			level = Level.WARNING;
		} else if (percentUsed > MEMORY_USAGE_INFO_THRESHOLD) {
			level = Level.INFO;
		}
		logger.log(level, msg);
	}
}
