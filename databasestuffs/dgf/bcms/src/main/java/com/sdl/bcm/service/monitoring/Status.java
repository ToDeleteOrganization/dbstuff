package com.sdl.bcm.service.monitoring;

public class Status {

	/**
	 * Specifies a valid status.
	 */
	public static final Status UP = new Status("UP");

	/**
	 * Specifies an invalid status.
	 */
    public static final Status DOWN = new Status("DOWN");
	
	private String status;

	private Status(String status) {
		this.status = status;
	}

	/**
	 * 
	 * @return Returns the current status.
	 */
	public String getStatus() {
		return status;
	}

}
