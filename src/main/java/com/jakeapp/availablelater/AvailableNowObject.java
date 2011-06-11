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
		// we don't need to do anything.
	}

	/**
	 * 
	 * @param content
	 *            value to return.
	 */
	public AvailableNowObject(T content) {
		if (log.isDebugEnabled())
			log.debug("Creating AvailableNowObject with " + content);
		this.set(content);
	}

	@Override
	public T calculate() throws Exception {
		return null;
	}

	@Override
	public void setListener(AvailabilityListener<T> listener) {
		listener.finished(this.get());
		super.setListener(listener);
	}

	@Override
	public AvailableLater<T> start() {
		if (log.isDebugEnabled())
			log.debug("AvailableNowObject - returning myself now");
		return this;
	}
}
