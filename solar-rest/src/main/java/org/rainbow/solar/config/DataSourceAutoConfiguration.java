/**
 *
 */
package org.rainbow.solar.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/**
 * @author biya-bi
 *
 */
public abstract class DataSourceAutoConfiguration {

	protected abstract String getDriverClassName();

	protected abstract String getUrl();

	protected abstract String getUsername();

	protected abstract String getPassword();

	protected abstract JpaVendorAdapter getJpaVendorAdapter();

	@Bean(name = "entityManagerFactory")
	@ConditionalOnBean(name = "dataSource")
	@ConditionalOnMissingBean
	public LocalContainerEntityManagerFactoryBean getEntityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(getDataSource());
		if (getEntityPackages() != null)
			em.setPackagesToScan(getEntityPackages());
		em.setJpaVendorAdapter(getJpaVendorAdapter());
		if (getAdditionalProperties() != null) {
			em.setJpaProperties(getAdditionalProperties());
		}
		return em;
	}

	@Bean(name = "transactionManager")
	@ConditionalOnMissingBean(type = "JpaTransactionManager")
	public JpaTransactionManager getTransactionManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);
		return transactionManager;
	}

	@Bean(name = "dataSource")
	@ConditionalOnMissingBean
	public DataSource getDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();

		dataSource.setDriverClassName(getDriverClassName());
		dataSource.setUrl(getUrl());
		dataSource.setUsername(getUsername());
		dataSource.setPassword(getPassword());

		return dataSource;
	}

	protected Properties getAdditionalProperties() {
		return null;
	}

	protected String[] getEntityPackages() {
		return null;
	}
}
