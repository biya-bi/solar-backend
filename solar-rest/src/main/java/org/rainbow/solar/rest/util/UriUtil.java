/**
 * 
 */
package org.rainbow.solar.rest.util;

import java.net.URI;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * @author biya-bi
 *
 */
public class UriUtil {
	public static <T> URI buildUri(T value) {
		return ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{value}").buildAndExpand(value).toUri();
	}

	public static <T> URI getCurrentUri() {
		return ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri();
	}

}
