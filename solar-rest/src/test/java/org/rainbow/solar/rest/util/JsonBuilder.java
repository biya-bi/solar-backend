/**
 *
 */
package org.rainbow.solar.rest.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author biya-bi
 *
 */
public class JsonBuilder {

	private Map<String, Object> map = new HashMap<>();

	public JsonBuilder setProperty(String key, Object value) {
		map.put(key, value);
		return this;
	}
	public String build() {
		StringBuilder b = new StringBuilder();
		b.append("{");
		int i = 0;
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			b.append("\"");
			b.append(entry.getKey());
			b.append("\":");
			b.append("\"");
			b.append(entry.getValue());
			b.append("\"");
			if (i < map.size() - 1)
				b.append(",");
			i++;

		}
		b.append("}");
		return b.toString();
	}
}