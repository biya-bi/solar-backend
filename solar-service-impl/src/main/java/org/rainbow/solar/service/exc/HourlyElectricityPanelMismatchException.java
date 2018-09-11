/**
 *
 */
package org.rainbow.solar.service.exc;

import org.rainbow.solar.service.util.ExceptionMessagesResourceBundle;

/**
 * @author biya-bi
 *
 */
public class HourlyElectricityPanelMismatchException extends HourlyElectricityException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 174658330597150881L;

	private final Long hourlyElectricityId;
	private final Long panelId;

	public HourlyElectricityPanelMismatchException(Long hourlyElectricityId, Long panelId) {
		super(String.format(ExceptionMessagesResourceBundle.getMessage("hourly.electricity.panel.mismatch"),
				hourlyElectricityId, panelId));
		this.hourlyElectricityId = hourlyElectricityId;
		this.panelId = panelId;
	}

	public Long getHourlyElectricityId() {
		return hourlyElectricityId;
	}

	public Long getPanelId() {
		return panelId;
	}

}
