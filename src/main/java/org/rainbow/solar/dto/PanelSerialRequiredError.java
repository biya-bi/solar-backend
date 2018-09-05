/**
 * 
 */
package org.rainbow.solar.dto;

/**
 * @author biya-bi
 *
 */
public class PanelSerialRequiredError extends SolarError {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7945839901693582435L;

	// We have intentionally made this constructor private so that it should be used
	// only during deserialization.
	@SuppressWarnings("unused")
	private PanelSerialRequiredError() {
	}

	public PanelSerialRequiredError(String message) {
		super(SolarErrorCode.PANEL_SERIAL_REQUIRED.value(), message);
	}

}
