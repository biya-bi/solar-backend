/**
 *
 */
package org.rainbow.solar.service.exc;

/**
 * @author biya-bi
 *
 */
public class HourlyElectricityException extends SolarException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1066708881275873461L;

	public HourlyElectricityException() {
		super();
	}

	public HourlyElectricityException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public HourlyElectricityException(String message, Throwable cause) {
		super(message, cause);
	}

	public HourlyElectricityException(String message) {
		super(message);
	}

	public HourlyElectricityException(Throwable cause) {
		super(cause);
	}

}
