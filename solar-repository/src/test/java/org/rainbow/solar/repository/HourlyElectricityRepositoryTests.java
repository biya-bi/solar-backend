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
		HourlyElectricity hourlyElectricity1 = new HourlyElectricity();
		hourlyElectricity1.setGeneratedElectricity(900L);
		hourlyElectricity1.setReadingAt(dateTime.minusHours(72));
		hourlyElectricity1.setPanel(panel);

		HourlyElectricity hourlyElectricity2 = new HourlyElectricity();
		hourlyElectricity2.setGeneratedElectricity(950L);
		hourlyElectricity2.setReadingAt(dateTime.minusHours(71));
		hourlyElectricity2.setPanel(panel);

		HourlyElectricity hourlyElectricity3 = new HourlyElectricity();
		hourlyElectricity3.setGeneratedElectricity(800L);
		hourlyElectricity3.setReadingAt(dateTime.minusHours(70));
		hourlyElectricity3.setPanel(panel);

		HourlyElectricity hourlyElectricity4 = new HourlyElectricity();
		hourlyElectricity4.setGeneratedElectricity(925L);
		hourlyElectricity4.setReadingAt(dateTime.minusHours(69));
		hourlyElectricity4.setPanel(panel);

		HourlyElectricity hourlyElectricity5 = new HourlyElectricity();
		hourlyElectricity5.setGeneratedElectricity(725L);
		hourlyElectricity5.setReadingAt(dateTime.minusHours(48));
		hourlyElectricity5.setPanel(panel);

		HourlyElectricity hourlyElectricity6 = new HourlyElectricity();
		hourlyElectricity6.setGeneratedElectricity(850L);
		hourlyElectricity6.setReadingAt(dateTime.minusHours(47));
		hourlyElectricity6.setPanel(panel);

		HourlyElectricity hourlyElectricity7 = new HourlyElectricity();
		hourlyElectricity7.setGeneratedElectricity(750L);
		hourlyElectricity7.setReadingAt(dateTime.minusHours(46));
		hourlyElectricity7.setPanel(panel);

		HourlyElectricity hourlyElectricity8 = new HourlyElectricity();
		hourlyElectricity8.setGeneratedElectricity(700L);
		hourlyElectricity8.setReadingAt(dateTime.minusHours(45));
		hourlyElectricity8.setPanel(panel);

		HourlyElectricity hourlyElectricity9 = new HourlyElectricity();
		hourlyElectricity9.setGeneratedElectricity(1000L);
		hourlyElectricity9.setReadingAt(dateTime.minusHours(24));
		hourlyElectricity9.setPanel(panel);

		HourlyElectricity hourlyElectricity10 = new HourlyElectricity();
		hourlyElectricity10.setGeneratedElectricity(975L);
		hourlyElectricity10.setReadingAt(dateTime.minusHours(23));
		hourlyElectricity10.setPanel(panel);

		HourlyElectricity hourlyElectricity11 = new HourlyElectricity();
		hourlyElectricity11.setGeneratedElectricity(1225L);
		hourlyElectricity11.setReadingAt(dateTime.minusHours(22));
		hourlyElectricity11.setPanel(panel);

		HourlyElectricity hourlyElectricity12 = new HourlyElectricity();
		hourlyElectricity12.setGeneratedElectricity(1500L);
		hourlyElectricity12.setReadingAt(dateTime.minusHours(21));
		hourlyElectricity12.setPanel(panel);

		HourlyElectricity hourlyElectricity13 = new HourlyElectricity();
		hourlyElectricity13.setGeneratedElectricity(750L);
		hourlyElectricity13.setReadingAt(dateTime);
		hourlyElectricity13.setPanel(panel);

		return Arrays.asList(hourlyElectricity1, hourlyElectricity2, hourlyElectricity3, hourlyElectricity4,
				hourlyElectricity5, hourlyElectricity6, hourlyElectricity7, hourlyElectricity8, hourlyElectricity9,
				hourlyElectricity10, hourlyElectricity11, hourlyElectricity12, hourlyElectricity13);
	}
}
