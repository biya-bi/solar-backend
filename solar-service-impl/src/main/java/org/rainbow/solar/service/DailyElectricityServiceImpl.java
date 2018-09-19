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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author biya-bi
 *
 */
@Service
public class DailyElectricityServiceImpl implements DailyElectricityService {
	@Autowired
	private HourlyElectricityRepository hourlyElectricityRepository;

	@Override
	public List<DailyElectricity> getBeforeDate(Long panelId, LocalDateTime dateTime) {
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

}