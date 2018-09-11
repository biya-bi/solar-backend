/**
 * 
 */
package org.rainbow.solar.rest.err;

import org.rainbow.solar.rest.util.ErrorMessagesResourceBundle;

/**
 * @author biya-bi
 *
 */
public class HourlyElectricityNotFoundError extends EntityNotFoundError<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6799250254862177701L;

	// We have intentionally made this constructor private so that it should be used
	// only during deserialization.
	@SuppressWarnings("unused")
	private HourlyElectricityNotFoundError() {
	}

	public HourlyElectricityNotFoundError(Long id) {
		super(SolarErrorCode.HOURLY_ELECTRICITY_ID_NOT_FOUND.value(),
				String.format(ErrorMessagesResourceBundle.getMessage("hourly.electricity.id.not.found"), id), id);
	}

}
