/**
 * 
 */
package org.rainbow.solar.service;

import static org.mockito.Mockito.stub;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rainbow.solar.model.DailyElectricity;
import org.rainbow.solar.model.HourlyElectricity;
import org.rainbow.solar.model.Panel;
import org.rainbow.solar.model.UnitOfMeasure;
import org.rainbow.solar.repository.HourlyElectricityRepository;

/**
 * @author biya-bi
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class DailyElectricityServiceImplTests {
	@InjectMocks
	private DailyElectricityServiceImpl dailyElectricityService;

	@Mock
	private HourlyElectricityRepository hourlyElectricityRepository;

	@Test
	public void getBeforeDate_HourlyElectricitiesExists_DailyElectricitiesReturned() {
		Panel panel = new Panel(1L, "100001", 70.650001, 72.512351, "canadiansolar", UnitOfMeasure.W);

		LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);

		List<HourlyElectricity> hourlyElectricities = constructHourlyElectricitiesThreeDaysBack(dateTime, panel);

		stub(hourlyElectricityRepository.findAllByPanelIdBeforeDate(panel.getId(), dateTime))
				.toReturn(hourlyElectricities);

		List<DailyElectricity> dailyElectricities = dailyElectricityService
				.getBeforeDate(panel.getId(), dateTime);

		DailyElectricity dailyElectricity1 = dailyElectricities.get(0);

		Assert.assertEquals(dateTime.minusDays(1).toLocalDate(), dailyElectricity1.getDate());
		Assert.assertEquals(Long.valueOf(4700), dailyElectricity1.getSum());
		Assert.assertEquals(Double.valueOf(1175), dailyElectricity1.getAverage());
		Assert.assertEquals(Long.valueOf(975), dailyElectricity1.getMin());
		Assert.assertEquals(Long.valueOf(1500), dailyElectricity1.getMax());

		DailyElectricity dailyElectricity2 = dailyElectricities.get(1);

		Assert.assertEquals(dateTime.minusDays(2).toLocalDate(), dailyElectricity2.getDate());
		Assert.assertEquals(Long.valueOf(3025), dailyElectricity2.getSum());
		Assert.assertEquals(Double.valueOf(756.25), dailyElectricity2.getAverage());
		Assert.assertEquals(Long.valueOf(700), dailyElectricity2.getMin());
		Assert.assertEquals(Long.valueOf(850), dailyElectricity2.getMax());

		DailyElectricity dailyElectricity3 = dailyElectricities.get(2);

		Assert.assertEquals(dateTime.minusDays(3).toLocalDate(), dailyElectricity3.getDate());
		Assert.assertEquals(Long.valueOf(3575), dailyElectricity3.getSum());
		Assert.assertEquals(Double.valueOf(893.75), dailyElectricity3.getAverage());
		Assert.assertEquals(Long.valueOf(800), dailyElectricity3.getMin());
		Assert.assertEquals(Long.valueOf(950), dailyElectricity3.getMax());
	}

	private List<HourlyElectricity> constructHourlyElectricitiesThreeDaysBack(LocalDateTime dateTime, Panel panel) {
		List<HourlyElectricity> hourlyElectricities = Arrays.asList(
				new HourlyElectricity(panel, 900L, dateTime.minusHours(72)),
				new HourlyElectricity(panel, 950L, dateTime.minusHours(71)),
				new HourlyElectricity(panel, 800L, dateTime.minusHours(70)),
				new HourlyElectricity(panel, 925L, dateTime.minusHours(69)),
				new HourlyElectricity(panel, 725L, dateTime.minusHours(48)),
				new HourlyElectricity(panel, 850L, dateTime.minusHours(47)),
				new HourlyElectricity(panel, 750L, dateTime.minusHours(46)),
				new HourlyElectricity(panel, 700L, dateTime.minusHours(45)),
				new HourlyElectricity(panel, 1000L, dateTime.minusHours(24)),
				new HourlyElectricity(panel, 975L, dateTime.minusHours(23)),
				new HourlyElectricity(panel, 1225L, dateTime.minusHours(22)),
				new HourlyElectricity(panel, 1500L, dateTime.minusHours(21)));

		// Sort the hourly electricities in descending order of generation date.
		Collections.sort(hourlyElectricities, (x, y) -> -x.getReadingAt().compareTo(y.getReadingAt()));

		return hourlyElectricities;
	}
}
