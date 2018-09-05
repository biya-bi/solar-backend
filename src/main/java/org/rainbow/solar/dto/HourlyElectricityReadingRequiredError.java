/**
 * 
 */
package org.rainbow.solar.dto;

/**
 * @author biya-bi
 *
 */
public class HourlyElectricityReadingRequiredError extends SolarError {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7163533970320437127L;

	// We have intentionally made this constructor private so that it should be used
	// only during deserialization.
	@SuppressWarnings("unused")
	private HourlyElectricityReadingRequiredError() {
	}

	public HourlyElectricityReadingRequiredError(String message) {
		super(SolarErrorCode.HOURLY_ELECTRICITY_READING_REQUIRED.value(),
				message);
	}

}
