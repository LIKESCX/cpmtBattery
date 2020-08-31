package com.cpit.cpmt.biz.main;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alibaba.fastjson.parser.ParserConfig;
import com.cpit.cpmt.biz.utils.mongodb.IdWorker;



@SpringBootApplication
@EnableMongoRepositories("com.cpit.cpmt.biz.dao.security.mongodb")
@EnableTransactionManagement
@EnableDiscoveryClient
@MapperScan(basePackages = "com.cpit.cpmt.biz.dao", annotationClass = com.cpit.cpmt.biz.common.MyBatisDao.class)
@ComponentScan(basePackages = { "com.cpit" })
public class Application {
	private final static Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		ParserConfig.getGlobalInstance().setSafeMode(true);
		System.setProperty("spring.devtools.restart.enabled", "true");
		SpringApplication app = new SpringApplication(Application.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.run(args);
		System.out.println("start cpmt battery docker processing job...");
		logger.info("start cpmt battery docker processing job...");
	}
	
	@Bean
	public IdWorker idWorker() {
		return new IdWorker();
	}


}
