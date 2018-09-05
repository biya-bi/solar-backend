package org.rainbow.solar.util;

import java.util.ResourceBundle;

import org.rainbow.solar.SolarApplication;

public class EndpointMessagesResourceBundle {

	private static final ResourceBundle resourceBundle = ResourceBundle
			.getBundle(SolarApplication.class.getPackage().getName() + ".EndpointMessages");

	public static String getMessage(String key) {
		return resourceBundle.getString(key);
	}
}
