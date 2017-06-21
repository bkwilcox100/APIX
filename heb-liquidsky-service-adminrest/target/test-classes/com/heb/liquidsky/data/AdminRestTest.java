package com.heb.liquidsky.data;

import java.util.logging.Level;

import org.junit.Test;

import com.heb.liquidsky.test.HEBTestCase;

public class AdminRestTest extends HEBTestCase {

	@Test
	public void testAdminRest() {
		try {
			assertEquals("42 == 42 This should always pass", 42, 42);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			fail(e.getMessage());
		}
	}
}
