/**
 * 
 */
package org.rainbow.solar.repository;

import org.junit.runner.RunWith;
import org.rainbow.solar.repository.config.TestConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author biya-bi
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@DataJpaTest
public abstract class RepositoryTests {
}
