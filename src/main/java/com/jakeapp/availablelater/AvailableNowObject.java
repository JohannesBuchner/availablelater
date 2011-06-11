package com.jakeapp.availablelater;

import org.apache.log4j.Logger;

/**
 * A {@link AvailableLaterObject} where the calculation is not asynchronous. To
 * use when the result is available immediately, and it is not worth creating a
 * Thread.
 * 
 * @author johannes
 * @param <T>
 *            result type.
 */
public class AvailableNowObject<T> extends AvailableLaterObject<T> {
	private static final Logger log = Logger
		.getLogger(AvailableNowObject.class);

	/* server functions */
	@Override
	protected void set(T o) {
		this.setInnercontent(o);
	}

	@Override
	public void run() {
	}

	@Override
	protected AvailabilityListener<T> getListener() {
		return listener;
	}

	/**
	 * 
	 * @param content
	 *            value to return.
	 */
	public AvailableNowObject(T content) {
		log.debug("Creating AvailableNowObject with " + content);
		this.set(content);
	}

	@Override
	public T calculate() throws Exception {
		log.debug("Calculating AvailableNowObject - direct data return");
		return this.get();
	}

	@Override
	public void setListener(AvailabilityListener<T> listener) {
		listener.finished(this.get());
	}

	@Override
	public AvailableLaterObject<T> start() {
		log.debug("AvailableNowObject - returning myself now");
		return this;
	}
}
