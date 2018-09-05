package org.rainbow.solar.exceptions;

import org.rainbow.solar.util.ExceptionMessagesResourceBundle;

public class PanelSerialMaxLengthExceededException extends PanelException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3413708555358479858L;

	private final String serial;
	private final int maxLength;

	public PanelSerialMaxLengthExceededException(String serial, int maxLength) {
		super(String.format(ExceptionMessagesResourceBundle.getMessage("panel.serial.length.too.long"), serial, maxLength));
		this.serial = serial;
		this.maxLength = maxLength;
	}

	public String getSerial() {
		return serial;
	}

	public int getMaxLength() {
		return maxLength;
	}

}
