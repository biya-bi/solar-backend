/**
 *
 */
package org.rainbow.solar.rest.controller;

import java.util.ArrayList;
import java.util.List;

import org.rainbow.solar.RequestMappings;
import org.rainbow.solar.model.Panel;
import org.rainbow.solar.rest.converter.PanelDtoConverter;
import org.rainbow.solar.rest.dto.PanelDto;
import org.rainbow.solar.rest.err.PanelNotFoundError;
import org.rainbow.solar.rest.util.UriUtil;
import org.rainbow.solar.service.PanelService;
import org.springframework.beans.factory.annotation.Autowired;
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

	/**
	 * Register a panel and start receiving the electricity statistics.
	 * 
	 * @param panel to register.
	 * @return
	 */
	@PostMapping
	public ResponseEntity<?> create(@RequestBody PanelDto panelDto) {
		Panel panel = PanelDtoConverter.fromDto(panelDto);

		Panel createdPanel = panelService.create(panel);

		return ResponseEntity.created(UriUtil.buildUri(createdPanel.getId())).build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PanelDto panelDto) {
		if (!panelService.exists(id)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PanelNotFoundError(id));
		}
		Panel panel = PanelDtoConverter.fromDto(panelDto);
		panel.setId(id);
		panelService.update(panel);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Panel panel = panelService.getById(id);
		if (panel == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PanelNotFoundError(id));
		}
		panelService.delete(panel);
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public ResponseEntity<?> get(@PageableDefault(size = 5) Pageable pageable) {
		List<PanelDto> panelDtos = new ArrayList<>();
		panelService.getAllByOrderBySerialAsc(pageable).getContent()
				.forEach(p -> panelDtos.add(PanelDtoConverter.toDto(p)));
		return ResponseEntity.ok(panelDtos);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getById(@PathVariable Long id) {
		Panel panel = panelService.getById(id);
		if (panel == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PanelNotFoundError(id));
		}
		return ResponseEntity.ok(PanelDtoConverter.toDto(panel));
	}

	@GetMapping(path = "/count")
	public long count() {
		return panelService.count();
	}

}