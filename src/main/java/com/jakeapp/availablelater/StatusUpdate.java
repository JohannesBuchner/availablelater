/**
 * 
 */
package com.jakeapp.availablelater;

public class StatusUpdate {

	private final double progress;

	private final String status;

	/**
	 * 
	 * @param p
	 *            current progress (0 for initializing, 1 for done 100%)
	 * @param s
	 *            current status (use something that can be used as a NLS lookup
	 *            key and/or displayed to the user. Assume that the context of
	 *            the current action is displayed alongside with this text, so
	 *            the generic "starting" and "working" should do in most cases.
	 *            More specific examples: "connecting", "waiting".
	 */
	public StatusUpdate(double p, String s) {
		super();
		progress = p;
		status = s;
	}

	public double getProgress() {
		return progress;
	}

	public String getStatus() {
		return status;
	}
}