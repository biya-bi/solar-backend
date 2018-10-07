/**
 * 
 */
package org.rainbow.solar.rest.util;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.util.Objects;

import org.rainbow.solar.RequestMappings;
import org.rainbow.solar.rest.controller.PanelController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * @author biya-bi
 *
 */
public class PanelHateoasUtil {
	private final Long panelId;

	public PanelHateoasUtil(Long panelId) {
		Objects.requireNonNull(panelId);
		this.panelId = panelId;
	}

	public URI buildUri() {
		return buildUri(null);
	}

	public URI buildHourlyUri() {
		return buildUri(RequestMappings.HOURLY_URI_SUFFIX);
	}

	public URI buildHourlyCountUri() {
		URI hourlyUri = buildHourlyUri();
		return ServletUriComponentsBuilder.fromUri(hourlyUri).path(RequestMappings.HOURLY_COUNT_URI_SUFFIX).build()
				.toUri();
	}

	public URI buildDailyUri() {
		return buildUri(RequestMappings.DAILY_URI_SUFFIX);
	}

	private URI buildUri(String path) {
		URI uri = linkTo(methodOn(PanelController.class).getById(panelId)).toUri();
		return ServletUriComponentsBuilder.fromUri(uri).path(path).build().toUri();
	}

}
