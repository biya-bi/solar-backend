/**
 * 
 */
package org.rainbow.solar.rest.converter;

import java.net.URI;

import org.rainbow.solar.model.HourlyElectricity;
import org.rainbow.solar.rest.dto.HourlyElectricityDto;
import org.rainbow.solar.rest.util.HourlyElectricityHateoasUtil;

/**
 * @author biya-bi
 *
 */
public class HourlyElectricityDtoConverter {
	public static HourlyElectricityDto toDto(Long panelId, HourlyElectricity hourlyElectricity) {
		URI uri = new HourlyElectricityHateoasUtil(hourlyElectricity.getId()).buildUri(panelId);
		return new HourlyElectricityDto(uri, hourlyElectricity.getGeneratedElectricity(),
				hourlyElectricity.getReadingAt());
	}

	public static HourlyElectricity fromDto(HourlyElectricityDto hourlyElectricityDto) {
		return new HourlyElectricity(hourlyElectricityDto.getGeneratedElectricity(),
				hourlyElectricityDto.getReadingAt());
	}
}
