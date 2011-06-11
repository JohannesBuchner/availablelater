package com.jakeapp.availablelater;

import org.apache.log4j.Logger;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link AvailableLaterObject}s provide a method of providing the result of a
 * calculation later. The {@link AvailableLaterObject} is returned immediately,
 * while the calculation runs asynchronously.
 * 
 * The supplied listener, a {@link AvailabilityListener} tells you when the
 * result is done or had an error.
 * 
 * Callee:
 * 
 * In {@link #run()}, implement the method that takes time. call
 * {@link #set(Object)}() when your done or the methods of
 * {@link AvailabilityListener} to notify the progress.
 * 
 * Call start() and return the object.
 * 
 * Caller:
 * 
 * set up a listener using {@link #setListener(AvailabilityListener)}.
 * 
 * @author johannes
 * @param <T>
 *            result type
 * @see AvailableLaterWaiter
 */
public abstract class AvailableLaterObject<T> implements Runnable,
	AvailableLater<T> {
	private static Logger log = Logger.getLogger(AvailableLaterObject.class);
	protected T innercontent;

	private AvailabilityListener<T> listener;

	protected Semaphore s = new Semaphore(0);

	private AtomicBoolean alreadyStarted = new AtomicBoolean(false);

	/*
	 * for debugging purposes it is useful to know where this object was
	 * created.
	 */
	@SuppressWarnings("unused")
	private StackTraceElement[] callerStackTrace = new Throwable()
		.getStackTrace();

	/* server functions */
	protected void set(T o) {
		this.setInnercontent(o);
		getListener().finished(o);
	}

	/**
	 * Implementer method: The asynchronous operation to run.
	 */
	public abstract T calculate() throws Exception;

	/**
	 * Don't call this function
	 */
	public void run() {
		log.debug("Running " + this.getClass().getSimpleName());
		try {
			this.set(this.calculate());
		} catch (Exception e) {
			log.warn("Calculation failed with an error: ", e);
			getListener().error(e);
		}
	}

	@Override
	public T get() {
		return getInnercontent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jakeapp.availablelater.AvailableLater#setListener(com.jakeapp.
	 * availablelater.AvailabilityListener)
	 */
	@Override
	public void setListener(AvailabilityListener<T> listener) {
		if (!isAlreadyStarted() && log.isInfoEnabled()) {
			log.info("The callee possibly forgot to call start()");
		}
		this.listener = listener;
		this.s.release();
	}

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
	 * Implementor function: access to the listener
	 * 
	 * @return
	 */
	protected AvailabilityListener<T> getListener() {
		blockForListener();
		return this.listener;
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
			// this.run();
		}

		return this;
	}

	private boolean setAlreadyStarted() {
		return this.alreadyStarted.getAndSet(true);
	}

	protected boolean isAlreadyStarted() {
		return this.alreadyStarted.get();
	}

	protected T getInnercontent() {
		return this.innercontent;
	}

	protected void setInnercontent(T innercontent) {
		this.innercontent = innercontent;
	}
}