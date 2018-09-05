package org.rainbow.solar.exceptions;

import org.rainbow.solar.util.ExceptionMessagesResourceBundle;

public class HourlyElectricityReadingRequiredException extends HourlyElectricityException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8066881680883255531L;

	public HourlyElectricityReadingRequiredException() {
		super(ExceptionMessagesResourceBundle.getMessage("hourly.electricity.reading.required"));
	}
}
