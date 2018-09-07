/**
 *
 */
package org.rainbow.solar.service.exc;

import org.rainbow.solar.service.util.ExceptionMessagesResourceBundle;

/**
 * @author biya-bi
 *
 */
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
