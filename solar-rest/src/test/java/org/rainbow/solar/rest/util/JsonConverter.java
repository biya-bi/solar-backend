/**
 * 
 */
package org.rainbow.solar.rest.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * @author biya-bi
 *
 */
public class JsonConverter {

	public static String toJson(Object obj) throws JsonProcessingException {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		return ow.writeValueAsString(obj);
	}

	@SuppressWarnings("unchecked")
	public static <T> T fromJson(Class<? extends T> clazz, String json) throws JsonProcessingException, IOException {
		ObjectReader or = new ObjectMapper().reader().forType(clazz);

		return (T) or.readValue(json);

	}
}