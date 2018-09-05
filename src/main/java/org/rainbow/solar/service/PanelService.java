package org.rainbow.solar.service;

import org.rainbow.solar.model.Panel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * PanelService interface for Panels.
 * 
 * @author biya-bi
 *
 */
public interface PanelService {
	void create(Panel panel);

	void update(Panel panel);

	void delete(Panel panel);

	Panel findById(Long id);

	Panel findBySerial(String serial);

	boolean exists(Long id);

	Page<Panel> getAllByOrderBySerialAsc(Pageable pageable);

	long count();
}
