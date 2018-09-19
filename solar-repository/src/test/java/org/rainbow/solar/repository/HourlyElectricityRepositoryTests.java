/**
 * 
 */
package org.rainbow.solar.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.rainbow.solar.model.HourlyElectricity;
import org.rainbow.solar.model.Panel;
import org.rainbow.solar.model.UnitOfMeasure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.Query;

/**
 * This class only tests the methods of the {@link HourlyElectricityRepository}
 * interface for which we have provided a custom query using the {@link Query}
 * annotation.
 * 
 * @author biya-bi
 *
 */
public class HourlyElectricityRepositoryTests extends RepositoryTests {
	@Autowired
	private HourlyElectricityRepository hourlyElectricityRepository;

	@Autowired
	private TestEntityManager entityManager;

	private static final LocalDateTime TODAY_MIDNIGHT = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);

	@Test
	public void findAllByPanelIdBeforeDate_HourlyElectricitiesGeneratedBeforeGivenDateExist_HourlyElectricitiesReturned() {
		Panel panel = new Panel("100001", 70.650001, 72.512351, "canadiansolar", UnitOfMeasure.W);
		entityManager.persist(panel);
		entityManager.flush();

		constructHourlyElectricities(TODAY_MIDNIGHT, panel).forEach(entityManager::persist);
		entityManager.flush();

		List<HourlyElectricity> hourlyElectricities = hourlyElectricityRepository
				.findAllByPanelIdBeforeDate(panel.getId(), TODAY_MIDNIGHT);

		Assert.assertNotNull(hourlyElectricities);

		Assert.assertEquals(12, hourlyElectricities.size());
	}

	@Test
	public void findAllByPanelIdBeforeDate_NoHourlyElectricityGeneratedBeforeGivenDateExists_NoHourlyElectricitiesReturned() {
		Panel panel = new Panel("100002", 60.123456, 60.123456, "suntech", UnitOfMeasure.W);
		entityManager.persist(panel);
		entityManager.flush();

		List<HourlyElectricity> hourlyElectricities = hourlyElectricityRepository
				.findAllByPanelIdBeforeDate(panel.getId(), TODAY_MIDNIGHT);

		Assert.assertNotNull(hourlyElectricities);

		Assert.assertTrue(hourlyElectricities.isEmpty());
	}

	@Test
	public void countByPanelId_HourlyElectricitiesGeneratedBeforeGivenDateExist_HourlyElectricitiesCountReturned() {
		Panel panel = new Panel("100003", 70.650001, 72.512351, "canadiansolar", UnitOfMeasure.W);
		entityManager.persist(panel);
		entityManager.flush();

		constructHourlyElectricities(TODAY_MIDNIGHT, panel).forEach(entityManager::persist);
		entityManager.flush();

		long count = hourlyElectricityRepository.countByPanelId(panel.getId());

		Assert.assertEquals(13, count);
	}

	@Test
	public void countByPanelId_NoHourlyElectricityGeneratedBeforeGivenDateExists_ZeroReturned() {
		Panel panel = new Panel("100004", 50.123456, 50.123456, "canadiansolar", UnitOfMeasure.W);
		entityManager.persist(panel);
		entityManager.flush();

		long count = hourlyElectricityRepository.countByPanelId(panel.getId());

		Assert.assertEquals(0, count);
	}

	private List<HourlyElectricity> constructHourlyElectricities(LocalDateTime dateTime, Panel panel) {
		return Arrays.asList(new HourlyElectricity(panel, 900L, dateTime.minusHours(72)),
				new HourlyElectricity(panel, 950L, dateTime.minusHours(71)),
				new HourlyElectricity(panel, 800L, dateTime.minusHours(70)),
				new HourlyElectricity(panel, 925L, dateTime.minusHours(69)),
				new HourlyElectricity(panel, 725L, dateTime.minusHours(48)),
				new HourlyElectricity(panel, 850L, dateTime.minusHours(47)),
				new HourlyElectricity(panel, 750L, dateTime.minusHours(46)),
				new HourlyElectricity(panel, 700L, dateTime.minusHours(45)),
				new HourlyElectricity(panel, 1000L, dateTime.minusHours(24)),
				new HourlyElectricity(panel, 925L, dateTime.minusHours(23)),
				new HourlyElectricity(panel, 1225L, dateTime.minusHours(22)),
				new HourlyElectricity(panel, 1500L, dateTime.minusHours(21)),
				new HourlyElectricity(panel, 750L, dateTime));
	}
}
