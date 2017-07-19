package com.zetainteractive.security;

import javax.sql.DataSource;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import org.springframework.transaction.support.TransactionTemplate;

@EnableTransactionManagement
@Configuration
@EnableCaching
public class AppConfiguration implements TransactionManagementConfigurer {
	
	@Bean
	@Scope("prototype")
	public	JdbcTemplate	jdbcTemplate() throws Exception {
		return new JdbcTemplate(dataSource(), true);
	}
	
	@Bean
	@Scope("prototype")
	public TransactionTemplate	txTemplate() throws Exception {
		return new TransactionTemplate(txManager());
	}
	@Bean
	@Scope("prototype")
	public	PlatformTransactionManager	txManager() {
		return	new	DataSourceTransactionManager(dataSource());
	}
	
	@Bean
	public	DataSource	dataSource()  {
		DriverManagerDataSource	dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://10.98.8.100:3306/security_dev?useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
		dataSource.setUsername("devuser");
		dataSource.setPassword("leo$123");
		/*try {
			dataSource.getConnection().setAutoCommit(false);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		return dataSource;
	}

	@Override
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		return txManager();
	}
}
