package com.cpit.cpmt.biz.controller.exchange.basic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.common.ResultInfo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.DEFINED_PORT)

public class TestStationsInfoController {
private final static Logger logger = LoggerFactory.getLogger(TestAlarmInfoController.class);
	
	@Value("http://localhost:28060/exchange/collect/")
	private String url;
	@Autowired TestRestTemplate testRestTemplate;
	@Test
	public void query_stationInfo() {
		try {
			String lastQueryTime = "2019-08-08 18:18:18";
			String pageNo = "1";
			String pageSize = "10";
			url = url+"query_stations_info?"+"lastQueryTime="+lastQueryTime+"&pageNo="+pageNo+"&pageSize="+pageSize;
			logger.info("url="+url);
			ResultInfo resultInfo = testRestTemplate.getForObject(url, ResultInfo.class);
			logger.info("\n测试类中打印返回结果:"+resultInfo);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
