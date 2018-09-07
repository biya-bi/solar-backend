/**
 *
 */
package org.rainbow.solar.service.exc;

import org.rainbow.solar.service.util.ExceptionMessagesResourceBundle;

/**
 * @author biya-bi
 *
 */
public class HourlyElectricityReadingRequiredException extends HourlyElectricityException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8066881680883255531L;

	public HourlyElectricityReadingRequiredException() {
		super(ExceptionMessagesResourceBundle.getMessage("hourly.electricity.reading.required"));
	}
}
