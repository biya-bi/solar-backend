package org.rainbow.solar.exceptions;

import org.rainbow.solar.util.ExceptionMessagesResourceBundle;

public class PanelSerialDuplicateException extends PanelException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9117305127269516411L;
	private final String serial;

	public PanelSerialDuplicateException(String serial) {
		super(String.format(ExceptionMessagesResourceBundle.getMessage("panel.serial.duplicate"), serial));
		this.serial = serial;
	}

	public String getSerial() {
		return serial;
	}

}
