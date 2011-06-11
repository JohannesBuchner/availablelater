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
public class AvailableLaterWaiter<T> implements AvailabilityListener<T> {

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
	public static <TT> TT await(AvailableLater<TT> avl) throws Exception {
		return new AvailableLaterWaiter<TT>(avl).get();
	}

	private Semaphore s = new Semaphore(0);
	private Exception exception = null;
	private AvailableLater<T> avl;

	protected AvailableLaterWaiter(AvailableLater<T> avl) {
		this.avl = avl;
		avl.setListener(this);
		while (true) {
			try {
				this.s.acquire();
				break;
			} catch (InterruptedException e) {
				// that's why we have the while
			}
		}
	}

	@Override
	public void error(Exception t) {
		AvailableLaterWaiter.this.exception = t;
		AvailableLaterWaiter.this.s.release();
	}

	@Override
	public void finished(T o) {
		AvailableLaterWaiter.this.s.release();
	}

	@Override
	public void statusUpdate(StatusUpdate p) {
		// no status update necessary, we only wait for the result
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	protected T get() throws Exception {
		if (this.exception != null)
			throw this.exception;
		return this.avl.get();
	}
}
