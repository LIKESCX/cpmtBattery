package com.cpit.cpmt.biz.config.db;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.cpit.common.db.PageHelper;

@Configuration
public class DataSourceConfig {
	
	@Value("${ds.if.dynamic}")
	String ifDynamicDS;

	@Primary
	@Bean
    @ConfigurationProperties("spring.datasource.main")
    public DataSource main() {
        return DataSourceBuilder.create().build();
    }
 
 
    @Bean
    @ConfigurationProperties("spring.datasource.read")
    public DataSource read() {
        return DataSourceBuilder.create().build();
    }
    
	@Bean
	public DataSource dynamicDataSource(
			@Qualifier("main") DataSource main,
			@Qualifier("read") DataSource read
			) {
		if("off".equals(ifDynamicDS)) {
			return main; //不用动态切换模式
		}
		Map<Object, Object> targetDataSources = new HashMap<>(2);
		targetDataSources.put("main", main);
		targetDataSources.put("read", read);
		DynamicDataSource dynamicDataSource = new DynamicDataSource();
		dynamicDataSource.setDefaultTargetDataSource(main); //默认的
		dynamicDataSource.setDataSources(targetDataSources);
		return dynamicDataSource;
	}
	
	@Bean
	public SqlSessionFactory sqlSessionFactory(
		@Qualifier("dynamicDataSource") DataSource dynamicDataSource)
			throws Exception {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dynamicDataSource);
		//bean.setTypeAliasesPackage("com.cpit.cpmt.dto");    // 扫描Model
		bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mappings/**/*.xml"));
		
		PageHelper pageHelper = new PageHelper();
		Properties p = new Properties();
		p.put("dialect","mysql");
		pageHelper.setProperties(p);
		
		bean.setPlugins(new Interceptor[]{pageHelper});
		return bean.getObject();
	}

	@Bean(name = "sqlSessionTemplate")
	public SqlSessionTemplate sqlSessionTemplate(
		@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory)
			throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory);
	}

    @Bean
    public PlatformTransactionManager transactionManager(
    	@Qualifier("dynamicDataSource") DataSource dynamicDataSource
    ) {
        // 配置事务管理, 使用事务时在方法头部添加@Transactional注解即可
        return new DataSourceTransactionManager(dynamicDataSource);
    }
    
}
