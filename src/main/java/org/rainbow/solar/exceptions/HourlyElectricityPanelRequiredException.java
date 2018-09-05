package org.rainbow.solar.exceptions;

import org.rainbow.solar.util.ExceptionMessagesResourceBundle;

public class HourlyElectricityPanelRequiredException extends HourlyElectricityException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 9189602017844659175L;

	public HourlyElectricityPanelRequiredException() {
		super(ExceptionMessagesResourceBundle.getMessage("hourly.electricity.panel.required"));
	}
}
