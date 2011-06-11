package com.jakeapp.availablelater;

/**
 * An {@link AvailableLaterObject} that immediately reports an error.
 * 
 * This avoids creating a Thread and boilerplate using the
 * {@link AvailableLaterObject}, when the operation fails early.
 * 
 * @author christopher
 */
public class AvailableErrorObject<T> implements AvailableLater<T> {

	private Exception exception;

	/**
	 * @param ex
	 *            The error to report.
	 */
	public AvailableErrorObject(Exception exception) {
		this.exception = exception;
	}

	@Override
	public void setListener(AvailabilityListener<T> listener) {
		listener.error(this.exception);
	}

}
