package com.jakeapp.availablelater;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

/**
 * {@link AvailableLaterObject}s is an implementation of
 * {@link AvailabilityListener} that is similar to {@link Runnable}, except it
 * can deal with Exceptions.
 * 
 * Implementers of this class should override {@link #calculate()} with the
 * asynchronous calculation (like {@link Runnable#run()}.
 * 
 * You may call {@link #setStatus(StatusUpdate)} to notify the listener of the
 * current progress.
 * 
 * Make sure you call {@link #start()} after you create a
 * {@link AvailableLaterObject} and before you return it to the caller.
 * Otherwise, the {@link #calculate()} method will never be called.
 * 
 * @author johannes
 * @param <T>
 *            result type
 */
public abstract class AvailableLaterObject<T> implements Runnable,
		AvailableLater<T> {

	private static Logger log = Logger.getLogger(AvailableLaterObject.class);

	private AvailabilityListener<T> listener;

	protected Semaphore s = new Semaphore(0);

	private final AtomicBoolean alreadyStarted = new AtomicBoolean(false);

	/*
	 * for debugging purposes it is useful to know where this object was
	 * created.
	 */
	@SuppressWarnings("unused")
	private final StackTraceElement[] callerStackTrace = new Throwable()
			.getStackTrace();

	/**
	 * waits until a listener is set
	 */
	private void blockForListener() {
		if (this.listener == null) {
			try {
				this.s.acquire();
			} catch (InterruptedException e) {
				blockForListener();
			}
		}
	}

	/**
	 * Implementer method: The asynchronous operation to run.
	 */
	public abstract T calculate() throws Exception;

	/**
	 * Implementor function: access to the listener
	 * 
	 * @return
	 */
	private AvailabilityListener<T> getListener() {
		blockForListener();
		return this.listener;
	}

	protected boolean isAlreadyStarted() {
		return this.alreadyStarted.get();
	}

	/**
	 * Don't call this function
	 */
	@Override
	public void run() {
		log.debug("Running " + this.getClass().getSimpleName());
		try {
			this.set(this.calculate());
		} catch (Exception e) {
			log.warn("Calculation failed with an error: ", e);
			getListener().error(e);
		}
	}

	/* server functions */
	protected void set(T o) {
		getListener().finished(o);
	}

	private boolean setAlreadyStarted() {
		return this.alreadyStarted.getAndSet(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jakeapp.availablelater.AvailableLater#setListener(com.jakeapp.
	 * availablelater.AvailabilityListener)
	 */
	@Override
	public void setListener(AvailabilityListener<T> listener) {
		this.listener = listener;
		this.s.release();
	}

	protected void setStatus(StatusUpdate update) {
		this.getListener().statusUpdate(update);
	}

	/**
	 * Caller function: Starts the Thread and returns the object itself.
	 * 
	 * Safe of multiple calls.
	 * 
	 * @return the object itself
	 */
	public AvailableLater<T> start() {
		if (!this.setAlreadyStarted()) {
			new Thread(this).start();
		}

		return this;
	}

}