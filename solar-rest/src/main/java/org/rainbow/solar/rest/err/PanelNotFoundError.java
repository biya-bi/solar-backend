/**
 * 
 */
package org.rainbow.solar.rest.err;

import org.rainbow.solar.rest.util.ErrorMessagesResourceBundle;

/**
 * @author biya-bi
 *
 */
public class PanelNotFoundError extends EntityNotFoundError<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8231707848263462645L;

	// We have intentionally made this constructor private so that it should be used
	// only during deserialization.
	@SuppressWarnings("unused")
	private PanelNotFoundError() {
	}

	public PanelNotFoundError(Long id) {
		super(SolarErrorCode.PANEL_ID_NOT_FOUND.value(),
				String.format(ErrorMessagesResourceBundle.getMessage("panel.id.not.found"), id), id);
	}

}
