/**
 *
 */
package org.rainbow.solar.service;

import org.rainbow.solar.model.Panel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * This interface encapsulates the services related to {@linkplain Panel}
 * objects.
 * 
 * @author biya-bi
 *
 */
public interface PanelService extends Service<Panel, Long> {
	Panel getBySerial(String serial);

	Page<Panel> getAllByOrderBySerialAsc(Pageable pageable);
}
