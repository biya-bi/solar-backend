/**
 *
 */
package org.rainbow.solar.service;

import java.time.LocalDateTime;
import java.util.List;

import org.rainbow.solar.model.DailyElectricity;
import org.rainbow.solar.model.HourlyElectricity;

/**
 * This interface encapsulates the services related to a
 * {@link HourlyElectricity} objects.
 * 
 * @author biya-bi
 *
 */
public interface DailyElectricityService {
	List<DailyElectricity> getBeforeDate(Long panelId, LocalDateTime dateTime);
}
