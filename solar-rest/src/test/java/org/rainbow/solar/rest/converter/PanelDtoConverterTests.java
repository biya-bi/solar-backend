/**
 *
 */
package org.rainbow.solar.rest.converter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.rainbow.solar.model.Panel;
import org.rainbow.solar.model.UnitOfMeasure;
import org.rainbow.solar.rest.dto.PanelDto;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * This class tests the methods of the {@link PanelDtoConverter} class.
 * 
 * @author biya-bi
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PanelDtoConverterTests {

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
	public void toDto_PanelGiven_PanelDtoReturned() {
		Panel panel = new Panel(1L, "100001", 50.123456, 51.123456, "brand", UnitOfMeasure.KW);

		PanelDto panelDto = PanelDtoConverter.toDto(panel);

		Assert.assertEquals(panel.getSerial(), panelDto.getSerial());
		Assert.assertEquals(panel.getLatitude(), panelDto.getLatitude());
		Assert.assertEquals(panel.getLongitude(), panelDto.getLongitude());
		Assert.assertEquals(panel.getBrand(), panelDto.getBrand());
		Assert.assertEquals(panel.getUnitOfMeasure().toString(), panelDto.getUnitOfMeasure());
	}

	@Test
	public void fromDto_PanelDtoGiven_PanelReturned() {
		PanelDto panelDto = new PanelDto();
		panelDto.setBrand("100001");
		panelDto.setLatitude(50.123456);
		panelDto.setLongitude(51.123456);
		panelDto.setBrand("brand");
		panelDto.setUnitOfMeasure("KW");

		Panel panel = PanelDtoConverter.fromDto(panelDto);

		Assert.assertEquals(panelDto.getSerial(), panel.getSerial());
		Assert.assertEquals(panelDto.getLatitude(), panel.getLatitude());
		Assert.assertEquals(panelDto.getLongitude(), panel.getLongitude());
		Assert.assertEquals(panelDto.getBrand(), panel.getBrand());
		Assert.assertEquals(panelDto.getUnitOfMeasure(), panel.getUnitOfMeasure().toString());
	}

}
