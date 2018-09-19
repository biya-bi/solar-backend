/**
 *
 */
package org.rainbow.solar.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.rainbow.solar.model.DailyElectricity;
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

	public Page<HourlyElectricity> getHourlyElectricities(Long panelId, Pageable pageable) {
		return hourlyElectricityRepository.findAllByPanelIdOrderByReadingAtDesc(panelId, pageable);
	}

	@Override
	public List<DailyElectricity> getDailyElectricitiesBeforeDate(Long panelId, LocalDateTime dateTime) {
		List<HourlyElectricity> hourlyElectricities = hourlyElectricityRepository.findAllByPanelIdBeforeDate(panelId,
				dateTime);

		List<DailyElectricity> dailyElectricities = new ArrayList<>();

		LinkedHashMap<LocalDate, DoubleSummaryStatistics> statisticsByDate = hourlyElectricities.stream()
				.collect(Collectors.groupingBy(x -> x.getReadingAt().toLocalDate(), LinkedHashMap::new,
						Collectors.summarizingDouble(x -> x.getGeneratedElectricity())));

		for (Map.Entry<LocalDate, DoubleSummaryStatistics> entry : statisticsByDate.entrySet()) {
			LocalDate date = entry.getKey();
			DoubleSummaryStatistics stats = entry.getValue();
			dailyElectricities.add(new DailyElectricity(date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
					Double.valueOf(stats.getSum()).longValue(), stats.getAverage(),
					Double.valueOf(stats.getMin()).longValue(), Double.valueOf(stats.getMax()).longValue()));
		}
		return dailyElectricities;
	}

	@Override
	public long getHourlyElectricitiesCount(Long panelId) {
		return hourlyElectricityRepository.countByPanelId(panelId);
	}

}