package com.cpit.cpmt.biz.controller.exchange.basic;

import java.util.HashMap;
import java.util.Map;

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

import com.cpit.common.JsonUtil;
import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.operator.ExcOperFlow;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.DEFINED_PORT)

public class TestStationStatusInfoController {
	private final static Logger logger = LoggerFactory.getLogger(TestStationStatusInfoController.class);
	@Value("http://localhost:28060/exchange/collect/")
	private String url;
	
	@Autowired TestRestTemplate testRestTemplate;
	@Test
	public void query_stationsStatus() {
		try {
			String[] stationIDs = {"1111111","2222222"};//充电站ID 列表
			url = url+"query_stations_status";
			logger.info("url="+url);//http://localhost:28060/exchange/collect/query_stations_status
			//cotroller层测试
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String[]> entity = new HttpEntity<String[]>(stationIDs, headers);
			ResponseEntity<ResultInfo> postForEntity = this.testRestTemplate.postForEntity(url, entity, ResultInfo.class, "");
			ResultInfo body = postForEntity.getBody();
			logger.info("body="+body);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
