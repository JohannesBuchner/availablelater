package com.jakeapp.availablelater;

/**
 * A documentary example on how to use AvailableLaterObjects.
 * 
 * @author Johannes Buchner
 */
public class AvailableLaterObjectExample {

	/**
	 * First, we show the "normal" blocking behaviour. This does not spawn a new
	 * Thread anywhere.
	 */
	public static AvailableLaterObject<String> calcQuickly(final int param1) {
		if (param1 == 0)
			// we throw an error.
			return new AvailableErrorObject<String>(new Exception("foo"));
		else
			// we just give back a result.
			return new AvailableNowObject<String>("Hello World");
	}

	static void callBlocking(int param1) {
		String v;
		try {
			// we just want the result, now! so use a AvailableLaterWaiter.
			v = AvailableLaterWaiter.await(calcQuickly(param1));
			System.out.println("Ah, the call finished. I got " + v);
		} catch (Exception e) {
			// we got a Exception:
			System.out.println("Oh no, an error occured: " + e);
		}
	}

	/*
	 * So the blocking call is pretty much like normal java calls, maybe a
	 * little more complicated. But you will see that asynchronour calls are
	 * much easier, than manually spawning threads:
	 */

	/**
	 * Here we show a callee that gives back a AvailableLaterObject.
	 * 
	 * The calculation is the same as above.
	 */
	public static AvailableLater<String> calcSlowly(final int param1) {
		return new AvailableLaterObject<String>() {

			@Override
			public String calculate() throws Exception {
				// this here is run in a seperate thread.
				Thread.sleep(1000);

				// we can throw exceptions, which the caller gets
				// a) through the listener as the error() call
				// b) when the caller calls get()
				if (param1 == 0)
					throw new Exception("foo");

				// we can provide simple progress information
				getListener().statusUpdate(new StatusUpdate(0.5, "bla"));
				Thread.sleep(1000);

				// we can just call return when we have finished the
				// calculation
				return "Hello World";
			}
		}.start();
	}

	public static void callAsynchronous(int param1) {
		// make the call
		AvailableLater<String> result = calcSlowly(param1);

		// here, the calculation is running, and we don't know the result yet.

		// please inform us of the result when you can:
		result.setListener(new AvailabilityListener<String>() {
			public void error(Exception e) {
				System.out.println("Oh no, an error occured: " + e);
			}

			public void finished(String o) {
				System.out.println("Ah, the call finished. I got " + o);
				// continue operation here.
			}

			public void statusUpdate(StatusUpdate u) {
				System.out.println("Task is still running: " + u.getProgress()
					+ " -- " + u.getStatus());
			}
		});
		// the calculation is running.
		System.out.println("Oh I wonder what the function will give back!");
		// here we can do something else
	}

	public static void main(String[] args) {
		System.out.println("Starting blocking calls");
		callBlocking(0);
		callBlocking(1);
		System.out.println("Starting asynchronous calls");
		callAsynchronous(0);
		callAsynchronous(1);
		// you should notice that the last two calls are running in parallel.
	}
}
