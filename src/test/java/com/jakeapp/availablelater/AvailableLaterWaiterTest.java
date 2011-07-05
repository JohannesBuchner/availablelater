package com.jakeapp.availablelater;

import junit.framework.Assert;

import org.junit.Test;

public class AvailableLaterWaiterTest {

	public static class AvailablesProvider {

		public static AvailableLater<Boolean> provideError() {
			return new AvailableErrorObject<Boolean>(new Exception("myerror"));
		}

		public static AvailableLater<Boolean> provideLater() {
			return new AvailableLaterObject<Boolean>() {

				@Override
				public Boolean calculate() throws Exception {
					Thread.sleep(1);
					return true;
				}
			}.start();
		}

		public static AvailableLater<Boolean> provideNow() {
			return new AvailableNowObject<Boolean>(true);
		}
	}

	@Test(timeout = 1000, expected = Exception.class)
	public void testAvailableError() throws Exception {
		AvailableLaterWaiter.await(AvailablesProvider.provideError());
	}

	@Test(timeout = 1000)
	public void testAvailableLater() throws Exception {
		Assert.assertTrue(AvailableLaterWaiter.await(AvailablesProvider
				.provideLater()));
	}

	@Test(timeout = 1000)
	public void testAvailableNow() throws Exception {
		Assert.assertTrue(AvailableLaterWaiter.await(AvailablesProvider
				.provideNow()));
	}

}
