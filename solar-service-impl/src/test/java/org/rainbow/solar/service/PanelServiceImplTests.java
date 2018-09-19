/**
 * 
 */
package org.rainbow.solar.service;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.verify;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rainbow.solar.model.Panel;
import org.rainbow.solar.model.UnitOfMeasure;
import org.rainbow.solar.repository.PanelRepository;
import org.rainbow.solar.service.exc.PanelSerialDuplicateException;
import org.rainbow.solar.service.exc.PanelSerialMaxLengthExceededException;
import org.rainbow.solar.service.exc.PanelSerialRequiredException;
import org.rainbow.solar.service.util.ExceptionMessagesResourceBundle;
import org.springframework.data.domain.Pageable;

/**
 * @author biya-bi
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PanelServiceImplTests {
	@InjectMocks
	private PanelServiceImpl panelService;

	@Mock
	private PanelRepository panelRepository;

	@Test
	public void create_PanelIsGiven_PanelCreated() {
		Panel panel = new Panel("232323", 54.123232, 54.123232, "tesla", UnitOfMeasure.KW);

		panelService.create(panel);

		ArgumentCaptor<Panel> argumentCaptor = ArgumentCaptor.forClass(Panel.class);
		verify(panelRepository).save(argumentCaptor.capture());
		Assert.assertThat(panel, is(argumentCaptor.getValue()));
	}

	@Test(expected = PanelSerialRequiredException.class)
	public void create_SerialNumberIsEmpty_PanelSerialRequiredExceptionThrown() {
		Panel panel = new Panel("", 75.645289, 75.147852, "suntech", UnitOfMeasure.KW);

		try {
			panelService.create(panel);
		} catch (PanelSerialRequiredException e) {
			Assert.assertEquals(ExceptionMessagesResourceBundle.getMessage("panel.serial.required"), e.getMessage());
			throw e;
		}
	}

	@Test(expected = PanelSerialMaxLengthExceededException.class)
	public void create_SerialNumberLengthIsGreaterThanMaximum_PanelSerialMaxLengthExceededExceptionThrown() {
		String serial = "1234567890123456789";

		Panel panel = new Panel(serial, 75.645289, 75.147852, "suntech", UnitOfMeasure.KW);

		try {
			panelService.create(panel);
		} catch (PanelSerialMaxLengthExceededException e) {
			Assert.assertEquals(String
					.format(ExceptionMessagesResourceBundle.getMessage("panel.serial.length.too.long"), serial, 16),
					e.getMessage());
			Assert.assertEquals(serial, e.getSerial());
			Assert.assertEquals(Integer.valueOf(16), Integer.valueOf(e.getMaxLength()));
			throw e;
		}
	}

	@Test(expected = PanelSerialDuplicateException.class)
	public void create_AnotherPanelHasSameSerial_PanelSerialDuplicateExceptionThrown() {
		String serial = "100001";

		stub(panelRepository.isDuplicateSerial(null, serial)).toReturn(true);

		Panel panel = new Panel(serial, 75.645289, 75.147852, "suntech", UnitOfMeasure.KW);

		try {
			panelService.create(panel);
		} catch (PanelSerialDuplicateException e) {
			Assert.assertEquals(
					String.format(ExceptionMessagesResourceBundle.getMessage("panel.serial.duplicate"), serial),
					e.getMessage());
			Assert.assertEquals(serial, e.getSerial());
			throw e;
		}
	}

	@Test
	public void update_PanelIsGiven_PanelUpdated() {
		Panel panel = new Panel(1L, "100001", 80.123456, 81.654322, "tesla", UnitOfMeasure.KW);

		panelService.update(panel);

		ArgumentCaptor<Panel> argumentCaptor = ArgumentCaptor.forClass(Panel.class);
		verify(panelRepository).save(argumentCaptor.capture());
		Assert.assertThat(panel, is(argumentCaptor.getValue()));
	}

	@Test(expected = PanelSerialRequiredException.class)
	public void update_SerialNumberIsEmpty_PanelSerialRequiredExceptionThrown() {
		Panel panel = new Panel(1L, "", 75.645289, 75.147852, "suntech", UnitOfMeasure.KW);

		try {
			panelService.update(panel);
		} catch (PanelSerialRequiredException e) {
			Assert.assertEquals(ExceptionMessagesResourceBundle.getMessage("panel.serial.required"), e.getMessage());
			throw e;
		}
	}

	@Test(expected = PanelSerialMaxLengthExceededException.class)
	public void update_SerialNumberLengthIsGreaterThanMaximum_PanelSerialMaxLengthExceededExceptionThrown() {
		String serial = "1234567890123456789";

		Panel panel = new Panel(1L, serial, 75.645289, 75.147852, "suntech", UnitOfMeasure.KW);

		try {
			panelService.update(panel);
		} catch (PanelSerialMaxLengthExceededException e) {
			Assert.assertEquals(String
					.format(ExceptionMessagesResourceBundle.getMessage("panel.serial.length.too.long"), serial, 16),
					e.getMessage());
			Assert.assertEquals(serial, e.getSerial());
			Assert.assertEquals(Integer.valueOf(16), Integer.valueOf(e.getMaxLength()));
			throw e;
		}
	}

	@Test(expected = PanelSerialDuplicateException.class)
	public void update_AnotherPanelHasSameSerial_PanelSerialDuplicateExceptionThrown() {
		String serial = "100001";
		long id = 1L;

		stub(panelRepository.isDuplicateSerial(id, serial)).toReturn(true);

		Panel panel = new Panel(id, serial, 80.446189, 85.756328, "suntech", UnitOfMeasure.KW);

		try {
			panelService.update(panel);
		} catch (PanelSerialDuplicateException e) {
			Assert.assertEquals(
					String.format(ExceptionMessagesResourceBundle.getMessage("panel.serial.duplicate"), serial),
					e.getMessage());
			Assert.assertEquals(serial, e.getSerial());
			throw e;
		}
	}

	@Test
	public void delete_PanelIsGiven_PanelDeleted() {
		Panel panel = new Panel(1L, "232323", 54.123232, 54.123232, "tesla", UnitOfMeasure.KW);

		panelService.delete(panel);

		ArgumentCaptor<Panel> argumentCaptor = ArgumentCaptor.forClass(Panel.class);
		verify(panelRepository).delete(argumentCaptor.capture());
		Assert.assertThat(argumentCaptor.getValue(), is(panel));
	}

	@Test
	public void getById_IdIsGiven_PanelReturned() {
		Panel panel = new Panel(1L, "232323", 54.123232, 54.123232, "tesla", UnitOfMeasure.KW);

		stub(panelRepository.findById(1L)).toReturn(panel);

		Panel actual = panelService.getById(1L);

		ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
		verify(panelRepository).findById(argumentCaptor.capture());
		Assert.assertThat(panel, is(actual));
	}

	@Test
	public void getAllByOrderBySerialAsc_PanelsExist_PanelsReturned() {
		panelService.getAllByOrderBySerialAsc(null);

		ArgumentCaptor<Pageable> argumentCaptor = ArgumentCaptor.forClass(Pageable.class);
		verify(panelRepository).findAllByOrderBySerialAsc(argumentCaptor.capture());
	}

	@Test
	public void count_PanelsExist_PanelsCountReturned() {
		stub(panelRepository.count()).toReturn(5L);

		long actual = panelService.count();

		verify(panelRepository).count();
		Assert.assertEquals(5L, actual);
	}
}
