/**
 * 
 */
package org.rainbow.solar.rest.converter;

import java.net.URI;

import org.rainbow.solar.RequestMappings;
import org.rainbow.solar.model.HourlyElectricity;
import org.rainbow.solar.rest.dto.HourlyElectricityDto;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * @author biya-bi
 *
 */
public class HourlyElectricityDtoConverter {
	private static final String HOURLY_URI_SUFFIX = "/hourly";

	public static HourlyElectricityDto toDto(Long panelId, HourlyElectricity hourlyElectricity) {
		ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentContextPath();
		URI uri = builder.path(RequestMappings.PANEL_ENDPOINT).path(panelId.toString())
				.path(HOURLY_URI_SUFFIX).path("/").path(hourlyElectricity.getId().toString()).build().toUri();
		return new HourlyElectricityDto(uri, hourlyElectricity.getGeneratedElectricity(),
				hourlyElectricity.getReadingAt());
	}

	public static HourlyElectricity fromDto(HourlyElectricityDto hourlyElectricityDto) {
		HourlyElectricity hourlyElectricity = new HourlyElectricity();
		hourlyElectricity.setReadingAt(hourlyElectricityDto.getReadingAt());
		return hourlyElectricity;
	}
}
