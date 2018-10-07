/**
 * 
 */
package org.rainbow.solar.rest.converter;

import org.rainbow.solar.model.Panel;
import org.rainbow.solar.model.UnitOfMeasure;
import org.rainbow.solar.rest.dto.PanelDto;
import org.rainbow.solar.rest.util.PanelHateoasUtil;

/**
 * @author biya-bi
 *
 */
public class PanelDtoConverter {
	public static PanelDto toDto(Panel panel) {
		PanelHateoasUtil panelHateoasUtil = new PanelHateoasUtil(panel.getId());

		return new PanelDto(panelHateoasUtil.buildUri(), panel.getSerial(), panel.getLatitude(), panel.getLongitude(),
				panel.getBrand(), panel.getUnitOfMeasure().toString(), panelHateoasUtil.buildHourlyUri(),
				panelHateoasUtil.buildDailyUri(), panelHateoasUtil.buildHourlyCountUri());
	}

	public static Panel fromDto(PanelDto panelDto) {
		return new Panel(panelDto.getSerial(), panelDto.getLatitude(), panelDto.getLongitude(), panelDto.getBrand(),
				panelDto.getUnitOfMeasure() != null ? UnitOfMeasure.valueOf(panelDto.getUnitOfMeasure()) : null);
	}
}
