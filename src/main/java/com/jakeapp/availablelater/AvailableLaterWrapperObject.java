package com.jakeapp.availablelater;

/**
 * The {@link AvailableLaterWrapperObject} allows simple linking of two
 * {@link AvailableLaterObject}s. When the "source" availableLaterObject is
 * finished, the calculate of this object is called. Use {@link #getSource()} to
 * retrieve the original value.
 * 
 * @author christopher
 * 
 * @param <T>
 *            result type
 * @param <S>
 *            result type of source
 */
public abstract class AvailableLaterWrapperObject<T, S> extends
	AvailableLaterObject<T> implements AvailabilityListener<S> {

	private AvailableLaterObject<S> source;

	/**
	 * @param source
	 *            Result we are dependent on.
	 */
	public AvailableLaterWrapperObject(AvailableLaterObject<S> source) {
		this.source = source;
	}

	/**
	 * Implementer function to fetch the result of the source calculation.
	 */
	protected AvailableLaterObject<S> getSource() {
		return this.source;
	}

	/**
	 * do not call this.
	 */
	@Override
	public void error(Exception t) {
		getListener().error(t);
	}

	/**
	 * do not call this.
	 */
	@Override
	public void finished(S o) {
		/*
		 * The run method of the source has returned by reporting finished. We
		 * can not perform our calculation based on the intermediate results the
		 * source provided.
		 */

		// perform this class' calculation
		new Thread(this).start();
		// report finished to the listener that is listening to
		// this AvailableLaterObject.

		/*
		 * Do not call this - the run method should set a value and report
		 * 'finished'.
		 */
		// this.listener.finished();
	}

	@Override
	public void statusUpdate(StatusUpdate p) {
	}

	@Override
	public AvailableLaterObject<T> start() {
		this.getSource().start();
		return this;
	}
}
