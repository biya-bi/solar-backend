/**
 * 
 */
package org.rainbow.solar.repository.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author biya-bi
 *
 */
@Configuration
@ComponentScan("org.rainbow.solar.repository")
@EnableAutoConfiguration
@EnableJpaRepositories( "org.rainbow.solar.repository")
@EntityScan("org.rainbow.solar.model")
public class TestConfig {
}
