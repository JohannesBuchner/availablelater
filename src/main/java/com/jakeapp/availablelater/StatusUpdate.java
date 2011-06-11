/**
 * 
 */
package com.jakeapp.availablelater;

public class StatusUpdate {
	public StatusUpdate(double progress, String status) {
		super();
		this.progress = progress;
		this.status = status;
	}

	private double progress;
	private String status;

	public double getProgress() {
		return progress;
	}

	public String getStatus() {
		return status;
	}
}