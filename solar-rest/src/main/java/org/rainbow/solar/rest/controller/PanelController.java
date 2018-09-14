/**
 *
 */
package org.rainbow.solar.rest.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.rainbow.solar.RequestMappings;
import org.rainbow.solar.model.DailyElectricity;
import org.rainbow.solar.model.HourlyElectricity;
import org.rainbow.solar.model.Panel;
import org.rainbow.solar.rest.converter.HourlyElectricityDtoConverter;
import org.rainbow.solar.rest.converter.PanelDtoConverter;
import org.rainbow.solar.rest.dto.HourlyElectricityDto;
import org.rainbow.solar.rest.dto.PanelDto;
import org.rainbow.solar.rest.err.HourlyElectricityNotFoundError;
import org.rainbow.solar.rest.err.PanelNotFoundError;
import org.rainbow.solar.rest.util.UriUtil;
import org.rainbow.solar.service.HourlyElectricityService;
import org.rainbow.solar.service.PanelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author biya-bi
 *
 */
@CrossOrigin
@RestController
@RequestMapping(RequestMappings.PANEL_ENDPOINT)
public class PanelController {
	@Autowired
	private PanelService panelService;

	@Autowired
	private HourlyElectricityService hourlyElectricityService;

	/**
	 * Register a panel and start receiving the electricity statistics.
	 * 
	 * @param panel to register.
	 * @return
	 */
	@PostMapping
	public ResponseEntity<?> createPanel(@RequestBody PanelDto panelDto) {
		Panel panel = PanelDtoConverter.fromDto(panelDto);

		panelService.create(panel);

		return ResponseEntity.created(UriUtil.buildUri(panel.getId())).build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updatePanel(@PathVariable Long id, @RequestBody PanelDto panelDto) {
		if (!panelService.exists(id)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PanelNotFoundError(id));
		}
		Panel panel = PanelDtoConverter.fromDto(panelDto);
		panel.setId(id);
		panelService.update(panel);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deletePanel(@PathVariable Long id) {
		Panel panel = panelService.getById(id);
		if (panel == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PanelNotFoundError(id));
		}
		panelService.delete(panel);
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public ResponseEntity<?> getPanels(@PageableDefault(size = 5) Pageable pageable) {
		List<PanelDto> panelDtos = new ArrayList<>();
		panelService.getAllByOrderBySerialAsc(pageable).getContent()
				.forEach(p -> panelDtos.add(PanelDtoConverter.toDto(p)));
		return ResponseEntity.ok(panelDtos);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getPanel(@PathVariable Long id) {
		Panel panel = panelService.getById(id);
		if (panel == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PanelNotFoundError(id));
		}
		return ResponseEntity.ok(PanelDtoConverter.toDtoFromCurrentUri(panel));
	}

	/**
	 * Create hourly electricity.
	 * 
	 * @param id                The ID of the panel that generated the electricity.
	 * @param hourlyElectricity generated electricity by this panel.
	 * @return
	 */
	@PostMapping(path = "/{id}/hourly")
	public ResponseEntity<?> createHourlyElectricity(@PathVariable(value = "id") Long id,
			@RequestBody HourlyElectricity hourlyElectricity) {
		Panel panel = panelService.getById(id);
		if (panel == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PanelNotFoundError(id));
		}
		hourlyElectricity.setPanel(panel);
		hourlyElectricityService.create(hourlyElectricity);
		return ResponseEntity.created(UriUtil.buildUri(hourlyElectricity.getId())).build();
	}

	@PutMapping(path = "/{id}/hourly/{hourlyElectricityId}")
	public ResponseEntity<?> updateHourlyElectricity(@PathVariable(value = "id") Long id,
			@PathVariable(value = "hourlyElectricityId") Long hourlyElectricityId,
			@RequestBody HourlyElectricity hourlyElectricity) {
		Panel panel = panelService.getById(id);
		if (panel == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PanelNotFoundError(id));
		}
		if (!hourlyElectricityService.exists(hourlyElectricityId)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new HourlyElectricityNotFoundError(hourlyElectricityId));
		}
		hourlyElectricity.setPanel(panel);
		hourlyElectricity.setId(hourlyElectricityId);
		hourlyElectricityService.update(hourlyElectricity);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping(path = "/{id}/hourly/{hourlyElectricityId}")
	public ResponseEntity<?> deleteHourlyElectricity(@PathVariable Long id,
			@PathVariable(value = "hourlyElectricityId") Long hourlyElectricityId) {
		Panel panel = panelService.getById(id);
		if (panel == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PanelNotFoundError(id));
		}
		HourlyElectricity hourlyElectricity = hourlyElectricityService.getById(hourlyElectricityId);
		if (hourlyElectricity == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new HourlyElectricityNotFoundError(hourlyElectricityId));
		}

		Panel p = new Panel();
		p.setId(id);
		hourlyElectricity.setPanel(panel);
		hourlyElectricityService.delete(hourlyElectricity);
		return ResponseEntity.noContent().build();
	}

	/**
	 * Get hourly electricity from previous dates.
	 */
	@GetMapping(path = "/{id}/hourly")
	public ResponseEntity<?> getHourlyElectricities(@PathVariable(value = "id") Long id,
			@PageableDefault(size = 5) Pageable pageable) {
		Panel panel = panelService.getById(id);
		if (panel == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PanelNotFoundError(id));
		}
		Page<HourlyElectricity> page = hourlyElectricityService.getHourlyElectricities(id, pageable);
		List<HourlyElectricityDto> hourlyElectricityDtos = new ArrayList<>();
		page.getContent().forEach(x -> hourlyElectricityDtos.add(HourlyElectricityDtoConverter.toDto(id, x)));
		return ResponseEntity.ok(hourlyElectricityDtos);
	}

	/**
	 * This end point is used by Front end charts component to plot the daily
	 * statistics of electricity generated by this panel from the day it was
	 * registered to end of previous day.
	 * 
	 * @param id The ID of the panel.
	 * @return
	 */
	@GetMapping(path = "/{id}/daily")
	public ResponseEntity<?> getAllDailyElectricityFromYesterday(@PathVariable(value = "id") Long id,
			@PageableDefault(size = 5) Pageable pageable) {
		Panel panel = panelService.getById(id);
		if (panel == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PanelNotFoundError(id));
		}
		List<DailyElectricity> dailyElectricityForPanel = hourlyElectricityService.getDailyElectricitiesBeforeDate(id,
				LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT), pageable);
		return ResponseEntity.ok(dailyElectricityForPanel);
	}

	@GetMapping(path = "/count")
	public long getPanelsCount() {
		return panelService.count();
	}

	@GetMapping(path = "/{id}/hourly/count")
	public ResponseEntity<?> getHourlyElectricitiesCount(@PathVariable(value = "id") Long id) {
		if (!panelService.exists(id)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PanelNotFoundError(id));
		}
		return ResponseEntity.ok(hourlyElectricityService.getHourlyElectricitiesCount(id));
	}
}