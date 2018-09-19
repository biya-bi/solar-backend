/**
 *
 */
package org.rainbow.solar.service;

import org.rainbow.solar.model.HourlyElectricity;
import org.rainbow.solar.repository.HourlyElectricityRepository;
import org.rainbow.solar.service.exc.HourlyElectricityPanelMismatchException;
import org.rainbow.solar.service.exc.HourlyElectricityPanelRequiredException;
import org.rainbow.solar.service.exc.HourlyElectricityReadingDateRequiredException;
import org.rainbow.solar.service.exc.HourlyElectricityReadingRequiredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 
 * @author biya-bi
 *
 */
@Service
public class HourlyElectricityServiceImpl implements HourlyElectricityService {
	@Autowired
	private HourlyElectricityRepository hourlyElectricityRepository;

	private void validate(HourlyElectricity hourlyElectricity) {
		if (hourlyElectricity.getGeneratedElectricity() == null)
			throw new HourlyElectricityReadingRequiredException();
		if (hourlyElectricity.getReadingAt() == null)
			throw new HourlyElectricityReadingDateRequiredException();
		if (hourlyElectricity.getPanel() == null)
			throw new HourlyElectricityPanelRequiredException();
	}

	private void validatePanel(HourlyElectricity hourlyElectricity) {
		HourlyElectricity existingHourlyElectricity = hourlyElectricityRepository.findById(hourlyElectricity.getId());
		if (existingHourlyElectricity != null
				&& !hourlyElectricity.getPanel().equals(existingHourlyElectricity.getPanel()))
			throw new HourlyElectricityPanelMismatchException(hourlyElectricity.getId(),
					hourlyElectricity.getPanel().getId());
	}

	public HourlyElectricity create(HourlyElectricity hourlyElectricity) {
		validate(hourlyElectricity);
		return hourlyElectricityRepository.save(hourlyElectricity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rainbow.solar.service.Service#update(java.lang.Object)
	 */
	@Override
	public HourlyElectricity update(HourlyElectricity hourlyElectricity) {
		validate(hourlyElectricity);
		validatePanel(hourlyElectricity);
		return hourlyElectricityRepository.save(hourlyElectricity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rainbow.solar.service.Service#delete(java.lang.Object)
	 */
	@Override
	public void delete(HourlyElectricity hourlyElectricity) {
		validatePanel(hourlyElectricity);
		hourlyElectricityRepository.delete(hourlyElectricity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rainbow.solar.service.Service#getById(java.lang.Object)
	 */
	@Override
	public HourlyElectricity getById(Long id) {
		return hourlyElectricityRepository.findById(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rainbow.solar.service.Service#exists(java.lang.Object)
	 */
	@Override
	public boolean exists(Long id) {
		return hourlyElectricityRepository.exists(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rainbow.solar.service.Service#count()
	 */
	@Override
	public long count() {
		return hourlyElectricityRepository.count();
	}

	public Page<HourlyElectricity> getByPanelId(Long panelId, Pageable pageable) {
		return hourlyElectricityRepository.findAllByPanelIdOrderByReadingAtDesc(panelId, pageable);
	}

	@Override
	public long countByPanelId(Long panelId) {
		return hourlyElectricityRepository.countByPanelId(panelId);
	}

}