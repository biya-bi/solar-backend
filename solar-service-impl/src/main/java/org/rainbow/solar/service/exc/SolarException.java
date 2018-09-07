/**
 *
 */
package org.rainbow.solar.service.exc;

/**
 * @author biya-bi
 *
 */
public class SolarException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6055232256551268422L;

	public SolarException() {
		super();
	}

	public SolarException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SolarException(String message, Throwable cause) {
		super(message, cause);
	}

	public SolarException(String message) {
		super(message);
	}

	public SolarException(Throwable cause) {
		super(cause);
	}

}
