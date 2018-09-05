package org.rainbow.solar.util;

import java.util.ResourceBundle;

import org.rainbow.solar.SolarApplication;

public class ExceptionMessagesResourceBundle {

	private static final ResourceBundle resourceBundle = ResourceBundle
			.getBundle(SolarApplication.class.getPackage().getName() + ".ExceptionMessages");

	public static String getMessage(String key) {
		return resourceBundle.getString(key);
	}
}
