/**
 *
 */
package org.rainbow.solar.service;

import org.rainbow.solar.model.Panel;
import org.rainbow.solar.repository.PanelRepository;
import org.rainbow.solar.service.PanelService;
import org.rainbow.solar.service.exc.PanelSerialDuplicateException;
import org.rainbow.solar.service.exc.PanelSerialMaxLengthExceededException;
import org.rainbow.solar.service.exc.PanelSerialRequiredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 
 * @author biya-bi
 *
 */
@Service
public class PanelServiceImpl implements PanelService {

	@Autowired
	private PanelRepository panelRepository;

	private static final int MAX_SERIAL_NUMBER_LENGTH = 16;

	private void validate(Panel panel) {
		if (panel.getSerial() == null || panel.getSerial().trim().isEmpty())
			throw new PanelSerialRequiredException();
		if (panel.getSerial().length() > MAX_SERIAL_NUMBER_LENGTH)
			throw new PanelSerialMaxLengthExceededException(panel.getSerial(), MAX_SERIAL_NUMBER_LENGTH);

		if (panelRepository.isDuplicateSerial(panel.getId(), panel.getSerial()))
			throw new PanelSerialDuplicateException(panel.getSerial());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rainbow.solar.service.Service#create(java.lang.Object)
	 */
	@Override
	public Panel create(Panel panel) {
		validate(panel);
		return panelRepository.save(panel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rainbow.solar.service.Service#update(java.lang.Object)
	 */
	@Override
	public Panel update(Panel panel) {
		validate(panel);
		return panelRepository.save(panel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rainbow.solar.service.Service#delete(java.lang.Object)
	 */
	@Override
	public void delete(Panel panel) {
		panelRepository.delete(panel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rainbow.solar.service.Service#getById(java.lang.Long)
	 */
	@Override
	public Panel getById(Long id) {
		return panelRepository.findById(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rainbow.solar.service.PanelService#getBySerial(java.lang.String)
	 */
	@Override
	public Panel getBySerial(String serial) {
		return panelRepository.findBySerial(serial);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rainbow.solar.service.Service#exists(java.lang.Long)
	 */
	@Override
	public boolean exists(Long id) {
		return panelRepository.exists(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rainbow.solar.service.PanelService#getAllByOrderBySerialAsc()
	 */
	@Override
	public Page<Panel> getAllByOrderBySerialAsc(Pageable pageable) {
		return panelRepository.findAllByOrderBySerialAsc(pageable);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rainbow.solar.service.Service#count()
	 */
	@Override
	public long count() {
		return panelRepository.count();
	}
}
