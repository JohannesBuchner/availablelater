/**
 * 
 */
package com.jakeapp.availablelater;

public class StatusUpdate {
	public StatusUpdate(double p, String s) {
		super();
		this.progress = p;
		this.status = s;
	}

	private double progress;
	private String status;

	public double getProgress() {
		return this.progress;
	}

	public String getStatus() {
		return this.status;
	}
}