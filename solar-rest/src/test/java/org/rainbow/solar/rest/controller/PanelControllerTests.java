/**
 *
 */
package org.rainbow.solar.rest.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.rainbow.solar.model.Panel;
import org.rainbow.solar.model.UnitOfMeasure;
import org.rainbow.solar.rest.dto.PanelDto;
import org.rainbow.solar.rest.err.PanelNotFoundError;
import org.rainbow.solar.rest.err.PanelSerialDuplicateError;
import org.rainbow.solar.rest.err.PanelSerialMaxLengthExceededError;
import org.rainbow.solar.rest.err.PanelSerialRequiredError;
import org.rainbow.solar.rest.err.SolarErrorCode;
import org.rainbow.solar.rest.handler.GlobalExceptionHandler;
import org.rainbow.solar.rest.util.ErrorMessagesResourceBundle;
import org.rainbow.solar.rest.util.JsonConverter;
import org.rainbow.solar.service.PanelService;
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

	@Before
	public void setup() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(panelController).setControllerAdvice(new GlobalExceptionHandler())
				.build();
	}

	@Test
	public void create_AllFieldsAreValid_PanelCreated() throws Exception {
		Panel panel = new Panel(1L, "232323", 54.123232, 54.123232, "tesla", UnitOfMeasure.KW);

		stub(panelService.create(any())).toReturn(panel);

		MvcResult result = mockMvc.perform(
				post("/api/panels").content(JsonConverter.toJson(panel)).contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.CREATED.value(), response.getStatus());

		ArgumentCaptor<Panel> argumentCaptor = ArgumentCaptor.forClass(Panel.class);
		verify(panelService).create(argumentCaptor.capture());
	}

	@Test
	public void create_SerialNumberIsEmpty_UnprocessableEntityErrorReturned() throws Exception {
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
	public void create_SerialNumberLengthIsGreaterThanMaximum_UnprocessableEntityErrorReturned() throws Exception {
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
	public void create_AnotherPanelHasSameSerial_UnprocessableEntityErrorReturned() throws Exception {
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
	public void update_AllFieldsAreValid_PanelUpdated() throws Exception {
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
	public void update_SerialNumberIsEmpty_UnprocessableEntityErrorReturned() throws Exception {
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
	public void update_SerialNumberLengthIsGreaterThanMaximum_UnprocessableEntityErrorReturned() throws Exception {
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
	public void update_AnotherPanelHasSameSerial_UnprocessableEntityErrorReturned() throws Exception {
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
	public void update_PanelDoesNotExist_NotFoundErrorReturned() throws Exception {
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
	public void delete_PanelIdGiven_PanelDeleted() throws Exception {
		Panel panel = new Panel(1L, "100001", 75.645289, 75.147852, "suntech", UnitOfMeasure.KW);

		stub(panelService.getById(1L)).toReturn(panel);

		MvcResult result = mockMvc.perform(delete("/api/panels/1")).andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());

		ArgumentCaptor<Panel> argumentCaptor = ArgumentCaptor.forClass(Panel.class);
		verify(panelService).delete(argumentCaptor.capture());
	}

	@Test
	public void delete_PanelDoesNotExist_NotFoundErrorReturned() throws Exception {
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
	public void getById_PanelIdGiven_PanelReturned() throws Exception {
		Panel panel = new Panel(1L, "100001", 70.650001, 72.512351, "canadiansolar", UnitOfMeasure.W);
		String uri = "/api/panels/" + panel.getId();

		stub(panelService.getById(panel.getId())).toReturn(panel);

		MvcResult result = mockMvc.perform(get(uri)).andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.OK.value(), response.getStatus());

		PanelDto actual = JsonConverter.fromJson(PanelDto.class, response.getContentAsString());
		Assert.assertNotNull(actual);

		ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
		verify(panelService).getById(argumentCaptor.capture());
	}

	@Test
	public void getById_PanelDoesNotExist_NotFoundErrorReturned() throws Exception {
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
	public void count_PanelsExist_PanelsCountReturned() throws Exception {
		Long expected = 5L;

		stub(panelService.count()).toReturn(expected);

		MvcResult result = mockMvc.perform(get("/api/panels/count")).andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.OK.value(), response.getStatus());

		Long actual = JsonConverter.fromJson(Long.class, response.getContentAsString());

		Assert.assertEquals(expected, actual);

		verify(panelService).count();
	}

}
