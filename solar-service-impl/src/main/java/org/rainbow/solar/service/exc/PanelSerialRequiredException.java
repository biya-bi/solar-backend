/**
 *
 */
package org.rainbow.solar.service.exc;

import org.rainbow.solar.service.util.ExceptionMessagesResourceBundle;

/**
 * @author biya-bi
 *
 */
public class PanelSerialRequiredException extends PanelException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4561473998797722353L;

	public PanelSerialRequiredException() {
		super(ExceptionMessagesResourceBundle.getMessage("panel.serial.required"));
	}
}
