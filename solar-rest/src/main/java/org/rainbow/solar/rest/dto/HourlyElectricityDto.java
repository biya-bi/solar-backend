package org.rainbow.solar.rest.dto;

import java.io.Serializable;
import java.net.URI;
import java.time.LocalDateTime;

/**
 * 
 * @author biya-bi
 *
 */
public class HourlyElectricityDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2810814225741508121L;
	private URI uri;
	private Long generatedElectricity;
	private LocalDateTime readingAt;

	public HourlyElectricityDto() {
	}

	public HourlyElectricityDto(URI uri, Long generatedElectricity, LocalDateTime readingAt) {
		this.uri = uri;
		this.generatedElectricity = generatedElectricity;
		this.readingAt = readingAt;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public Long getGeneratedElectricity() {
		return generatedElectricity;
	}

	public void setGeneratedElectricity(Long generatedElectricity) {
		this.generatedElectricity = generatedElectricity;
	}

	public LocalDateTime getReadingAt() {
		return readingAt;
	}

	public void setReadingAt(LocalDateTime readingAt) {
		this.readingAt = readingAt;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HourlyElectricityDto other = (HourlyElectricityDto) obj;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HourlyElectricity [uri=" + uri + ", generatedElectricity=" + generatedElectricity + ", readingAt="
				+ readingAt + "]";
	}

}
