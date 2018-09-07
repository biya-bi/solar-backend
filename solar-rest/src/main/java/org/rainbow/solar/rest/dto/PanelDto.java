/**
 *
 */
package org.rainbow.solar.rest.dto;

import java.io.Serializable;
import java.net.URI;

/**
 * @author biya-bi
 *
 */
public class PanelDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1135652507665767992L;
	private URI uri;
	private String serial;
	private Double latitude;
	private Double longitude;
	private String brand;
	private String unitOfMeasure;
	private URI hourlyUri;
	private URI dailyUri;

	public PanelDto() {
	}

	public PanelDto(URI uri, String serial, Double latitude, Double longitude, String brand, String unitOfMeasure,
			URI hourlyUri, URI dailyUri) {
		this.uri = uri;
		this.serial = serial;
		this.latitude = latitude;
		this.longitude = longitude;
		this.brand = brand;
		this.unitOfMeasure = unitOfMeasure;
		this.hourlyUri = hourlyUri;
		this.dailyUri = dailyUri;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}

	public void setUnitOfMeasure(String unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}

	public URI getHourlyUri() {
		return hourlyUri;
	}

	public void setHourlyUri(URI hourlyUri) {
		this.hourlyUri = hourlyUri;
	}

	public URI getDailyUri() {
		return dailyUri;
	}

	public void setDailyUri(URI dailyUri) {
		this.dailyUri = dailyUri;
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
		PanelDto other = (PanelDto) obj;
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
		return "PanelDto [uri=" + uri + ", serial=" + serial + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", brand=" + brand + "]";
	}
}
