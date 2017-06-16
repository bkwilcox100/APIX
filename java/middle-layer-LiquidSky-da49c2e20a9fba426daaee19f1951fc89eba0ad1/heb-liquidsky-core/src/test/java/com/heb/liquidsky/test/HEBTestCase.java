package com.heb.liquidsky.test;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;

import junit.framework.TestCase;

public abstract class HEBTestCase extends TestCase {

	protected static final Logger logger = Logger.getLogger(HEBTestCase.class.getName());

	protected void pause(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// ignore
		}
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
}
