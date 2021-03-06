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
import org.rainbow.solar.rest.handler.GlobalExceptionHandler;
import org.rainbow.solar.rest.util.JsonConverter;
import org.rainbow.solar.service.PanelService;
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
	public void create_PanelIsValid_PanelCreated() throws Exception {
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
	public void update_PanelIsValid_PanelUpdated() throws Exception {
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
	public void update_PanelDoesNotExist_NotFoundErrorReturned() throws Exception {
		Long panelId = 1L;
		stub(panelService.exists(1L)).toReturn(false);

		Panel panel = new Panel("100001", 75.645289, 75.147852, "suntech", UnitOfMeasure.KW);

		MvcResult result = mockMvc.perform(put("/api/panels/" + panelId).content(JsonConverter.toJson(panel))
				.contentType(MediaType.APPLICATION_JSON)).andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
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
