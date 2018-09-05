/**
 * 
 */
package org.rainbow.solar.dto;

/**
 * @author biya-bi
 *
 */
public class HourlyElectricityReadingDateRequiredError extends SolarError {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7251947586822843211L;

	// We have intentionally made this constructor private so that it should be used
	// only during deserialization.
	@SuppressWarnings("unused")
	private HourlyElectricityReadingDateRequiredError() {
	}

	public HourlyElectricityReadingDateRequiredError(String message) {
		super(SolarErrorCode.HOURLY_ELECTRICITY_READING_DATE_REQUIRED.value(),
				message);
	}

}
