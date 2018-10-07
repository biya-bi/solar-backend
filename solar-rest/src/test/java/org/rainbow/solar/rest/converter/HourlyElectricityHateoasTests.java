/**
 *
 */
package org.rainbow.solar.rest.converter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.rainbow.solar.rest.util.HourlyElectricityHateoasUtil;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * This class tests the methods of the {@link HourlyElectricityHateoasUtil}
 * class.
 * 
 * @author biya-bi
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class HourlyElectricityHateoasTests {

	/**
	 * 
	 */
	private static final String PANEL_ENDPOINT = "/api/panels";

	@Before
	public void setup() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest(null, PANEL_ENDPOINT);

		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
	}

	@Test
	public void buildMethods_PanelIdGiven_UrisBuilt() {
		Long panelId = 1L;
		Long hourlyElectricityId = 1L;
		HourlyElectricityHateoasUtil hourlyElectricityHateoasUtil = new HourlyElectricityHateoasUtil(
				hourlyElectricityId);

		String expectedHourlyElectricityUri = String.format("%s/%s/hourly/%s", PANEL_ENDPOINT, panelId,
				hourlyElectricityId);

		Assert.assertTrue(
				hourlyElectricityHateoasUtil.buildUri(panelId).toString().endsWith(expectedHourlyElectricityUri));
	}

}
