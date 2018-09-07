/**
 *
 */
package org.rainbow.solar.rest.util;

import java.util.ResourceBundle;

/**
 * @author biya-bi
 *
 */
public class ErrorMessagesResourceBundle {

	private static final ResourceBundle resourceBundle = ResourceBundle
			.getBundle("org.rainbow.solar.rest.ErrorMessages");

	public static String getMessage(String key) {
		return resourceBundle.getString(key);
	}
}