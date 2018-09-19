/**
 *
 */
package org.rainbow.solar.service;

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
public interface HourlyElectricityService extends Service<HourlyElectricity, Long> {
	Page<HourlyElectricity> getByPanelId(Long panelId, Pageable pageable);

	long countByPanelId(Long panelId);
}
