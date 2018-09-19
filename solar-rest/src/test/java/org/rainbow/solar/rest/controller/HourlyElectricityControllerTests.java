/**
 *
 */
package org.rainbow.solar.rest.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rainbow.solar.model.HourlyElectricity;
import org.rainbow.solar.model.Panel;
import org.rainbow.solar.rest.err.HourlyElectricityNotFoundError;
import org.rainbow.solar.rest.err.HourlyElectricityPanelMismatchError;
import org.rainbow.solar.rest.err.HourlyElectricityReadingDateRequiredError;
import org.rainbow.solar.rest.err.HourlyElectricityReadingRequiredError;
import org.rainbow.solar.rest.err.PanelNotFoundError;
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
import org.rainbow.solar.service.util.ExceptionMessagesResourceBundle;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * This class tests APIs in {@link HourlyElectricityController}
 * 
 * @author biya-bi
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class HourlyElectricityControllerTests {

	private MockMvc mockMvc;

	@InjectMocks
	private HourlyElectricityController hourlyElectricityController;

	@Mock
	private PanelService panelService;

	@Mock
	private HourlyElectricityService hourlyElectricityService;

	@Before
	public void setup() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(hourlyElectricityController)
				.setControllerAdvice(new GlobalExceptionHandler()).build();
	}

	@Test
	public void create_AllFieldsAreValid_HourlyElectricityCreated() throws Exception {
		Long panelId = 1L;

		String uri = String.format("/api/panels/%s/hourly", panelId);

		LocalDateTime now = LocalDateTime.now();

		Panel panel = new Panel(panelId);

		stub(hourlyElectricityService.create(any()))
				.toReturn(new HourlyElectricity(1L, panel, 500L, LocalDateTime.now()));

		stub(panelService.getById(panelId)).toReturn(panel);

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
	public void create_PanelDoesnotExist_NotFoundErrorReturned() throws Exception {
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
	public void create_ReadingAtIsNotSpecified_UnprocessableEntityErrorReturned() throws Exception {
		Long panelId = 1L;

		String uri = String.format("/api/panels/%s/hourly", panelId);

		stub(hourlyElectricityService.create(any())).toThrow(new HourlyElectricityReadingDateRequiredException());
		stub(panelService.getById(panelId)).toReturn(new Panel(panelId));

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
	public void create_GeneratedElectricityIsNotSpecified_UnprocessableEntityErrorReturned() throws Exception {
		Long panelId = 1L;

		String uri = String.format("/api/panels/%s/hourly", panelId);

		stub(hourlyElectricityService.create(any())).toThrow(new HourlyElectricityReadingRequiredException());
		stub(panelService.getById(panelId)).toReturn(new Panel(panelId));

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
	public void update_AllFieldsAreValid_HourlyElectricityUpdated() throws Exception {
		Long panelId = 1L;
		Long hourlyElectricityId = 1L;

		String uri = String.format("/api/panels/%s/hourly/%s", panelId, hourlyElectricityId);

		LocalDateTime now = LocalDateTime.now();

		Panel panel = new Panel(panelId);

		stub(hourlyElectricityService.update(any()))
				.toReturn((new HourlyElectricity(hourlyElectricityId, panel, 500L, LocalDateTime.now())));
		stub(hourlyElectricityService.exists(hourlyElectricityId)).toReturn(true);

		stub(panelService.getById(panelId)).toReturn(panel);

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
	public void update_PanelDoesnotExist_NotFoundErrorReturned() throws Exception {
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
	public void update_ReadingAtIsNotSpecified_UnprocessableEntityErrorReturned() throws Exception {
		Long panelId = 1L;
		Long hourlyElectricityId = 1L;

		String uri = String.format("/api/panels/%s/hourly/%s", panelId, hourlyElectricityId);

		stub(hourlyElectricityService.update(any())).toThrow(new HourlyElectricityReadingDateRequiredException());
		stub(hourlyElectricityService.exists(hourlyElectricityId)).toReturn(true);

		stub(panelService.getById(panelId)).toReturn(new Panel(panelId));

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
	public void update_GeneratedElectricityIsNotSpecified_UnprocessableEntityErrorReturned() throws Exception {
		Long panelId = 1L;
		Long hourlyElectricityId = 1L;

		String uri = String.format("/api/panels/%s/hourly/%s", panelId, hourlyElectricityId);

		stub(hourlyElectricityService.update(any())).toThrow(new HourlyElectricityReadingRequiredException());
		stub(hourlyElectricityService.exists(hourlyElectricityId)).toReturn(true);

		stub(panelService.getById(panelId)).toReturn(new Panel(panelId));

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
	public void update_HourlyElectricityDoesnotExist_NotFoundErrorReturned() throws Exception {
		Long panelId = 1L;
		Long hourlyElectricityId = 1L;

		String uri = String.format("/api/panels/%s/hourly/%s", panelId, hourlyElectricityId);

		stub(hourlyElectricityService.exists(hourlyElectricityId)).toReturn(false);

		stub(panelService.getById(panelId)).toReturn(new Panel(panelId));

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
	public void update_HourlyElectricityNotGeneratedByPanel_UnprocessableEntityErrorReturned() throws Exception {
		Long panelId = 1L;
		Long hourlyElectricityId = 1L;

		String uri = String.format("/api/panels/%s/hourly/%s", panelId, hourlyElectricityId);

		stub(hourlyElectricityService.update(any()))
				.toThrow(new HourlyElectricityPanelMismatchException(hourlyElectricityId, panelId));
		stub(hourlyElectricityService.exists(hourlyElectricityId)).toReturn(true);

		stub(panelService.getById(panelId)).toReturn(new Panel(panelId));

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
	public void delete_PanelIdAndHourlyElectricityIdGiven_HourlyElectricityDeleted() throws Exception {
		Long panelId = 1L;
		Long hourlyElectricityId = 1L;

		String uri = String.format("/api/panels/%s/hourly/%s", panelId, hourlyElectricityId);

		Panel panel = new Panel(panelId);

		stub(hourlyElectricityService.getById(hourlyElectricityId))
				.toReturn((new HourlyElectricity(hourlyElectricityId, panel, 500L, LocalDateTime.now())));
		stub(hourlyElectricityService.exists(hourlyElectricityId)).toReturn(true);

		stub(panelService.getById(panelId)).toReturn(panel);
		stub(panelService.exists(panelId)).toReturn(true);

		MvcResult result = mockMvc.perform(delete(uri)).andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());

		ArgumentCaptor<HourlyElectricity> argumentCaptor = ArgumentCaptor.forClass(HourlyElectricity.class);
		verify(hourlyElectricityService).delete(argumentCaptor.capture());
	}

	@Test
	public void delete_PanelDoesnotExist_NotFoundErrorReturned() throws Exception {
		Long panelId = 1L;
		Long hourlyElectricityId = 1L;

		String uri = String.format("/api/panels/%s/hourly/%s", panelId, hourlyElectricityId);

		stub(hourlyElectricityService.getById(any())).toReturn(new HourlyElectricity(hourlyElectricityId));
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
	public void delete_HourlyElectricityDoesnotExist_NotFoundErrorReturned() throws Exception {
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
	public void delete_HourlyElectricityNotGeneratedByPanel_UnprocessableEntityErrorReturned() throws Exception {
		Long panelId = 1L;
		Long hourlyElectricityId = 1L;

		String uri = String.format("/api/panels/%s/hourly/%s", panelId, hourlyElectricityId);

		stub(hourlyElectricityService.getById(hourlyElectricityId))
				.toReturn(new HourlyElectricity(hourlyElectricityId, new Panel(panelId), 500L, LocalDateTime.now()));

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
	public void countByPanelId_PanelExists_HourlyElectricitiesCountReturned() throws Exception {
		Long panelId = 1L;
		Long expected = 10L;
		stub(hourlyElectricityService.countByPanelId(panelId)).toReturn(expected);
		stub(panelService.exists(panelId)).toReturn(true);

		String uri = String.format("/api/panels/%s/hourly/count", panelId);

		MvcResult result = mockMvc.perform(get(uri)).andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.OK.value(), response.getStatus());

		Long actual = JsonConverter.fromJson(Long.class, response.getContentAsString());

		Assert.assertEquals(expected, actual);

		verify(hourlyElectricityService).countByPanelId(panelId);
	}

	@Test
	public void countByPanelId_PanelDoesNotExist_NotFoundErrorReturned() throws Exception {
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
