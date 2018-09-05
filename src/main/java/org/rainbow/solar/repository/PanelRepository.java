package org.rainbow.solar.repository;

import org.rainbow.solar.model.Panel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * PanelRepository allows all operations to PanelDto Entity.
 * 
 * @author biya-bi
 *
 */

@RestResource(exported = false)
public interface PanelRepository extends PagingAndSortingRepository<Panel, Long> {
	Panel findById(Long id);

	Panel findBySerial(String serial);

	@Query("SELECT COUNT(id)>0 FROM Panel WHERE ((:id is null) or id<>:id) AND serial=:serial")
	boolean isDuplicateSerial(@Param("id") Long id, @Param("serial") String serial);

	Page<Panel> findAllByOrderBySerialAsc(Pageable pageable);
}
