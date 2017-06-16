package com.heb.liquidsky.messaging;

import java.io.IOException;
import java.util.logging.Level;

import org.junit.Test;

import com.heb.liquidsky.test.HEBTestCase;

public class CloudMessagingTest extends HEBTestCase {

	@Test
	public void testSendMessage() {
		FcmMessage message = FcmMessage.initializeForTopic("/topics/liquidsky_sku__all");
		try {
			CloudMessaging.getInstance().publishAsync(message);
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			fail(e.getMessage());
		}
	}
}
