/**
 *
 */
package org.rainbow.solar.rest.controller;

import java.util.ArrayList;
import java.util.List;

import org.rainbow.solar.RequestMappings;
import org.rainbow.solar.model.HourlyElectricity;
import org.rainbow.solar.model.Panel;
import org.rainbow.solar.rest.converter.HourlyElectricityDtoConverter;
import org.rainbow.solar.rest.dto.HourlyElectricityDto;
import org.rainbow.solar.rest.err.HourlyElectricityNotFoundError;
import org.rainbow.solar.rest.err.PanelNotFoundError;
import org.rainbow.solar.rest.util.HourlyElectricityHateoasUtil;
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
public class HourlyElectricityController {
	@Autowired
	private PanelService panelService;

	@Autowired
	private HourlyElectricityService hourlyElectricityService;

	/**
	 * Create hourly electricity.
	 * 
	 * @param panelId           The ID of the panel that generated the electricity.
	 * @param hourlyElectricity generated electricity by this panel.
	 * @return
	 */
	@PostMapping(path = "/{panelId}/hourly")
	public ResponseEntity<?> create(@PathVariable(value = "panelId") Long panelId,
			@RequestBody HourlyElectricity hourlyElectricity) {
		Panel panel = panelService.getById(panelId);
		if (panel == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PanelNotFoundError(panelId));
		}
		hourlyElectricity.setPanel(panel);
		HourlyElectricity createdHourlyElectricity = hourlyElectricityService.create(hourlyElectricity);
		return ResponseEntity
				.created(new HourlyElectricityHateoasUtil(createdHourlyElectricity.getId()).buildUri(panelId)).build();
	}

	@PutMapping(path = "/{panelId}/hourly/{hourlyElectricityId}")
	public ResponseEntity<?> update(@PathVariable(value = "panelId") Long panelId,
			@PathVariable(value = "hourlyElectricityId") Long hourlyElectricityId,
			@RequestBody HourlyElectricity hourlyElectricity) {
		Panel panel = panelService.getById(panelId);
		if (panel == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PanelNotFoundError(panelId));
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

	@DeleteMapping(path = "/{panelId}/hourly/{hourlyElectricityId}")
	public ResponseEntity<?> delete(@PathVariable(value = "panelId") Long panelId,
			@PathVariable(value = "hourlyElectricityId") Long hourlyElectricityId) {
		if (!panelService.exists(panelId)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PanelNotFoundError(panelId));
		}
		HourlyElectricity hourlyElectricity = hourlyElectricityService.getById(hourlyElectricityId);
		if (hourlyElectricity == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new HourlyElectricityNotFoundError(hourlyElectricityId));
		}

		hourlyElectricity.setPanel(new Panel(panelId));
		hourlyElectricityService.delete(hourlyElectricity);
		return ResponseEntity.noContent().build();
	}

	/**
	 * Get hourly electricity from previous dates.
	 */
	@GetMapping(path = "/{panelId}/hourly")
	public ResponseEntity<?> getByPanelId(@PathVariable(value = "panelId") Long panelId,
			@PageableDefault(size = 5) Pageable pageable) {
		if (!panelService.exists(panelId)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PanelNotFoundError(panelId));
		}
		Page<HourlyElectricity> page = hourlyElectricityService.getByPanelId(panelId, pageable);
		List<HourlyElectricityDto> hourlyElectricityDtos = new ArrayList<>();
		page.getContent().forEach(x -> hourlyElectricityDtos.add(HourlyElectricityDtoConverter.toDto(panelId, x)));
		return ResponseEntity.ok(hourlyElectricityDtos);
	}

	@GetMapping(path = "/{id}/hourly/count")
	public ResponseEntity<?> countByPanelId(@PathVariable(value = "id") Long id) {
		if (!panelService.exists(id)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PanelNotFoundError(id));
		}
		return ResponseEntity.ok(hourlyElectricityService.countByPanelId(id));
	}
}