/**
 * 
 */
package org.rainbow.solar.service;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rainbow.solar.model.DailyElectricity;
import org.rainbow.solar.model.HourlyElectricity;
import org.rainbow.solar.model.Panel;
import org.rainbow.solar.model.UnitOfMeasure;
import org.rainbow.solar.repository.HourlyElectricityRepository;
import org.rainbow.solar.service.exc.HourlyElectricityPanelMismatchException;
import org.rainbow.solar.service.exc.HourlyElectricityReadingDateRequiredException;
import org.rainbow.solar.service.exc.HourlyElectricityReadingRequiredException;
import org.rainbow.solar.service.util.ExceptionMessagesResourceBundle;
import org.springframework.data.domain.Pageable;

/**
 * @author biya-bi
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class HourlyElectricityServiceImplTests {
	@InjectMocks
	private HourlyElectricityServiceImpl hourlyElectricityService;

	@Mock
	private HourlyElectricityRepository hourlyElectricityRepository;

	@Test
	public void create_HourlyElectricityIsGiven_HourlyElectricityCreated() {
		hourlyElectricityService.create(new HourlyElectricity(new Panel(1L), 500L, LocalDateTime.now()));

		ArgumentCaptor<HourlyElectricity> argumentCaptor = ArgumentCaptor.forClass(HourlyElectricity.class);
		verify(hourlyElectricityRepository).save(argumentCaptor.capture());
	}

	@Test(expected = HourlyElectricityReadingDateRequiredException.class)
	public void create_ReadingAtIsNotSpecified_HourlyElectricityReadingDateRequiredExceptionThrown() {
		try {
			hourlyElectricityService.create(new HourlyElectricity(new Panel(1L), 500L, null));
		} catch (HourlyElectricityReadingDateRequiredException e) {
			Assert.assertEquals(ExceptionMessagesResourceBundle.getMessage("hourly.electricity.reading.date.required"),
					e.getMessage());
			throw e;
		}
	}

	@Test(expected = HourlyElectricityReadingRequiredException.class)
	public void create_GeneratedElectricityIsNotSpecified_HourlyElectricityReadingRequiredExceptionThrown() {
		try {
			hourlyElectricityService.create(new HourlyElectricity(new Panel(1L), null, LocalDateTime.now()));
		} catch (HourlyElectricityReadingRequiredException e) {
			Assert.assertEquals(ExceptionMessagesResourceBundle.getMessage("hourly.electricity.reading.required"),
					e.getMessage());
			throw e;
		}
	}

	@Test
	public void update_HourlyElectricityIsGiven_HourlyElectricityUpdated() {
		HourlyElectricity hourlyElectricity = new HourlyElectricity(1L, new Panel(1L), 500L, LocalDateTime.now());

		stub(hourlyElectricityRepository.findById(1L)).toReturn(hourlyElectricity);

		hourlyElectricityService.update(hourlyElectricity);

		ArgumentCaptor<HourlyElectricity> argumentCaptor = ArgumentCaptor.forClass(HourlyElectricity.class);
		verify(hourlyElectricityRepository).save(argumentCaptor.capture());
	}

	@Test(expected = HourlyElectricityReadingDateRequiredException.class)
	public void update_ReadingAtIsNotSpecified_HourlyElectricityReadingDateRequiredExceptionThrown() {
		HourlyElectricity hourlyElectricity = new HourlyElectricity(1L, new Panel(1L), 500L, null);

		stub(hourlyElectricityRepository.findById(1L)).toReturn(hourlyElectricity);

		try {
			hourlyElectricityService.update(hourlyElectricity);
		} catch (HourlyElectricityReadingDateRequiredException e) {
			Assert.assertEquals(ExceptionMessagesResourceBundle.getMessage("hourly.electricity.reading.date.required"),
					e.getMessage());
			throw e;
		}
	}

	@Test(expected = HourlyElectricityReadingRequiredException.class)
	public void update_GeneratedElectricityIsNotSpecified_HourlyElectricityReadingRequiredExceptionThrown() {
		HourlyElectricity hourlyElectricity = new HourlyElectricity(1L, new Panel(1L), null, LocalDateTime.now());

		try {
			hourlyElectricityService.update(hourlyElectricity);
		} catch (HourlyElectricityReadingRequiredException e) {
			Assert.assertEquals(ExceptionMessagesResourceBundle.getMessage("hourly.electricity.reading.required"),
					e.getMessage());
			throw e;
		}
	}

	@Test(expected = HourlyElectricityPanelMismatchException.class)
	public void update_HourlyElectricityNotGeneratedByPanel_HourlyElectricityPanelMismatchExceptionThrown() {
		Long hourlyElectricityId = 1L;
		Long panelId = 2L;

		HourlyElectricity existingHourlyElectricity = new HourlyElectricity(hourlyElectricityId, new Panel(1L), 500L,
				LocalDateTime.now());

		HourlyElectricity hourlyElectricity = new HourlyElectricity(hourlyElectricityId, new Panel(panelId), 2000L, LocalDateTime.now());

		stub(hourlyElectricityRepository.findById(hourlyElectricityId)).toReturn(existingHourlyElectricity);

		try {
			hourlyElectricityService.update(hourlyElectricity);
		} catch (HourlyElectricityPanelMismatchException e) {
			Assert.assertEquals(
					String.format(ExceptionMessagesResourceBundle.getMessage("hourly.electricity.panel.mismatch"),
							hourlyElectricityId, panelId),
					e.getMessage());
			Assert.assertEquals(hourlyElectricityId, e.getHourlyElectricityId());
			Assert.assertEquals(panelId, e.getPanelId());
			throw e;
		}
	}

	@Test
	public void delete_HourlyElectricityIsGiven_HourlyElectricityDeleted() {
		HourlyElectricity hourlyElectricity = new HourlyElectricity(1L, new Panel(1L), 500L, LocalDateTime.now());

		hourlyElectricityService.delete(hourlyElectricity);

		ArgumentCaptor<HourlyElectricity> argumentCaptor = ArgumentCaptor.forClass(HourlyElectricity.class);
		verify(hourlyElectricityRepository).delete(argumentCaptor.capture());
		Assert.assertThat(argumentCaptor.getValue(), is(hourlyElectricity));
	}

	@Test(expected = HourlyElectricityPanelMismatchException.class)
	public void delete_HourlyElectricityNotGeneratedByPanel_HourlyElectricityPanelMismatchExceptionThrown() {
		Long hourlyElectricityId = 1L;
		Long panelId = 2L;

		HourlyElectricity existingHourlyElectricity = new HourlyElectricity(hourlyElectricityId, new Panel(1L),
				500L, LocalDateTime.now());

		HourlyElectricity hourlyElectricity = new HourlyElectricity(hourlyElectricityId, new Panel(panelId), 2000L,
				LocalDateTime.now());

		stub(hourlyElectricityRepository.findById(hourlyElectricityId)).toReturn(existingHourlyElectricity);

		try {
			hourlyElectricityService.delete(hourlyElectricity);
		} catch (HourlyElectricityPanelMismatchException e) {
			Assert.assertEquals(
					String.format(ExceptionMessagesResourceBundle.getMessage("hourly.electricity.panel.mismatch"),
							hourlyElectricityId, panelId),
					e.getMessage());
			Assert.assertEquals(hourlyElectricityId, e.getHourlyElectricityId());
			Assert.assertEquals(panelId, e.getPanelId());
			throw e;
		}
	}

	@Test
	public void getByPanelId_PanelIdGiven_HourlyElectricitiesReturned() throws Exception {
		hourlyElectricityService.getHourlyElectricities(1L, null);

		ArgumentCaptor<Long> argumentCaptor1 = ArgumentCaptor.forClass(Long.class);
		ArgumentCaptor<Pageable> argumentCaptor2 = ArgumentCaptor.forClass(Pageable.class);
		verify(hourlyElectricityRepository).findAllByPanelIdOrderByReadingAtDesc(argumentCaptor1.capture(),
				argumentCaptor2.capture());
	}

	@Test
	public void countByPanelId_PanelExists_HourlyElectricitiesCountReturned() {
		stub(hourlyElectricityRepository.countByPanelId(1L)).toReturn(10L);

		long actual = hourlyElectricityService.getHourlyElectricitiesCount(1L);

		ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
		verify(hourlyElectricityRepository).countByPanelId(argumentCaptor.capture());
		Assert.assertEquals(10L, actual);
	}

	@Test
	public void count_PanelsExist_PanelsCountReturned() {
		stub(hourlyElectricityRepository.count()).toReturn(20L);

		long actual = hourlyElectricityService.count();

		verify(hourlyElectricityRepository).count();
		Assert.assertEquals(20L, actual);
	}

	@Test
	public void getDailyElectricitiesBeforeDate_HourlyElectricitiesExists_DailyElectricitiesReturned() {
		Panel panel = new Panel(1L, "100001", 70.650001, 72.512351, "canadiansolar", UnitOfMeasure.W);

		LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);

		List<HourlyElectricity> hourlyElectricities = constructHourlyElectricitiesThreeDaysBack(dateTime, panel);

		stub(hourlyElectricityRepository.findAllByPanelIdBeforeDate(panel.getId(), dateTime))
				.toReturn(hourlyElectricities);

		List<DailyElectricity> dailyElectricities = hourlyElectricityService
				.getDailyElectricitiesBeforeDate(panel.getId(), dateTime);

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
