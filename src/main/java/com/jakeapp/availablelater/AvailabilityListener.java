package com.jakeapp.availablelater;

/**
 * The {@link AvailableLaterObject} reports its status updates using this
 * 
 * @author johannes
 * @param <T>
 *            The type of the result (of the {@link AvailableLaterObject}
 *            calculation)
 */
public interface AvailabilityListener<T> {

	/**
	 * is called on progress updates
	 * 
	 * @param progress
	 *            by convention, a number between 0 and 1, referring to the
	 *            percent of completion.
	 * @param status
	 *            A string describing the current status. Ideally, this would be
	 *            a key like "init" "searching" that you can look up in your
	 *            I18N table and display.
	 */
	public void statusUpdate(StatusUpdate progress);

	/**
	 * The operation is finished, and this is the result. All calls to
	 * {@link AvailableLaterObject#get()} will return this result too, now.
	 * 
	 * @param o
	 */
	public void finished(T o);

	/**
	 * The operation failed by throwing this exception. All calls to
	 * {@link AvailableLaterObject#get()} will throw this exception now.
	 * 
	 * @param o
	 */
	public void error(Exception t);

}
