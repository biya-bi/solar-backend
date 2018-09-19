/**
 *
 */
package org.rainbow.solar.rest.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import org.rainbow.solar.rest.err.PanelNotFoundError;
import org.rainbow.solar.rest.err.SolarErrorCode;
import org.rainbow.solar.rest.handler.GlobalExceptionHandler;
import org.rainbow.solar.rest.util.ErrorMessagesResourceBundle;
import org.rainbow.solar.rest.util.JsonConverter;
import org.rainbow.solar.service.DailyElectricityService;
import org.rainbow.solar.service.PanelService;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * This class tests APIs in {@link DailyElectricityController}
 * 
 * @author biya-bi
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class DailyElectricityControllerTests {

	private MockMvc mockMvc;

	@InjectMocks
	private DailyElectricityController dailyElectricityController;

	@Mock
	private PanelService panelService;

	@Mock
	private DailyElectricityService dailyElectricityService;

	@Before
	public void setup() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(dailyElectricityController).setControllerAdvice(new GlobalExceptionHandler())
				.build();
	}

	@Test
	public void getBeforeToday_PanelIdGiven_DailyElectricitiesReturned() throws Exception {
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

		stub(dailyElectricityService.getBeforeDate(anyLong(), any())).toReturn(dailyElectrities);

		stub(panelService.exists(panelId)).toReturn(true);

		mockMvc.perform(get(uri)).andExpect(jsonPath("$", hasSize(3)));

		ArgumentCaptor<Long> argumentCaptor1 = ArgumentCaptor.forClass(Long.class);
		ArgumentCaptor<LocalDateTime> argumentCaptor2 = ArgumentCaptor.forClass(LocalDateTime.class);

		verify(dailyElectricityService).getBeforeDate(argumentCaptor1.capture(),
				argumentCaptor2.capture());
	}

	@Test
	public void getBeforeToday_PanelDoesNotExist_NotFoundErrorReturned() throws Exception {
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

}
