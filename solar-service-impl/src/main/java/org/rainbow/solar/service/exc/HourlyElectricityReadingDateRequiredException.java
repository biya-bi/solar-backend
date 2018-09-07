/**
 *
 */
package org.rainbow.solar.service.exc;

import org.rainbow.solar.service.util.ExceptionMessagesResourceBundle;

/**
 * @author biya-bi
 *
 */
public class HourlyElectricityReadingDateRequiredException extends HourlyElectricityException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3413334749884638140L;

	public HourlyElectricityReadingDateRequiredException() {
		super(ExceptionMessagesResourceBundle.getMessage("hourly.electricity.reading.date.required"));
	}
}
