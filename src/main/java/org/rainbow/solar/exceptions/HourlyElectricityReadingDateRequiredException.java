package org.rainbow.solar.exceptions;

import org.rainbow.solar.util.ExceptionMessagesResourceBundle;

public class HourlyElectricityReadingDateRequiredException extends HourlyElectricityException {


	/**
	 * 
	 */
	private static final long serialVersionUID = -3413334749884638140L;

	public HourlyElectricityReadingDateRequiredException() {
		super(ExceptionMessagesResourceBundle.getMessage("hourly.electricity.reading.date.required"));
	}
}
