
package com.self.common.db;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

/**
 * MyBatis基础配置
 *
 * @author jung.zhang
 * @since 2016-07-07 10:11
 */
@Configuration
@EnableTransactionManagement
public class MyBatisConfig implements TransactionManagementConfigurer {

	@Value("${databaseRW.name}")
	private String name;

	@Value("${databaseRW.driverClassName}")
	private String className;

	@Value("${databaseRW.url}")
	private String url;

	@Value("${databaseRW.username}")
	private String username;

	@Value("${databaseRW.password}")
	private String password;

	@Value("${databaseRW.maxPoolSize}")
	private int maxPoolSize;

	@Value("${databaseRW.minPoolSize}")
	private int minPoolSize;

	@Value("${databaseRW.initialPoolSize}")
	private int initialPoolSize;

	@Value("${databaseRW.maxIdleTime}")
	private int maxIdleTime;

	/**
	 * 配置dataSource，使用C3P0连接池
	 * 
	 * @throws PropertyVetoException
	 */
	@Bean(destroyMethod = "close")
	@Primary
	public DataSource dataSource1() throws PropertyVetoException {

		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setDataSourceName(name);
		dataSource.setDriverClass(className);
		dataSource.setJdbcUrl(url);
		dataSource.setUser(username);
		dataSource.setPassword(password);
		dataSource.setMaxPoolSize(maxPoolSize);
		dataSource.setMinPoolSize(minPoolSize);
		dataSource.setInitialPoolSize(initialPoolSize);
		dataSource.setMaxIdleTime(maxIdleTime);
		return dataSource;
	}

	/**
	 * Transaction 相关配置 因为有两个数据源，
	 * 所有使用ChainedTransactionManager把两个DataSourceTransactionManager包括在一起。
	 */
	@Override
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		DataSourceTransactionManager dtm1;
		ChainedTransactionManager ctm = null;
		try {
			dtm1 = new DataSourceTransactionManager(dataSource1());

			ctm = new ChainedTransactionManager(dtm1);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}

		return ctm;
	}

}