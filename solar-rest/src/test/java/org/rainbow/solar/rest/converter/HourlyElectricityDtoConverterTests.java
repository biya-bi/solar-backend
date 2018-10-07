/**
 *
 */
package org.rainbow.solar.rest.converter;

import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.rainbow.solar.model.HourlyElectricity;
import org.rainbow.solar.model.Panel;
import org.rainbow.solar.model.UnitOfMeasure;
import org.rainbow.solar.rest.dto.HourlyElectricityDto;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * This class tests the methods of the {@link HourlyElectricityDtoConverter}
 * class.
 * 
 * @author biya-bi
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class HourlyElectricityDtoConverterTests {

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
	public void toDto_HourlyElectricityGiven_HourlyElectricityDtoReturned() {
		Panel panel = new Panel(1L, "100001", 50.123456, 51.123456, "brand", UnitOfMeasure.KW);
		HourlyElectricity hourlyElectricity = new HourlyElectricity(1L, panel, 1500L, LocalDateTime.now());

		HourlyElectricityDto hourlyElectricityDto = HourlyElectricityDtoConverter.toDto(1L, hourlyElectricity);

		Assert.assertEquals(hourlyElectricity.getGeneratedElectricity(),
				hourlyElectricityDto.getGeneratedElectricity());
		Assert.assertEquals(hourlyElectricity.getReadingAt(), hourlyElectricityDto.getReadingAt());
	}

	@Test
	public void fromDto_HourlyElectricityDtoGiven_HourlyElectricityReturned() {
		HourlyElectricityDto hourlyElectricityDto = new HourlyElectricityDto();
		hourlyElectricityDto.setGeneratedElectricity(1500L);
		hourlyElectricityDto.setReadingAt(LocalDateTime.now());

		HourlyElectricity hourlyElectricity = HourlyElectricityDtoConverter.fromDto(hourlyElectricityDto);

		Assert.assertEquals(hourlyElectricityDto.getGeneratedElectricity(),
				hourlyElectricity.getGeneratedElectricity());
		Assert.assertEquals(hourlyElectricityDto.getReadingAt(), hourlyElectricity.getReadingAt());
	}

}
