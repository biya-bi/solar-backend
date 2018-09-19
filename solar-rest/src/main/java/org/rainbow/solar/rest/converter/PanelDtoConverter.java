/**
 * 
 */
package org.rainbow.solar.rest.converter;

import java.net.URI;

import org.rainbow.solar.model.Panel;
import org.rainbow.solar.model.UnitOfMeasure;
import org.rainbow.solar.rest.dto.PanelDto;
import org.rainbow.solar.rest.util.UriUtil;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * @author biya-bi
 *
 */
public class PanelDtoConverter {
	private static final String HOURLY_URI_SUFFIX = "/hourly";
	private static final String DAILY_URI_SUFFIX = "/daily";
	private static final String HOURLY_COUNT_URI_SUFFIX = "/count";

	public static PanelDto toDto(Panel panel) {
		URI uri = UriUtil.buildUri(panel.getId());
		URI hourlyUri = ServletUriComponentsBuilder.fromUri(uri).path(HOURLY_URI_SUFFIX).build().toUri();
		URI dailyUri = ServletUriComponentsBuilder.fromUri(uri).path(DAILY_URI_SUFFIX).build().toUri();
		URI hourlyCountUri = ServletUriComponentsBuilder.fromUri(uri).path(HOURLY_URI_SUFFIX)
				.path(HOURLY_COUNT_URI_SUFFIX).build().toUri();
		return new PanelDto(uri, panel.getSerial(), panel.getLatitude(), panel.getLongitude(), panel.getBrand(),
				panel.getUnitOfMeasure().toString(), hourlyUri, dailyUri, hourlyCountUri);
	}

	public static Panel fromDto(PanelDto panelDto) {
		return new Panel(panelDto.getSerial(), panelDto.getLatitude(), panelDto.getLongitude(), panelDto.getBrand(),
				panelDto.getUnitOfMeasure() != null ? UnitOfMeasure.valueOf(panelDto.getUnitOfMeasure()) : null);
	}

	public static PanelDto toDtoFromCurrentUri(Panel panel) {
		URI uri = UriUtil.getCurrentUri();
		URI hourlyUri = ServletUriComponentsBuilder.fromUri(uri).path(HOURLY_URI_SUFFIX).build().toUri();
		URI dailyUri = ServletUriComponentsBuilder.fromUri(uri).path(DAILY_URI_SUFFIX).build().toUri();
		URI hourlyCountUri = ServletUriComponentsBuilder.fromUri(uri).path(HOURLY_URI_SUFFIX)
				.path(HOURLY_COUNT_URI_SUFFIX).build().toUri();
		return new PanelDto(uri, panel.getSerial(), panel.getLatitude(), panel.getLongitude(), panel.getBrand(),
				panel.getUnitOfMeasure().toString(), hourlyUri, dailyUri, hourlyCountUri);
	}
}
