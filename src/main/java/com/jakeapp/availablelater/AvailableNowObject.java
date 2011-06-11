package com.jakeapp.availablelater;


/**
 * A {@link AvailableLaterObject} where the calculation is not asynchronous. To
 * use when the result is available immediately, and it is not worth creating a
 * Thread.
 * 
 * @author johannes
 * @param <T>
 *            result type.
 */
public class AvailableNowObject<T> implements AvailableLater<T> {

	private T value;

	public AvailableNowObject(T value) {
		this.value = value;
	}

	@Override
	public void setListener(AvailabilityListener<T> listener) {
		listener.finished(this.value);
	}

}
