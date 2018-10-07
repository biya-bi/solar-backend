/**
 * 
 */
package org.rainbow.solar.rest.converter;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;

import org.rainbow.solar.model.HourlyElectricity;
import org.rainbow.solar.rest.controller.PanelController;
import org.rainbow.solar.rest.dto.HourlyElectricityDto;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * @author biya-bi
 *
 */
public class HourlyElectricityDtoConverter {
	private static final String HOURLY_URI_SUFFIX = "/hourly";

	public static HourlyElectricityDto toDto(Long panelId, HourlyElectricity hourlyElectricity) {
		URI panelUri = linkTo(methodOn(PanelController.class).getById(panelId)).toUri();
		URI uri = ServletUriComponentsBuilder.fromUri(panelUri).path(HOURLY_URI_SUFFIX).path("/").path(hourlyElectricity.getId().toString()).build().toUri();
		return new HourlyElectricityDto(uri, hourlyElectricity.getGeneratedElectricity(),
				hourlyElectricity.getReadingAt());
	}

	public static HourlyElectricity fromDto(HourlyElectricityDto hourlyElectricityDto) {
		return new HourlyElectricity(hourlyElectricityDto.getGeneratedElectricity(),
				hourlyElectricityDto.getReadingAt());
	}
}
