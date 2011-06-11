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

	private AvailableLater<S> source;
	private S sourceResult;
	private Exception sourceException;

	/**
	 * fetch the result of the source calculation
	 * 
	 * @return
	 * @throws Exception
	 *             if the source throw an exception
	 */
	protected S getSourceResult() throws Exception {
		if (this.sourceException != null)
			throw this.sourceException;
		return this.sourceResult;

	}

	/**
	 * @param source
	 *            Result we are dependent on.
	 */
	public AvailableLaterWrapperObject(AvailableLater<S> source) {
		this.source = source;
		this.source.setListener(this);
	}

	/**
	 * @param source
	 *            Result we are dependent on.
	 */
	public AvailableLaterWrapperObject(AvailableLaterObject<S> source) {
		this.source = source.start();
		this.source.setListener(this);
	}

	/**
	 * do not call this.
	 */
	@Override
	public void error(Exception t) {
		this.sourceException = t;
		// now we can start
		new Thread(this).start();
	}

	/**
	 * do not call this.
	 */
	@Override
	public void finished(S o) {
		this.sourceResult = o;
		// now we can start
		new Thread(this).start();
	}

	@Override
	public void statusUpdate(StatusUpdate p) {
		setStatus(p);
	}

	@Override
	public AvailableLater<T> start() {
		// we can't start, we have to wait for the source
		return this;
	}
}
