package org.rainbow.solar.service;

import java.time.LocalDateTime;
import java.util.List;

import org.rainbow.solar.dto.DailyElectricity;
import org.rainbow.solar.exceptions.HourlyElectricityPanelRequiredException;
import org.rainbow.solar.exceptions.HourlyElectricityReadingDateRequiredException;
import org.rainbow.solar.exceptions.HourlyElectricityReadingRequiredException;
import org.rainbow.solar.model.HourlyElectricity;
import org.rainbow.solar.repository.HourlyElectricityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * HourlyElectricityServiceImpl will handle electricity generated by a PanelDto.
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

	public HourlyElectricity save(HourlyElectricity hourlyElectricity) {
		validate(hourlyElectricity);
		return hourlyElectricityRepository.save(hourlyElectricity);
	}

	public Page<HourlyElectricity> getHourlyElectricities(Long panelId, Pageable pageable) {
		return hourlyElectricityRepository.findAllByPanelIdOrderByReadingAtDesc(panelId, pageable);
	}

	@Override
	public List<DailyElectricity> getDailyElectricitiesBeforeDate(Long panelId, LocalDateTime dateTime,
			Pageable pageable) {
		return hourlyElectricityRepository.findDailyElectricitiesBeforeDate(panelId, dateTime, pageable);
	}

	@Override
	public long getHourlyElectricitiesCount(Long panelId) {
		return hourlyElectricityRepository.findHourlyElectricitiesCount(panelId);
	}

}