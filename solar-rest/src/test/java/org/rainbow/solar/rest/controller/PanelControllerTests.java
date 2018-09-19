/**
 *
 */
package org.rainbow.solar.rest.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
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
import org.rainbow.solar.rest.dto.PanelDto;
import org.rainbow.solar.rest.err.HourlyElectricityNotFoundError;
import org.rainbow.solar.rest.err.HourlyElectricityPanelMismatchError;
import org.rainbow.solar.rest.err.HourlyElectricityReadingDateRequiredError;
import org.rainbow.solar.rest.err.HourlyElectricityReadingRequiredError;
import org.rainbow.solar.rest.err.PanelNotFoundError;
import org.rainbow.solar.rest.err.PanelSerialDuplicateError;
import org.rainbow.solar.rest.err.PanelSerialMaxLengthExceededError;
import org.rainbow.solar.rest.err.PanelSerialRequiredError;
import org.rainbow.solar.rest.err.SolarErrorCode;
import org.rainbow.solar.rest.handler.GlobalExceptionHandler;
import org.rainbow.solar.rest.util.ErrorMessagesResourceBundle;
import org.rainbow.solar.rest.util.JsonBuilder;
import org.rainbow.solar.rest.util.JsonConverter;
import org.rainbow.solar.rest.util.RegexUtil;
import org.rainbow.solar.service.HourlyElectricityService;
import org.rainbow.solar.service.PanelService;
import org.rainbow.solar.service.exc.HourlyElectricityPanelMismatchException;
import org.rainbow.solar.service.exc.HourlyElectricityReadingDateRequiredException;
import org.rainbow.solar.service.exc.HourlyElectricityReadingRequiredException;
import org.rainbow.solar.service.exc.PanelSerialDuplicateException;
import org.rainbow.solar.service.exc.PanelSerialMaxLengthExceededException;
import org.rainbow.solar.service.exc.PanelSerialRequiredException;
import org.rainbow.solar.service.util.ExceptionMessagesResourceBundle;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * This class tests APIs in {@link PanelController}
 * 
 * @author biya-bi
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PanelControllerTests {

	private MockMvc mockMvc;

	@InjectMocks
	private PanelController panelController;

	@Mock
	private PanelService panelService;

	@Mock
	private HourlyElectricityService hourlyElectricityService;

	@Before
	public void setup() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(panelController).setControllerAdvice(new GlobalExceptionHandler())
				.build();
	}

	@Test
	public void createPanel_AllFieldsAreValid_PanelCreated() throws Exception {
		Panel panel = new Panel(1L, "232323", 54.123232, 54.123232, "tesla", UnitOfMeasure.KW);

		stub(panelService.create(any())).toReturn(panel);

		MvcResult result = mockMvc.perform(
				post("/api/panels").content(JsonConverter.toJson(panel)).contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.CREATED.value(), response.getStatus());

		String location = response.getHeader("Location");
		Assert.assertNotNull(location);
		Assert.assertTrue(RegexUtil.endsWithDigit("/api/panels/", location));

		ArgumentCaptor<Panel> argumentCaptor = ArgumentCaptor.forClass(Panel.class);
		verify(panelService).create(argumentCaptor.capture());
	}

	@Test
	public void createPanel_SerialNumberIsEmpty_UnprocessableEntityErrorReturned() throws Exception {
		stub(panelService.create(any())).toThrow(new PanelSerialRequiredException());

		Panel panel = new Panel("", 75.645289, 75.147852, "suntech", UnitOfMeasure.KW);

		MvcResult result = mockMvc.perform(
				post("/api/panels").content(JsonConverter.toJson(panel)).contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());

		PanelSerialRequiredError error = JsonConverter.fromJson(PanelSerialRequiredError.class,
				response.getContentAsString());
		Assert.assertEquals(SolarErrorCode.PANEL_SERIAL_REQUIRED.value(), error.getCode());
		Assert.assertEquals(ExceptionMessagesResourceBundle.getMessage("panel.serial.required"), error.getMessage());

		ArgumentCaptor<Panel> argumentCaptor = ArgumentCaptor.forClass(Panel.class);
		verify(panelService).create(argumentCaptor.capture());
	}

	@Test
	public void createPanel_SerialNumberLengthIsGreaterThanMaximum_UnprocessableEntityErrorReturned() throws Exception {
		String serial = "1234567890123456789";
		int maxLength = 16;

		stub(panelService.create(any())).toThrow(new PanelSerialMaxLengthExceededException(serial, maxLength));

		Panel panel = new Panel(serial, 75.645289, 75.147852, "suntech", UnitOfMeasure.KW);

		MvcResult result = mockMvc.perform(
				post("/api/panels").content(JsonConverter.toJson(panel)).contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());

		PanelSerialMaxLengthExceededError error = JsonConverter.fromJson(PanelSerialMaxLengthExceededError.class,
				response.getContentAsString());
		Assert.assertEquals(SolarErrorCode.PANEL_SERIAL_MAX_LENGTH_EXCEEDED.value(), error.getCode());
		Assert.assertEquals(String.format(ExceptionMessagesResourceBundle.getMessage("panel.serial.length.too.long"),
				serial, maxLength), error.getMessage());
		Assert.assertEquals(serial, error.getSerial());
		Assert.assertEquals(Integer.valueOf(maxLength), Integer.valueOf(error.getMaxLength()));

		ArgumentCaptor<Panel> argumentCaptor = ArgumentCaptor.forClass(Panel.class);
		verify(panelService).create(argumentCaptor.capture());
	}

	@Test
	public void createPanel_AnotherPanelHasSameSerial_UnprocessableEntityErrorReturned() throws Exception {
		String serial = "100001";

		stub(panelService.create(any())).toThrow(new PanelSerialDuplicateException(serial));

		Panel panel = new Panel(serial, 75.645289, 75.147852, "suntech", UnitOfMeasure.KW);

		MvcResult result = mockMvc.perform(
				post("/api/panels").content(JsonConverter.toJson(panel)).contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());

		PanelSerialDuplicateError error = JsonConverter.fromJson(PanelSerialDuplicateError.class,
				response.getContentAsString());
		Assert.assertEquals(SolarErrorCode.PANEL_SERIAL_DUPLICATE.value(), error.getCode());
		Assert.assertEquals(String.format(ExceptionMessagesResourceBundle.getMessage("panel.serial.duplicate"), serial),
				error.getMessage());
		Assert.assertEquals(serial, error.getSerial());

		ArgumentCaptor<Panel> argumentCaptor = ArgumentCaptor.forClass(Panel.class);
		verify(panelService).create(argumentCaptor.capture());
	}

	@Test
	public void updatePanel_AllFieldsAreValid_PanelUpdated() throws Exception {
		Panel panel = new Panel(1L, "22222", 80.123456, 81.654321, "tesla", UnitOfMeasure.KW);

		stub(panelService.update(any())).toReturn(panel);
		stub(panelService.exists(panel.getId())).toReturn(true);

		MvcResult result = mockMvc.perform(
				put("/api/panels/1").content(JsonConverter.toJson(panel)).contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());

		ArgumentCaptor<Panel> argumentCaptor = ArgumentCaptor.forClass(Panel.class);
		verify(panelService).update(argumentCaptor.capture());
	}

	@Test
	public void updatePanel_SerialNumberIsEmpty_UnprocessableEntityErrorReturned() throws Exception {
		stub(panelService.update(any())).toThrow(new PanelSerialRequiredException());

		Panel panel = new Panel("", 75.645289, 75.147852, "suntech", UnitOfMeasure.KW);
		stub(panelService.exists(1L)).toReturn(true);

		MvcResult result = mockMvc.perform(
				put("/api/panels/1").content(JsonConverter.toJson(panel)).contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());

		PanelSerialRequiredError error = JsonConverter.fromJson(PanelSerialRequiredError.class,
				response.getContentAsString());
		Assert.assertEquals(SolarErrorCode.PANEL_SERIAL_REQUIRED.value(), error.getCode());
		Assert.assertEquals(ExceptionMessagesResourceBundle.getMessage("panel.serial.required"), error.getMessage());

		ArgumentCaptor<Panel> argumentCaptor = ArgumentCaptor.forClass(Panel.class);
		verify(panelService).update(argumentCaptor.capture());
	}

	@Test
	public void updatePanel_SerialNumberLengthIsGreaterThanMaximum_UnprocessableEntityErrorReturned() throws Exception {
		String serial = "1234567890123456789";
		int maxLength = 16;

		stub(panelService.update(any())).toThrow(new PanelSerialMaxLengthExceededException(serial, maxLength));
		stub(panelService.exists(1L)).toReturn(true);

		Panel panel = new Panel(serial, 75.645289, 75.147852, "suntech", UnitOfMeasure.KW);

		MvcResult result = mockMvc.perform(
				put("/api/panels/1").content(JsonConverter.toJson(panel)).contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());

		PanelSerialMaxLengthExceededError error = JsonConverter.fromJson(PanelSerialMaxLengthExceededError.class,
				response.getContentAsString());
		Assert.assertEquals(SolarErrorCode.PANEL_SERIAL_MAX_LENGTH_EXCEEDED.value(), error.getCode());
		Assert.assertEquals(String.format(ExceptionMessagesResourceBundle.getMessage("panel.serial.length.too.long"),
				serial, maxLength), error.getMessage());
		Assert.assertEquals(serial, error.getSerial());
		Assert.assertEquals(Integer.valueOf(maxLength), Integer.valueOf(error.getMaxLength()));

		ArgumentCaptor<Panel> argumentCaptor = ArgumentCaptor.forClass(Panel.class);
		verify(panelService).update(argumentCaptor.capture());
	}

	@Test
	public void updatePanel_AnotherPanelHasSameSerial_UnprocessableEntityErrorReturned() throws Exception {
		String serial = "100001";

		stub(panelService.update(any())).toThrow(new PanelSerialDuplicateException(serial));
		stub(panelService.exists(1L)).toReturn(true);

		Panel panel = new Panel(serial, 75.645289, 75.147852, "suntech", UnitOfMeasure.KW);

		MvcResult result = mockMvc.perform(
				put("/api/panels/1").content(JsonConverter.toJson(panel)).contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());

		PanelSerialDuplicateError error = JsonConverter.fromJson(PanelSerialDuplicateError.class,
				response.getContentAsString());
		Assert.assertEquals(SolarErrorCode.PANEL_SERIAL_DUPLICATE.value(), error.getCode());
		Assert.assertEquals(String.format(ExceptionMessagesResourceBundle.getMessage("panel.serial.duplicate"), serial),
				error.getMessage());
		Assert.assertEquals(serial, error.getSerial());

		ArgumentCaptor<Panel> argumentCaptor = ArgumentCaptor.forClass(Panel.class);
		verify(panelService).update(argumentCaptor.capture());
	}

	@Test
	public void updatePanel_PanelDoesNotExist_NotFoundErrorReturned() throws Exception {
		Long panelId = 1L;
		stub(panelService.exists(1L)).toReturn(false);

		Panel panel = new Panel("100001", 75.645289, 75.147852, "suntech", UnitOfMeasure.KW);

		MvcResult result = mockMvc.perform(put("/api/panels/" + panelId).content(JsonConverter.toJson(panel))
				.contentType(MediaType.APPLICATION_JSON)).andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

		PanelNotFoundError error = JsonConverter.fromJson(PanelNotFoundError.class, response.getContentAsString());
		Assert.assertEquals(SolarErrorCode.PANEL_ID_NOT_FOUND.value(), error.getCode());
		Assert.assertEquals(String.format(ErrorMessagesResourceBundle.getMessage("panel.id.not.found"), panelId),
				error.getMessage());
		Assert.assertEquals(panelId, error.getId());
	}

	@Test
	public void deletePanel_PanelIdGiven_PanelDeleted() throws Exception {
		Panel panel = new Panel(1L, "100001", 75.645289, 75.147852, "suntech", UnitOfMeasure.KW);

		stub(panelService.getById(1L)).toReturn(panel);

		MvcResult result = mockMvc.perform(delete("/api/panels/1")).andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());

		ArgumentCaptor<Panel> argumentCaptor = ArgumentCaptor.forClass(Panel.class);
		verify(panelService).delete(argumentCaptor.capture());
	}

	@Test
	public void deletePanel_PanelDoesNotExist_NotFoundErrorReturned() throws Exception {
		Long panelId = 1L;
		stub(panelService.exists(panelId)).toReturn(false);

		MvcResult result = mockMvc.perform(delete("/api/panels/" + panelId)).andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

		PanelNotFoundError error = JsonConverter.fromJson(PanelNotFoundError.class, response.getContentAsString());
		Assert.assertEquals(SolarErrorCode.PANEL_ID_NOT_FOUND.value(), error.getCode());
		Assert.assertEquals(String.format(ErrorMessagesResourceBundle.getMessage("panel.id.not.found"), panelId),
				error.getMessage());
		Assert.assertEquals(panelId, error.getId());
	}

	@Test
	public void getPanel_PanelIdGiven_PanelReturned() throws Exception {
		Panel panel = new Panel(1L, "100001", 70.650001, 72.512351, "canadiansolar", UnitOfMeasure.W);
		String uri = "/api/panels/" + panel.getId();

		stub(panelService.getById(panel.getId())).toReturn(panel);

		MvcResult result = mockMvc.perform(get(uri)).andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.OK.value(), response.getStatus());

		PanelDto actual = JsonConverter.fromJson(PanelDto.class, response.getContentAsString());
		Assert.assertNotNull(actual);
		Assert.assertTrue(actual.getUri().toString().endsWith(uri));
		Assert.assertEquals("100001", actual.getSerial());
		Assert.assertEquals(Double.valueOf(70.650001), actual.getLatitude());
		Assert.assertEquals(Double.valueOf(72.512351), actual.getLongitude());
		Assert.assertEquals("canadiansolar", actual.getBrand());
		Assert.assertTrue(actual.getHourlyUri().toString().endsWith(uri + "/hourly"));
		Assert.assertTrue(actual.getDailyUri().toString().endsWith(uri + "/daily"));
		Assert.assertTrue(actual.getHourlyCountUri().toString().endsWith(uri + "/hourly/count"));
		Assert.assertEquals(UnitOfMeasure.W.toString(), actual.getUnitOfMeasure());

		ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
		verify(panelService).getById(argumentCaptor.capture());
	}

	@Test
	public void getPanel_PanelDoesNotExist_NotFoundErrorReturned() throws Exception {
		Long panelId = 1L;

		MvcResult result = mockMvc.perform(get("/api/panels/" + panelId)).andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

		PanelNotFoundError error = JsonConverter.fromJson(PanelNotFoundError.class, response.getContentAsString());
		Assert.assertEquals(SolarErrorCode.PANEL_ID_NOT_FOUND.value(), error.getCode());
		Assert.assertEquals(String.format(ErrorMessagesResourceBundle.getMessage("panel.id.not.found"), panelId),
				error.getMessage());
		Assert.assertEquals(panelId, error.getId());

		ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
		verify(panelService).getById(argumentCaptor.capture());
	}

	@Test
	public void getPanelsCount_PanelsExist_PanelsCountReturned() throws Exception {
		Long expected = 5L;

		stub(panelService.count()).toReturn(expected);

		MvcResult result = mockMvc.perform(get("/api/panels/count")).andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.OK.value(), response.getStatus());

		Long actual = JsonConverter.fromJson(Long.class, response.getContentAsString());

		Assert.assertEquals(expected, actual);

		verify(panelService).count();
	}

	@Test
	public void createHourlyElectricity_AllFieldsAreValid_HourlyElectricityCreated() throws Exception {
		Long panelId = 1L;

		String uri = String.format("/api/panels/%s/hourly", panelId);

		LocalDateTime now = LocalDateTime.now();

		HourlyElectricity hourlyElectricity = new HourlyElectricity();
		hourlyElectricity.setId(1L);
		hourlyElectricity.setGeneratedElectricity(500L);
		hourlyElectricity.setReadingAt(now);
		stub(hourlyElectricityService.create(any())).toReturn(hourlyElectricity);

		stub(panelService.getById(panelId)).toReturn(new Panel());

		String requestBody = new JsonBuilder().setProperty("generatedElectricity", 500)
				.setProperty("readingAt", now.format(DateTimeFormatter.ISO_DATE_TIME)).build();

		MvcResult result = mockMvc.perform(post(uri).content(requestBody).contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.CREATED.value(), response.getStatus());

		String location = response.getHeader("Location");
		Assert.assertNotNull(location);
		Assert.assertTrue(RegexUtil.endsWithDigit(uri + "/", location));

		ArgumentCaptor<HourlyElectricity> argumentCaptor = ArgumentCaptor.forClass(HourlyElectricity.class);
		verify(hourlyElectricityService).create(argumentCaptor.capture());
	}

	@Test
	public void createHourlyElectricity_PanelDoesnotExist_NotFoundErrorReturned() throws Exception {
		Long panelId = 1L;

		String uri = String.format("/api/panels/%s/hourly", panelId);

		stub(panelService.getById(panelId)).toReturn(null);

		String requestBody = new JsonBuilder().setProperty("generatedElectricity", 500)
				.setProperty("readingAt", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)).build();

		MvcResult result = mockMvc.perform(post(uri).content(requestBody).contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

		PanelNotFoundError error = JsonConverter.fromJson(PanelNotFoundError.class, response.getContentAsString());
		Assert.assertEquals(SolarErrorCode.PANEL_ID_NOT_FOUND.value(), error.getCode());
		Assert.assertEquals(String.format(ErrorMessagesResourceBundle.getMessage("panel.id.not.found"), panelId),
				error.getMessage());
		Assert.assertEquals(panelId, error.getId());
	}

	@Test
	public void createHourlyElectricity_ReadingAtIsNotSpecified_UnprocessableEntityErrorReturned() throws Exception {
		Long panelId = 1L;

		String uri = String.format("/api/panels/%s/hourly", panelId);

		stub(hourlyElectricityService.create(any())).toThrow(new HourlyElectricityReadingDateRequiredException());
		stub(panelService.getById(panelId)).toReturn(new Panel());

		String requestBody = new JsonBuilder().setProperty("generatedElectricity", 500).build();

		MvcResult result = mockMvc.perform(post(uri).content(requestBody).contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());

		HourlyElectricityReadingDateRequiredError error = JsonConverter
				.fromJson(HourlyElectricityReadingDateRequiredError.class, response.getContentAsString());
		Assert.assertEquals(SolarErrorCode.HOURLY_ELECTRICITY_READING_DATE_REQUIRED.value(), error.getCode());
		Assert.assertEquals(ExceptionMessagesResourceBundle.getMessage("hourly.electricity.reading.date.required"),
				error.getMessage());

		ArgumentCaptor<HourlyElectricity> argumentCaptor = ArgumentCaptor.forClass(HourlyElectricity.class);
		verify(hourlyElectricityService).create(argumentCaptor.capture());
	}

	@Test
	public void createHourlyElectricity_GeneratedElectricityIsNotSpecified_UnprocessableEntityErrorReturned()
			throws Exception {
		Long panelId = 1L;

		String uri = String.format("/api/panels/%s/hourly", panelId);

		stub(hourlyElectricityService.create(any())).toThrow(new HourlyElectricityReadingRequiredException());
		stub(panelService.getById(panelId)).toReturn(new Panel());

		String requestBody = new JsonBuilder()
				.setProperty("readingAt", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)).build();

		MvcResult result = mockMvc.perform(post(uri).content(requestBody).contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());

		HourlyElectricityReadingRequiredError error = JsonConverter
				.fromJson(HourlyElectricityReadingRequiredError.class, response.getContentAsString());
		Assert.assertEquals(SolarErrorCode.HOURLY_ELECTRICITY_READING_REQUIRED.value(), error.getCode());
		Assert.assertEquals(ExceptionMessagesResourceBundle.getMessage("hourly.electricity.reading.required"),
				error.getMessage());

		ArgumentCaptor<HourlyElectricity> argumentCaptor = ArgumentCaptor.forClass(HourlyElectricity.class);
		verify(hourlyElectricityService).create(argumentCaptor.capture());
	}

	@Test
	public void updateHourlyElectricity_AllFieldsAreValid_HourlyElectricityUpdated() throws Exception {
		Long panelId = 1L;
		Long hourlyElectricityId = 1L;

		String uri = String.format("/api/panels/%s/hourly/%s", panelId, hourlyElectricityId);

		LocalDateTime now = LocalDateTime.now();

		HourlyElectricity hourlyElectricity = new HourlyElectricity();
		hourlyElectricity.setId(1L);
		hourlyElectricity.setGeneratedElectricity(500L);
		hourlyElectricity.setReadingAt(now);
		stub(hourlyElectricityService.update(any())).toReturn(hourlyElectricity);
		stub(hourlyElectricityService.exists(hourlyElectricityId)).toReturn(true);

		stub(panelService.getById(panelId)).toReturn(new Panel());

		String requestBody = new JsonBuilder().setProperty("generatedElectricity", 500)
				.setProperty("readingAt", now.format(DateTimeFormatter.ISO_DATE_TIME)).build();

		MvcResult result = mockMvc.perform(put(uri).content(requestBody).contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());

		ArgumentCaptor<HourlyElectricity> argumentCaptor = ArgumentCaptor.forClass(HourlyElectricity.class);
		verify(hourlyElectricityService).update(argumentCaptor.capture());
	}

	@Test
	public void updateHourlyElectricity_PanelDoesnotExist_NotFoundErrorReturned() throws Exception {
		Long panelId = 1L;
		Long hourlyElectricityId = 1L;

		String uri = String.format("/api/panels/%s/hourly/%s", panelId, hourlyElectricityId);

		LocalDateTime now = LocalDateTime.now();

		stub(hourlyElectricityService.exists(hourlyElectricityId)).toReturn(true);

		stub(panelService.getById(panelId)).toReturn(null);

		String requestBody = new JsonBuilder().setProperty("generatedElectricity", 500)
				.setProperty("readingAt", now.format(DateTimeFormatter.ISO_DATE_TIME)).build();

		MvcResult result = mockMvc.perform(put(uri).content(requestBody).contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

		PanelNotFoundError error = JsonConverter.fromJson(PanelNotFoundError.class, response.getContentAsString());
		Assert.assertEquals(SolarErrorCode.PANEL_ID_NOT_FOUND.value(), error.getCode());
		Assert.assertEquals(String.format(ErrorMessagesResourceBundle.getMessage("panel.id.not.found"), panelId),
				error.getMessage());
		Assert.assertEquals(panelId, error.getId());
	}

	@Test
	public void updateHourlyElectricity_ReadingAtIsNotSpecified_UnprocessableEntityErrorReturned() throws Exception {
		Long panelId = 1L;
		Long hourlyElectricityId = 1L;

		String uri = String.format("/api/panels/%s/hourly/%s", panelId, hourlyElectricityId);

		stub(hourlyElectricityService.update(any())).toThrow(new HourlyElectricityReadingDateRequiredException());
		stub(hourlyElectricityService.exists(hourlyElectricityId)).toReturn(true);

		stub(panelService.getById(panelId)).toReturn(new Panel());

		String requestBody = new JsonBuilder().setProperty("generatedElectricity", 500).build();

		MvcResult result = mockMvc.perform(put(uri).content(requestBody).contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());

		HourlyElectricityReadingDateRequiredError error = JsonConverter
				.fromJson(HourlyElectricityReadingDateRequiredError.class, response.getContentAsString());
		Assert.assertEquals(SolarErrorCode.HOURLY_ELECTRICITY_READING_DATE_REQUIRED.value(), error.getCode());
		Assert.assertEquals(ExceptionMessagesResourceBundle.getMessage("hourly.electricity.reading.date.required"),
				error.getMessage());

		ArgumentCaptor<HourlyElectricity> argumentCaptor = ArgumentCaptor.forClass(HourlyElectricity.class);
		verify(hourlyElectricityService).update(argumentCaptor.capture());
	}

	@Test
	public void updateHourlyElectricity_GeneratedElectricityIsNotSpecified_UnprocessableEntityErrorReturned()
			throws Exception {
		Long panelId = 1L;
		Long hourlyElectricityId = 1L;

		String uri = String.format("/api/panels/%s/hourly/%s", panelId, hourlyElectricityId);

		stub(hourlyElectricityService.update(any())).toThrow(new HourlyElectricityReadingRequiredException());
		stub(hourlyElectricityService.exists(hourlyElectricityId)).toReturn(true);

		stub(panelService.getById(panelId)).toReturn(new Panel());

		String requestBody = new JsonBuilder()
				.setProperty("readingAt", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)).build();

		MvcResult result = mockMvc.perform(put(uri).content(requestBody).contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());

		HourlyElectricityReadingRequiredError error = JsonConverter
				.fromJson(HourlyElectricityReadingRequiredError.class, response.getContentAsString());
		Assert.assertEquals(SolarErrorCode.HOURLY_ELECTRICITY_READING_REQUIRED.value(), error.getCode());
		Assert.assertEquals(ExceptionMessagesResourceBundle.getMessage("hourly.electricity.reading.required"),
				error.getMessage());

		ArgumentCaptor<HourlyElectricity> argumentCaptor = ArgumentCaptor.forClass(HourlyElectricity.class);
		verify(hourlyElectricityService).update(argumentCaptor.capture());
	}

	@Test
	public void updateHourlyElectricity_HourlyElectriciyDoesnotExist_NotFoundErrorReturned() throws Exception {
		Long panelId = 1L;
		Long hourlyElectricityId = 1L;

		String uri = String.format("/api/panels/%s/hourly/%s", panelId, hourlyElectricityId);

		stub(hourlyElectricityService.exists(hourlyElectricityId)).toReturn(false);

		stub(panelService.getById(panelId)).toReturn(new Panel());

		String requestBody = new JsonBuilder().setProperty("generatedElectricity", 500).build();

		MvcResult result = mockMvc.perform(put(uri).content(requestBody).contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

		HourlyElectricityNotFoundError error = JsonConverter.fromJson(HourlyElectricityNotFoundError.class,
				response.getContentAsString());
		Assert.assertEquals(SolarErrorCode.HOURLY_ELECTRICITY_ID_NOT_FOUND.value(), error.getCode());
		Assert.assertEquals(String.format(ErrorMessagesResourceBundle.getMessage("hourly.electricity.id.not.found"),
				hourlyElectricityId), error.getMessage());
		Assert.assertEquals(hourlyElectricityId, error.getId());
	}

	@Test
	public void updateHourlyElectricity_HourlyElectricityNotGeneratedByPanel_UnprocessableEntityErrorReturned()
			throws Exception {
		Long panelId = 1L;
		Long hourlyElectricityId = 1L;

		String uri = String.format("/api/panels/%s/hourly/%s", panelId, hourlyElectricityId);

		stub(hourlyElectricityService.update(any()))
				.toThrow(new HourlyElectricityPanelMismatchException(hourlyElectricityId, panelId));
		stub(hourlyElectricityService.exists(hourlyElectricityId)).toReturn(true);

		stub(panelService.getById(panelId)).toReturn(new Panel());

		String requestBody = new JsonBuilder().setProperty("generatedElectricity", 500)
				.setProperty("readingAt", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)).build();

		MvcResult result = mockMvc.perform(put(uri).content(requestBody).contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());

		HourlyElectricityPanelMismatchError error = JsonConverter.fromJson(HourlyElectricityPanelMismatchError.class,
				response.getContentAsString());
		Assert.assertEquals(SolarErrorCode.HOURLY_ELECTRICITY_PANEL_MISMATCH.value(), error.getCode());
		Assert.assertEquals(
				String.format(ExceptionMessagesResourceBundle.getMessage("hourly.electricity.panel.mismatch"),
						hourlyElectricityId, panelId),
				error.getMessage());
		Assert.assertEquals(panelId, Long.valueOf(error.getPanelId()));
		Assert.assertEquals(hourlyElectricityId, Long.valueOf(error.getHourlyElectricityId()));

		ArgumentCaptor<HourlyElectricity> argumentCaptor = ArgumentCaptor.forClass(HourlyElectricity.class);
		verify(hourlyElectricityService).update(argumentCaptor.capture());
	}

	@Test
	public void deleteHourlyElectricity_PanelIdAndHourlyElectricityIdGiven_HourlyElectricityDeleted() throws Exception {
		Long panelId = 1L;
		Long hourlyElectricityId = 1L;

		String uri = String.format("/api/panels/%s/hourly/%s", panelId, hourlyElectricityId);

		HourlyElectricity hourlyElectricity = new HourlyElectricity();
		hourlyElectricity.setId(1L);
		hourlyElectricity.setGeneratedElectricity(500L);
		hourlyElectricity.setReadingAt(LocalDateTime.now());
		stub(hourlyElectricityService.getById(hourlyElectricityId)).toReturn(hourlyElectricity);
		stub(hourlyElectricityService.exists(hourlyElectricityId)).toReturn(true);

		stub(panelService.getById(panelId)).toReturn(new Panel());
		stub(panelService.exists(panelId)).toReturn(true);

		MvcResult result = mockMvc.perform(delete(uri)).andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());

		ArgumentCaptor<HourlyElectricity> argumentCaptor = ArgumentCaptor.forClass(HourlyElectricity.class);
		verify(hourlyElectricityService).delete(argumentCaptor.capture());
	}

	@Test
	public void deleteHourlyElectricity_PanelDoesnotExist_NotFoundErrorReturned() throws Exception {
		Long panelId = 1L;
		Long hourlyElectricityId = 1L;

		String uri = String.format("/api/panels/%s/hourly/%s", panelId, hourlyElectricityId);

		stub(hourlyElectricityService.getById(any())).toReturn(new HourlyElectricity());
		stub(hourlyElectricityService.exists(hourlyElectricityId)).toReturn(true);

		stub(panelService.exists(panelId)).toReturn(false);

		MvcResult result = mockMvc.perform(delete(uri)).andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

		PanelNotFoundError error = JsonConverter.fromJson(PanelNotFoundError.class, response.getContentAsString());
		Assert.assertEquals(SolarErrorCode.PANEL_ID_NOT_FOUND.value(), error.getCode());
		Assert.assertEquals(String.format(ErrorMessagesResourceBundle.getMessage("panel.id.not.found"), panelId),
				error.getMessage());
		Assert.assertEquals(panelId, error.getId());
	}

	@Test
	public void deleteHourlyElectricity_HourlyElectricityDoesnotExist_NotFoundErrorReturned() throws Exception {
		Long panelId = 1L;
		Long hourlyElectricityId = 1L;
		String uri = String.format("/api/panels/%s/hourly/%s", panelId, hourlyElectricityId);

		stub(hourlyElectricityService.getById(any())).toReturn(null);
		stub(hourlyElectricityService.exists(hourlyElectricityId)).toReturn(true);

		stub(panelService.exists(panelId)).toReturn(true);

		MvcResult result = mockMvc.perform(delete(uri)).andReturn();

		MockHttpServletResponse response = result.getResponse();

		HourlyElectricityNotFoundError error = JsonConverter.fromJson(HourlyElectricityNotFoundError.class,
				response.getContentAsString());
		Assert.assertEquals(SolarErrorCode.HOURLY_ELECTRICITY_ID_NOT_FOUND.value(), error.getCode());
		Assert.assertEquals(String.format(ErrorMessagesResourceBundle.getMessage("hourly.electricity.id.not.found"),
				hourlyElectricityId), error.getMessage());
		Assert.assertEquals(hourlyElectricityId, error.getId());
	}

	@Test
	public void deleteHourlyElectricity_HourlyElectricityNotGeneratedByPanel_UnprocessableEntityErrorReturned()
			throws Exception {
		Long panelId = 1L;
		Long hourlyElectricityId = 1L;

		String uri = String.format("/api/panels/%s/hourly/%s", panelId, hourlyElectricityId);

		HourlyElectricity hourlyElectricity = new HourlyElectricity();
		hourlyElectricity.setId(1L);
		hourlyElectricity.setGeneratedElectricity(500L);
		hourlyElectricity.setReadingAt(LocalDateTime.now());
		stub(hourlyElectricityService.getById(hourlyElectricityId)).toReturn(hourlyElectricity);

		doThrow(new HourlyElectricityPanelMismatchException(hourlyElectricityId, panelId))
				.when(hourlyElectricityService).delete(any());

		stub(panelService.exists(panelId)).toReturn(true);

		MvcResult result = mockMvc.perform(delete(uri)).andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());

		HourlyElectricityPanelMismatchError error = JsonConverter.fromJson(HourlyElectricityPanelMismatchError.class,
				response.getContentAsString());
		Assert.assertEquals(SolarErrorCode.HOURLY_ELECTRICITY_PANEL_MISMATCH.value(), error.getCode());
		Assert.assertEquals(
				String.format(ExceptionMessagesResourceBundle.getMessage("hourly.electricity.panel.mismatch"),
						hourlyElectricityId, panelId),
				error.getMessage());
		Assert.assertEquals(panelId, Long.valueOf(error.getPanelId()));
		Assert.assertEquals(hourlyElectricityId, Long.valueOf(error.getHourlyElectricityId()));

		ArgumentCaptor<HourlyElectricity> argumentCaptor = ArgumentCaptor.forClass(HourlyElectricity.class);
		verify(hourlyElectricityService).delete(argumentCaptor.capture());
	}


	@Test
	public void getAllDailyElectricityFromYesterday_PanelIdGiven_DailyElectricitiesReturned() throws Exception {
		Long panelId = 1L;

		String uri = String.format("/api/panels/%s/daily", panelId);

		LocalDate today = LocalDate.now();
		LocalDate oneDayAgo = today.minusDays(1);
		LocalDate twoDaysAgo = today.minusDays(2);
		LocalDate threeDaysAgo = today.minusDays(3);

		List<DailyElectricity> dailyElectrities = Arrays.asList(
				new DailyElectricity(oneDayAgo.getYear(), oneDayAgo.getMonthValue(), oneDayAgo.getDayOfMonth(), 3575L,
						893.75, 800L, 950L),
				new DailyElectricity(twoDaysAgo.getYear(), twoDaysAgo.getMonthValue(), twoDaysAgo.getDayOfMonth(),
						3025L, 756.25, 700L, 850L),
				new DailyElectricity(threeDaysAgo.getYear(), threeDaysAgo.getMonthValue(), threeDaysAgo.getDayOfMonth(),
						4700L, 1175D, 975L, 1500L));

		stub(hourlyElectricityService.getDailyElectricitiesBeforeDate(anyLong(), any())).toReturn(dailyElectrities);

		stub(panelService.exists(panelId)).toReturn(true);

		mockMvc.perform(get(uri)).andExpect(jsonPath("$", hasSize(3)));

		ArgumentCaptor<Long> argumentCaptor1 = ArgumentCaptor.forClass(Long.class);
		ArgumentCaptor<LocalDateTime> argumentCaptor2 = ArgumentCaptor.forClass(LocalDateTime.class);

		verify(hourlyElectricityService).getDailyElectricitiesBeforeDate(argumentCaptor1.capture(),
				argumentCaptor2.capture());
	}

	@Test
	public void getAllDailyElectricityFromYesterday_PanelDoesNotExist_NotFoundErrorReturned() throws Exception {
		Long panelId = 1L;
		String uri = String.format("/api/panels/%s/daily", panelId);
		stub(panelService.exists(panelId)).toReturn(false);

		MvcResult result = mockMvc.perform(get(uri)).andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

		PanelNotFoundError error = JsonConverter.fromJson(PanelNotFoundError.class, response.getContentAsString());
		Assert.assertEquals(SolarErrorCode.PANEL_ID_NOT_FOUND.value(), error.getCode());
		Assert.assertEquals(String.format(ErrorMessagesResourceBundle.getMessage("panel.id.not.found"), panelId),
				error.getMessage());
		Assert.assertEquals(panelId, error.getId());
	}

	@Test
	public void getHourlyElectricitiesCount_PanelExists_HourlyElectricitiesCountReturned() throws Exception {
		Long panelId = 1L;
		Long expected = 10L;
		stub(hourlyElectricityService.getHourlyElectricitiesCount(panelId)).toReturn(expected);
		stub(panelService.exists(panelId)).toReturn(true);

		String uri = String.format("/api/panels/%s/hourly/count", panelId);

		MvcResult result = mockMvc.perform(get(uri)).andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.OK.value(), response.getStatus());

		Long actual = JsonConverter.fromJson(Long.class, response.getContentAsString());

		Assert.assertEquals(expected, actual);

		verify(hourlyElectricityService).getHourlyElectricitiesCount(panelId);
	}

	@Test
	public void getHourlyElectricitiesCount_PanelDoesNotExist_NotFoundErrorReturned() throws Exception {
		Long panelId = 1L;
		String uri = String.format("/api/panels/%s/hourly/count", panelId);

		MvcResult result = mockMvc.perform(get(uri)).andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

		PanelNotFoundError error = JsonConverter.fromJson(PanelNotFoundError.class, response.getContentAsString());
		Assert.assertEquals(SolarErrorCode.PANEL_ID_NOT_FOUND.value(), error.getCode());
		Assert.assertEquals(String.format(ErrorMessagesResourceBundle.getMessage("panel.id.not.found"), panelId),
				error.getMessage());
		Assert.assertEquals(panelId, error.getId());
	}

}
