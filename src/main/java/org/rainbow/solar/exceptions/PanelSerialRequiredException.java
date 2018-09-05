package org.rainbow.solar.exceptions;

import org.rainbow.solar.util.ExceptionMessagesResourceBundle;

public class PanelSerialRequiredException extends PanelException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4561473998797722353L;

	public PanelSerialRequiredException() {
		super(ExceptionMessagesResourceBundle.getMessage("panel.serial.required"));
	}
}
