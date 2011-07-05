package com.jakeapp.availablelater;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;
import local.test.Tracer;

import org.junit.Test;

public class AvailableLaterObjectTest {

	public static class AvailablesProvider {

		public static AvailableLater<Boolean> provideError() {
			return new AvailableErrorObject<Boolean>(new Exception("myerror"));
		}

		public static AvailableLater<BigInteger> provideFib(final int max) {
			return new AvailableLaterObject<BigInteger>() {

				// this should be quite inefficient ...
				@Override
				public BigInteger calculate() throws Exception {
					BigInteger[] v = new BigInteger[max];
					v[0] = BigInteger.ONE;
					v[1] = BigInteger.ONE;
					int i = 2;
					int nsteps = 10;
					for (int j = 1; j <= nsteps; j++) {
						while (i < j * max / nsteps) {
							Thread.sleep(1);
							v[i] = v[i - 1].add(v[i - 2]);
							i++;
						}
						setStatus(new StatusUpdate(j * 1.0 / nsteps,
								"looks good: " + v[i - 1]));
					}
					while (i < max) {
						Thread.sleep(1);
						v[i] = v[i - 1].add(v[i - 2]);
						i++;
					}
					return v[i - 1];
				}
			}.start();
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


		public static AvailableLater<String> provideLaterChain() {
			String[] words = { "ist", "das", "Haus", "vom", "Nikolaus" };
			AvailableLaterObject<String> lastavl = new AvailableLaterObject<String>() {

				@Override
				public String calculate() throws Exception {
					return "Das";
				}

			};

			AvailableLaterObject<String> avl1;
			for (final String word : words) {
				avl1 = new AvailableLaterWrapperObject<String, String>(
						lastavl.start()) {

					@Override
					public String calculate() throws Exception {
						return getSourceResult() + " " + word;
					}
				};
				lastavl = avl1;
			}
			return lastavl.start();
		}

		public static AvailableLater<Boolean> provideLaterDoubleStart() {
			AvailableLaterObject<Boolean> avl = new AvailableLaterObject<Boolean>() {

				@Override
				public Boolean calculate() throws Exception {
					Thread.sleep(1);
					return true;
				}
			};
			avl.start();
			avl.start();
			return avl;
		}

		public static AvailableLater<Boolean> provideLaterError() {
			return new AvailableLaterObject<Boolean>() {

				@SuppressWarnings("null")
				@Override
				public Boolean calculate() throws Exception {
					Thread.sleep(20);
					String a = null;
					a.toString();
					return true;
				}
			}.start();
		}

		public static AvailableLater<String> provideLaterWrap() {
			AvailableLater<String> parent = new AvailableNowObject<String>(
					"bar");
			AvailableLaterObject<String> avl = new AvailableLaterWrapperObject<String, String>(
					parent) {

				@Override
				public String calculate() throws Exception {
					return "foo" + getSourceResult();
				}
			};
			avl.start();
			return avl;
		}

		public static AvailableLater<String> provideLaterWrapException() {

			AvailableLater<String> parent = new AvailableErrorObject<String>(
					new NullPointerException());
			AvailableLaterObject<String> avl = new AvailableLaterWrapperObject<String, String>(
					parent) {

				@Override
				public String calculate() throws Exception {
					try {
						getSourceResult();
					} catch (NullPointerException e) {
						return "nullish";
					}
					return "bar";
				}
			};
			avl.start();
			return avl;
		}

		public static AvailableLater<Boolean> provideNow() {
			return new AvailableNowObject<Boolean>(true);
		}
	}

	private static final int FIB_MAX = 1000;

	@Test
	public void testAvailableError() {
		final Tracer tracer = new Tracer();

		AvailableLater<Boolean> avl = AvailablesProvider.provideError();
		avl.setListener(new TracingListener<Boolean>(tracer));
		Assert.assertTrue(tracer.await("error: myerror", 100,
				TimeUnit.MILLISECONDS));
		Assert.assertTrue(tracer.isDone());
	}

	@Test
	public void testAvailableFib() {
		final Tracer tracer = new Tracer();
		AvailableLater<BigInteger> avl = AvailablesProvider.provideFib(FIB_MAX);
		avl.setListener(new AvailabilityListener<BigInteger>() {

			@Override
			public void error(Exception t) {
				tracer.step("failure!!");
				t.printStackTrace();
			}

			@Override
			public void finished(BigInteger o) {
				tracer.step("done");
				tracer.step(o.toString());
			}

			@Override
			public void statusUpdate(StatusUpdate u) {
				if (u.getProgress() == 0.5) {
					tracer.step("half done");
				}
				tracer.step(u.getProgress() + " - " + u.getStatus());
			}

		});
		Assert.assertTrue(tracer
				.await("half done", 2000, TimeUnit.MILLISECONDS));
		Assert.assertTrue(tracer.await("done", 2000, TimeUnit.MILLISECONDS));
		// Assert.assertTrue(tracer.await("1556111435", 1000,
		// TimeUnit.MILLISECONDS));
	}

	@Test
	public void testAvailableLater() {
		final Tracer tracer = new Tracer();

		AvailableLater<Boolean> avl = AvailablesProvider.provideLater();
		avl.setListener(new TracingListener<Boolean>(tracer));
		Assert.assertTrue(tracer
				.await("done: true", 100, TimeUnit.MILLISECONDS));
		Assert.assertTrue(tracer.isDone());
	}

	@Test
	public void testAvailableLater_withError() {
		final Tracer tracer = new Tracer();

		AvailableLater<Boolean> avl = AvailablesProvider.provideLaterError();
		avl.setListener(new TracingListener<Boolean>(tracer));
		Assert.assertTrue(tracer.await("error: null", 100,
				TimeUnit.MILLISECONDS));
		Assert.assertTrue(tracer.isDone());
	}

	@Test
	public void testAvailableLaterDoubleStart() {
		final Tracer tracer = new Tracer();

		AvailableLater<Boolean> avl = AvailablesProvider.provideLater();
		avl.setListener(new TracingListener<Boolean>(tracer));
		Assert.assertTrue(tracer
				.await("done: true", 100, TimeUnit.MILLISECONDS));
		Assert.assertTrue(tracer.isDone());
	}

	@Test
	public void testAvailableNow() {
		final Tracer tracer = new Tracer();

		AvailableLater<Boolean> avl = AvailablesProvider.provideNow();
		avl.setListener(new TracingListener<Boolean>(tracer));
		Assert.assertTrue(tracer
				.await("done: true", 100, TimeUnit.MILLISECONDS));
		Assert.assertTrue(tracer.isDone());
	}

	@Test
	public void testChainingAction() {
		final Tracer tracer = new Tracer();
		AvailableLater<String> avl = AvailablesProvider.provideLaterChain();
		avl.setListener(new AvailabilityListener<String>() {

			@Override
			public void error(Exception t) {
				tracer.step("error");
			}

			@Override
			public void finished(String o) {
				tracer.step(o);
			}

			@Override
			public void statusUpdate(StatusUpdate u) {
				// we don't look at the status
			}

		});
		Assert.assertTrue(tracer.await("Das ist das Haus vom Nikolaus", 500,
				TimeUnit.MILLISECONDS));
		Assert.assertTrue(tracer.isDone());
	}

	@Test
	public void testWrap() {
		final Tracer tracer = new Tracer();
		AvailableLater<String> avl = AvailablesProvider.provideLaterWrap();
		avl.setListener(new TracingListener<String>(tracer));
		Assert.assertTrue(tracer.await("done: foobar", 100,
				TimeUnit.MILLISECONDS));
		Assert.assertTrue(tracer.isDone());
	}

	@Test
	public void testWrapException() {
		final Tracer tracer = new Tracer();
		AvailableLater<String> avl = AvailablesProvider
				.provideLaterWrapException();
		avl.setListener(new TracingListener<String>(tracer));
		Assert.assertTrue(tracer.await("done: nullish", 100,
				TimeUnit.MILLISECONDS));
		Assert.assertTrue(tracer.isDone());
	}
}
