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

import com.cpit.cpmt.biz.impl.exchange.basic.AlarmInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.basic.TestAlarmInfoMgmt;
import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.common.ResultInfo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.DEFINED_PORT)
//---scx
public class TestAlarmInfoController {
	private final static Logger logger = LoggerFactory.getLogger(TestAlarmInfoController.class);
	
	@Value("http://localhost:28060/exchange/collect/")
	private String url;
	
	@Autowired TestAlarmInfoMgmt testAlarmInfoMgmt;
		
	@Autowired TestRestTemplate testRestTemplate;
	@Test
	public void query_alarmInfo() {
		try {
			String stationID = "1111111";
			String speratorID = "22222222";
			String equipmentID = "333333333";
			url = url+"query_alarm_info?"+"stationID="+stationID+"&speratorID="+speratorID+"&equipmentID="+equipmentID;
			logger.info("url="+url);
			ResultInfo resultInfo = testRestTemplate.getForObject(url, ResultInfo.class);
			logger.info("\n测试类中打印返回结果:"+resultInfo);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
