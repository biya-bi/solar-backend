/**
 *
 */
package org.rainbow.solar.service;

import java.time.LocalDateTime;
import java.util.List;

import org.rainbow.solar.model.DailyElectricity;
import org.rainbow.solar.model.HourlyElectricity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * This interface encapsulates the services related to a
 * {@link HourlyElectricity} objects.
 * 
 * @author biya-bi
 *
 */
public interface HourlyElectricityService {
	HourlyElectricity save(HourlyElectricity hourlyElectricity);

	Page<HourlyElectricity> getHourlyElectricities(Long panelId, Pageable pageable);

	List<DailyElectricity> getDailyElectricitiesBeforeDate(Long panelId, LocalDateTime dateTime, Pageable pageable);

	long getHourlyElectricitiesCount(Long panelId);
}
