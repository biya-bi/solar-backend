/**
 * 
 */
package org.rainbow.solar.rest.util;

import java.net.URI;
import java.util.Objects;

import org.rainbow.solar.RequestMappings;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * @author biya-bi
 *
 */
public class HourlyElectricityHateoasUtil {
	private final Long hourlyElectricityId;

	public HourlyElectricityHateoasUtil(Long hourlyElectricityId) {
		Objects.requireNonNull(hourlyElectricityId);
		this.hourlyElectricityId = hourlyElectricityId;
	}

	public URI buildUri(Long panelId) {
		URI panelUri = new PanelHateoasUtil(panelId).buildUri();
		return ServletUriComponentsBuilder.fromUri(panelUri).path(RequestMappings.HOURLY_URI_SUFFIX).path("/")
				.path(hourlyElectricityId.toString()).build().toUri();

	}
}
