/**
 *
 */
package org.rainbow.solar.service.util;

import java.util.ResourceBundle;

/**
 * @author biya-bi
 *
 */
public class ExceptionMessagesResourceBundle {

	private static final ResourceBundle resourceBundle = ResourceBundle
			.getBundle("org.rainbow.solar.service.ExceptionMessages");

	public static String getMessage(String key) {
		return resourceBundle.getString(key);
	}
}
