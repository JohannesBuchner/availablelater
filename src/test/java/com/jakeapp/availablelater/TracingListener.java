/**
 * 
 */
package com.jakeapp.availablelater;

import org.apache.log4j.Logger;

import local.test.Tracer;

class TracingListener<V> implements AvailabilityListener<V> {

	private Tracer tracer;

	public TracingListener(Tracer tracer) {
		super();
		this.tracer = tracer;
	}

	@Override
	public void error(Exception t) {
		this.tracer.step("error: " + t.getMessage());
	}

	@Override
	public void finished(V o) {
		this.tracer.step("done: " + o.toString());
	}

	@Override
	public void statusUpdate(StatusUpdate progress) {
		// tracer.step("status update");
		// always nice, but never required
	}
}