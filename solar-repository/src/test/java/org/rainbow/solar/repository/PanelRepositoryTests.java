/**
 * 
 */
package org.rainbow.solar.repository;

import org.junit.Assert;
import org.junit.Test;
import org.rainbow.solar.model.Panel;
import org.rainbow.solar.model.UnitOfMeasure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.Query;

/**
 * This class only tests the methods of the {@link PanelRepository} interface
 * for which we have provided a custom query using the {@link Query} annotation.
 * 
 * @author biya-bi
 *
 */
public class PanelRepositoryTests extends RepositoryTests {

	@Autowired
	private PanelRepository panelRepository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void isDuplicateSerial_IdIsNullAndSerialIsAssignedToExistingPanel_ReturnTrue() {
		createPanel(new Panel("100001", 70.650001, 72.512351, "canadiansolar", UnitOfMeasure.W));

		boolean actual = panelRepository.isDuplicateSerial(null, "100001");

		Assert.assertTrue(actual);
	}

	@Test
	public void isDuplicateSerial_IdIsNullAndSerialIsNotAssigned_ReturnFalse() {
		boolean actual = panelRepository.isDuplicateSerial(null, "100001");

		Assert.assertFalse(actual);
	}

	@Test
	public void isDuplicateSerial_IdAndSerialAreAssignedToSamePanel_ReturnFalse() {
		Panel panel = createPanel(new Panel("100001", 70.650001, 72.512351, "canadiansolar", UnitOfMeasure.W));

		boolean actual = panelRepository.isDuplicateSerial(panel.getId(), "100001");

		Assert.assertFalse(actual);
	}

	@Test
	public void isDuplicateSerial_IdIsNotNullAndSerialIsAssignedToAnotherPanel_ReturnTrue() {
		Panel panel = createPanel(new Panel("100001", 70.650001, 72.512351, "canadiansolar", UnitOfMeasure.W));
		createPanel(new Panel("100002", 70.650001, 72.512351, "suntech", UnitOfMeasure.W));

		boolean actual = panelRepository.isDuplicateSerial(panel.getId(), "100002");

		Assert.assertTrue(actual);
	}

	@Test
	public void isDuplicateSerial_IdIsNotNullAndSerialIsNotAssigned_ReturnFalse() {
		boolean actual = panelRepository.isDuplicateSerial(1L, "100001");

		Assert.assertFalse(actual);
	}

	private Panel createPanel(Panel panel) {
		entityManager.persist(panel);
		entityManager.flush();
		return panel;
	}
}
