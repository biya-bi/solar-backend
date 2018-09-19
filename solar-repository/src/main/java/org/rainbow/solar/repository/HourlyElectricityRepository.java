/**
 *
 */
package org.rainbow.solar.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.rainbow.solar.model.HourlyElectricity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * This interface encapsulates data access layer operations related to an hourly
 * electricity.
 * 
 * @author biya-bi
 */
@RestResource(exported = false)
public interface HourlyElectricityRepository extends PagingAndSortingRepository<HourlyElectricity, Long> {
	Page<HourlyElectricity> findAllByPanelIdOrderByReadingAtDesc(Long panelId, Pageable pageable);

	@Query("SELECT he FROM HourlyElectricity he WHERE he.panel.id=:panelId AND he.readingAt<:dateTime "
			+ "ORDER BY he.readingAt DESC")
	List<HourlyElectricity> findAllByPanelIdBeforeDate(@Param("panelId") Long panelId,
			@Param("dateTime") LocalDateTime dateTime);

	@Query("SELECT COUNT(id) FROM HourlyElectricity WHERE panel.id=:panelId")
	long countByPanelId(@Param("panelId") Long panelId);

	HourlyElectricity findById(Long id);
}
