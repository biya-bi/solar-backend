/**
 *
 */
package org.rainbow.solar.rest.err;

/**
 * @author biya-bi
 *
 */
public class PanelSerialDuplicateError extends SolarError {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6940735943980033552L;
	private String serial;

	// We have intentionally made this constructor private so that it should be used
	// only during deserialization.
	@SuppressWarnings("unused")
	private PanelSerialDuplicateError() {
	}

	public PanelSerialDuplicateError(String serial, String message) {
		super(SolarErrorCode.PANEL_SERIAL_DUPLICATE.value(), message);
		this.serial = serial;
	}

	public String getSerial() {
		return serial;
	}

}
