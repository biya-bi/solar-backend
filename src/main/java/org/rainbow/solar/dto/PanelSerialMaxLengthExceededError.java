package org.rainbow.solar.dto;

public class PanelSerialMaxLengthExceededError extends SolarError {

	/**
	 * 
	 */
	private static final long serialVersionUID = -188888066392632851L;
	private String serial;
	private int maxLength;

	// We have intentionally made this constructor private so that it should be used
	// only during deserialization.
	@SuppressWarnings("unused")
	private PanelSerialMaxLengthExceededError() {
	}

	public PanelSerialMaxLengthExceededError(String serial, int maxLength, String  message) {
		super(SolarErrorCode.PANEL_SERIAL_MAX_LENGTH_EXCEEDED.value(), message);
		this.serial = serial;
		this.maxLength = maxLength;
	}

	public String getSerial() {
		return serial;
	}

	public int getMaxLength() {
		return maxLength;
	}

}
