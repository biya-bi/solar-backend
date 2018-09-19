/**
 *
 */
package org.rainbow.solar.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Panel class hold information related to a solar panel.
 * 
 * @author biya-bi
 *
 */
@Entity
@Table(name = "panel")
public class Panel implements Serializable {

	private static final long serialVersionUID = -8527695980909864257L;

	private Long id;
	private String serial;
	private Double latitude;
	private Double longitude;
	private String brand;
	private UnitOfMeasure unitOfMeasure;

	public Panel() {
	}

	public Panel(Long id) {
		this.id = id;
	}

	public Panel(String serial, Double latitude, Double longitude, String brand, UnitOfMeasure unitOfMeasure) {
		this(null, serial, latitude, longitude, brand, unitOfMeasure);
	}

	public Panel(Long id, String serial, Double latitude, Double longitude, String brand, UnitOfMeasure unitOfMeasure) {
		this.id = id;
		this.serial = serial;
		this.latitude = latitude;
		this.longitude = longitude;
		this.brand = brand;
		this.unitOfMeasure = unitOfMeasure;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@NotNull
	@Column(name = "serial", length = 16, unique = true, nullable = false)
	@Size(min = 1, max = 16)
	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	@Column(name = "latitude", scale = 6)
	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	@Column(name = "longitude", scale = 6)
	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@Column(name = "brand")
	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	@NotNull
	@Column(name = "unit_of_measure", nullable = false)
	@Enumerated(EnumType.STRING)
	public UnitOfMeasure getUnitOfMeasure() {
		return unitOfMeasure;
	}

	public void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Panel other = (Panel) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
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
		return "Panel [id=" + id + ", serial=" + serial + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", brand=" + brand + ", unitOfMeasure=" + unitOfMeasure + "]";
	}
}
