package org.rainbow.solar.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.rainbow.solar.dto.DailyElectricity;
import org.rainbow.solar.model.HourlyElectricity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * HourlyElectricity Repository is for all operations for HourlyElectricity.
 * 
 * @author biya-bi
 */
@RestResource(exported = false)
public interface HourlyElectricityRepository extends PagingAndSortingRepository<HourlyElectricity, Long> {
	Page<HourlyElectricity> findAllByPanelIdOrderByReadingAtDesc(Long panelId, Pageable pageable);

	@Query("SELECT new org.rainbow.solar.dto.DailyElectricity(YEAR(readingAt) as year, "
			+ "MONTH(readingAt) as month, DAY(readingAt) as day, SUM(generatedElectricity) as sum, "
			+ "AVG(generatedElectricity) as average, MIN(generatedElectricity) as min, "
			+ "MAX(generatedElectricity) as max) FROM HourlyElectricity "
			+ "WHERE panel.id=:panelId AND readingAt<:dateTime GROUP BY YEAR(readingAt),MONTH(readingAt),DAY(readingAt) "
			+ "ORDER BY YEAR(readingAt) DESC,MONTH(readingAt) DESC,DAY(readingAt) DESC")
	List<DailyElectricity> findDailyElectricitiesBeforeDate(@Param("panelId") Long panelId,
			@Param("dateTime") LocalDateTime dateTime, Pageable pageable);

	@Query("SELECT COUNT(id) FROM HourlyElectricity WHERE panel.id=:panelId")
	long findHourlyElectricitiesCount(@Param("panelId") Long panelId);
}
