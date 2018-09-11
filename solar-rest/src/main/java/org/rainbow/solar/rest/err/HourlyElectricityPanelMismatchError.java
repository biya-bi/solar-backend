/**
 * 
 */
package org.rainbow.solar.rest.err;

/**
 * @author biya-bi
 *
 */
public class HourlyElectricityPanelMismatchError extends SolarError {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4806637805945921016L;

	private long hourlyElectricityId;
	private long panelId;

	// We have intentionally made this constructor private so that it should be used
	// only during deserialization.
	@SuppressWarnings("unused")
	private HourlyElectricityPanelMismatchError() {
	}

	public HourlyElectricityPanelMismatchError(long hourlyElectricityId, long panelId, String message) {
		super(SolarErrorCode.HOURLY_ELECTRICITY_PANEL_MISMATCH.value(), message);
		this.hourlyElectricityId = hourlyElectricityId;
		this.panelId = panelId;
	}

	public long getHourlyElectricityId() {
		return hourlyElectricityId;
	}

	public void setHourlyElectricityId(long hourlyElectricityId) {
		this.hourlyElectricityId = hourlyElectricityId;
	}

	public long getPanelId() {
		return panelId;
	}

	public void setPanelId(long panelId) {
		this.panelId = panelId;
	}

}
