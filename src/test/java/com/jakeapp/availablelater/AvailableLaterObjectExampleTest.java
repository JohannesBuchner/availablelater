package com.jakeapp.availablelater;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @see AvailableLaterObjectExample
 * 
 * @author Johannes Buchner
 */
public class AvailableLaterObjectExampleTest {

	public static AvailableLater<String> calcSlowly(final int param1) {
		return new AvailableLaterObject<String>() {

			@Override
			public String calculate() throws Exception {
				if (param1 == 0) {
					throw new Exception("foo");
				}
				setStatus(new StatusUpdate(0.5, "bla"));
				return "Hello World";
			}
		}.start();
	}

	@Test
	public void testAsyncCall() throws Exception {
		AvailableLaterWaiter.await(calcSlowly(1));
	}

	@Test(expected = Exception.class)
	public void testAsyncException() throws Exception {
		AvailableLaterWaiter.await(calcSlowly(0));
	}

	@Test
	public void testBlockingCall() throws Exception {
		Assert.assertEquals(AvailableLaterWaiter
				.await(AvailableLaterObjectExample.calcQuickly(1)),
				"Hello World");
	}

	@Test(expected = Exception.class)
	public void testBlockingException() throws Exception {
		AvailableLaterWaiter.await(AvailableLaterObjectExample.calcQuickly(0));
	}

}
