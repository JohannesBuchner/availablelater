package com.jakeapp.availablelater;

import java.util.concurrent.Semaphore;

/**
 * The {@link AvailableLaterWaiter} provides blocking until a
 * {@link AvailableLaterObject} is complete.
 * 
 * @author johannes
 * 
 * @param <T>
 */
public class AvailableLaterWaiter<T> {

	/**
	 * Waits for the result and returns it (or throws the received exception).
	 * This will block until the {@link AvailableLaterObject} finished its
	 * calculation.
	 * 
	 * @param <TT>
	 * @param avl
	 * @return
	 * @throws Exception
	 */
	public static <TT> TT await(AvailableLaterObject<TT> avl) throws Exception {
		return new AvailableLaterWaiter<TT>(avl).get();
	}

	private Semaphore s = new Semaphore(0);
	private Exception exception = null;
	private T result = null;
	private AvailableLaterObject<T> avl;

	protected AvailableLaterWaiter(AvailableLaterObject<T> avl) {
		this.avl = avl;
		avl.start();

		avl.setListener(new AvailabilityListener<T>() {

			@Override
			public void error(Exception t) {
				AvailableLaterWaiter.this.exception = t;
				s.release();
			}

			@Override
			public void finished(T o) {
				AvailableLaterWaiter.this.result = o;
				s.release();
			}

			@Override
			public void statusUpdate(StatusUpdate p) {
			}

		});
		while (true) {
			try {
				s.acquire();
				break;
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	protected T get() throws Exception {
		if (this.exception != null)
			throw this.exception;
		return avl.get();
	}
}
