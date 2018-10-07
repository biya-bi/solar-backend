/**
 *
 */
package org.rainbow.solar.rest.converter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.rainbow.solar.rest.util.PanelHateoasUtil;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * This class tests the methods of the {@link PanelHateoasUtil} class.
 * 
 * @author biya-bi
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PanelHateoasTests {

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
		PanelHateoasUtil panelHateoasUtil = new PanelHateoasUtil(panelId);

		String expectedPanelUri = String.format("%s/%s", PANEL_ENDPOINT, panelId);

		Assert.assertTrue(panelHateoasUtil.buildUri().toString().endsWith(expectedPanelUri));
		Assert.assertTrue(panelHateoasUtil.buildHourlyUri().toString().endsWith(expectedPanelUri + "/hourly"));
		Assert.assertTrue(panelHateoasUtil.buildDailyUri().toString().endsWith(expectedPanelUri + "/daily"));
		Assert.assertTrue(
				panelHateoasUtil.buildHourlyCountUri().toString().endsWith(expectedPanelUri + "/hourly/count"));
	}

}
