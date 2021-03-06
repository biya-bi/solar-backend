/**
 *
 */
package org.rainbow.solar.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * This class holds the sum, average,minimum and maximum electricity generated
 * by a {@link Panel} on a given day.
 * 
 * @author biya-bi
 *
 */
public class DailyElectricity implements Serializable {

	private static final long serialVersionUID = 3605549122072628877L;
	private LocalDate date;
	private Long sum;
	private Double average;
	private Long min;
	private Long max;

	public DailyElectricity() {
	}

	public DailyElectricity(int year, int month, int dayOfMonth, Long sum, Double average, Long min, Long max) {
		this.date = LocalDate.of(year, month, dayOfMonth);
		this.sum = sum;
		this.average = average;
		this.min = min;
		this.max = max;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Long getSum() {
		return sum;
	}

	public void setSum(Long sum) {
		this.sum = sum;
	}

	public Double getAverage() {
		return average;
	}

	public void setAverage(Double average) {
		this.average = average;
	}

	public Long getMin() {
		return min;
	}

	public void setMin(Long min) {
		this.min = min;
	}

	public Long getMax() {
		return max;
	}

	public void setMax(Long max) {
		this.max = max;
	}

	@Override
	public String toString() {
		return "DailyElectricity [date=" + date + ", sum=" + sum + ", average=" + average + ", min=" + min + ", max="
				+ max + "]";
	}

}
