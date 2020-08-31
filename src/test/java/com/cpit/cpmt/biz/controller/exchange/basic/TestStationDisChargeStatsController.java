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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.common.ResultInfo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.DEFINED_PORT)

public class TestStationDisChargeStatsController {
	private final static Logger logger = LoggerFactory.getLogger(TestStationDisChargeStatsController.class);
	@Value("http://localhost:28060/exchange/collect/")
	private String url;
	
	@Autowired TestRestTemplate testRestTemplate;
	@Test
	public void query_station_discharge_stats() {
		try {
			String stationID ="1111111111";//充电站Id
			String operatorID ="testA1003";//充电站Id
			String startTime="2019-09-22";
			String endTime="2019-09-24";
			url = url+"query_station_discharge_stats?stationID="+stationID+"&operatorID="+operatorID+"&startTime="+startTime+"&endTime="+endTime;
			logger.info("url="+url);//http://localhost:28060/exchange/collect/query_station_discharge_stats
			ResponseEntity<ResultInfo> postForEntity = this.testRestTemplate.getForEntity(url, ResultInfo.class);
			ResultInfo body = postForEntity.getBody();
			logger.info("body="+body);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
