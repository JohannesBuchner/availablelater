package com.jakeapp.availablelater;

/**
 * {@link AvailableLater}s provide a method of providing the result of a
 * calculation later. The {@link AvailableLater} is returned immediately, while
 * the calculation runs asynchronously.
 * 
 * The supplied listener, a {@link AvailabilityListener} tells when the result
 * is done or had an error.
 * 
 * @author johannes
 * @param <T>
 *            result type
 * @see AvailableLaterWaiter
 */
public interface AvailableLater<T> {

	/**
	 * Caller function: What should be called when done?
	 * 
	 * @param listener
	 */
	public abstract void setListener(AvailabilityListener<T> listener);

}