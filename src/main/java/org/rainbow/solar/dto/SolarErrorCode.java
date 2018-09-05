/**
 * 
 */
package org.rainbow.solar.dto;

/**
 * @author biya-bi
 *
 */
public enum SolarErrorCode {
	UNPROCESSABLE_ENTITY(1),
	UNEXPECTED_ERROR(2),

	PANEL_ID_NOT_FOUND(1001), PANEL_SERIAL_REQUIRED(1002), PANEL_SERIAL_MAX_LENGTH_EXCEEDED(1003),
	PANEL_SERIAL_DUPLICATE(1004),

	HOURLY_ELECTRICITY_READING_DATE_REQUIRED(2001), HOURLY_ELECTRICITY_READING_REQUIRED(2002);

	private final int value;

	private SolarErrorCode(int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}

}
