/**
 *
 */
package org.rainbow.solar.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

/**
 * @author biya-bi
 *
 */
@Configuration
@ConditionalOnClass(DataSource.class)
@ConditionalOnResource(resources = "classpath:application.properties")
@PropertySource("classpath:application.properties")
public class HibernateDataSourceAutoConfiguration extends DataSourceAutoConfiguration {

	@Value("${spring.datasource.driver-class-name}")
	private String driverClassName;

	@Value("${spring.datasource.url}")
	private String url;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;

	@Value("${spring.jpa.properties.hibernate.dialect}")
	private String dialect;

	@Value("${spring.jpa.show-sql}")
	private String show_sql;

	@Value("spring.jpa.generate-ddl")
	private String generate_ddl;

	@Override
	protected String getDriverClassName() {
		return driverClassName;
	}

	@Override
	protected String getUrl() {
		return url;
	}

	@Override
	protected String getUsername() {
		return username;
	}

	@Override
	protected String getPassword() {
		return password;
	}

	@Override
	protected JpaVendorAdapter getJpaVendorAdapter() {
		return new HibernateJpaVendorAdapter();
	}

	@Override
	protected Properties getAdditionalProperties() {
		Properties properties = new Properties();

		properties.setProperty("hibernate.hbm2ddl.auto", generate_ddl != null ? generate_ddl : "none");
		properties.setProperty("hibernate.dialect", dialect);
		properties.setProperty("hibernate.show_sql", show_sql != null ? show_sql : "false");

		return properties;
	}

	@Override
	protected String[] getEntityPackages() {
		return new String[] { "org.rainbow.solar.model" };
	}

}
