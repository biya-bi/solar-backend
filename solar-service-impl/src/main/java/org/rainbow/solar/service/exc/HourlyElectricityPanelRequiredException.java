/**
 *
 */
package org.rainbow.solar.service.exc;

import org.rainbow.solar.service.util.ExceptionMessagesResourceBundle;

/**
 * @author biya-bi
 *
 */
public class HourlyElectricityPanelRequiredException extends HourlyElectricityException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9189602017844659175L;

	public HourlyElectricityPanelRequiredException() {
		super(ExceptionMessagesResourceBundle.getMessage("hourly.electricity.panel.required"));
	}
}
