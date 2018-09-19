/**
 * 
 */
package org.rainbow.solar.service;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rainbow.solar.model.HourlyElectricity;
import org.rainbow.solar.model.Panel;
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

		HourlyElectricity hourlyElectricity = new HourlyElectricity(hourlyElectricityId, new Panel(panelId), 2000L,
				LocalDateTime.now());

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

		HourlyElectricity existingHourlyElectricity = new HourlyElectricity(hourlyElectricityId, new Panel(1L), 500L,
				LocalDateTime.now());

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
		hourlyElectricityService.getByPanelId(1L, null);

		ArgumentCaptor<Long> argumentCaptor1 = ArgumentCaptor.forClass(Long.class);
		ArgumentCaptor<Pageable> argumentCaptor2 = ArgumentCaptor.forClass(Pageable.class);
		verify(hourlyElectricityRepository).findAllByPanelIdOrderByReadingAtDesc(argumentCaptor1.capture(),
				argumentCaptor2.capture());
	}

	@Test
	public void countByPanelId_PanelExists_HourlyElectricitiesCountReturned() {
		stub(hourlyElectricityRepository.countByPanelId(1L)).toReturn(10L);

		long actual = hourlyElectricityService.countByPanelId(1L);

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
}
