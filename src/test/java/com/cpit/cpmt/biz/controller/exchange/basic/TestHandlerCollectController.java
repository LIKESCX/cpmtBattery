package com.cpit.cpmt.biz.controller.exchange.basic;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
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
import com.cpit.cpmt.dto.exchange.basic.SupplyCollect;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.DEFINED_PORT)
public class TestHandlerCollectController {
	@Autowired
	private TestRestTemplate restTemplate;
	
	@Value("http://localhost:28060/exchange/collect")
	private String url;
	
	//获取补采列表
	@Test
	public void getUncollectInfos() {
		try {
			String urls = url  + "/queryUncollectInfos?pageNumber="+1+"&pageSize="+10;
			SupplyCollect supplyCollect = new SupplyCollect();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			Map<String, String> aa = new HashMap<String, String>();
			supplyCollect.setStationID("test001");
			supplyCollect.setOperatorID("1111111");
			supplyCollect.setEquipmentID("2222222");
			supplyCollect.setVersionNum("V1.0");
			supplyCollect.setInterfaceName("query_stations_info");
			supplyCollect.setStartTime("2019-09-25 10:52:26");
			supplyCollect.setEndTime("2019-09-25 10:52:30");
			HttpEntity<SupplyCollect> entity = new HttpEntity<SupplyCollect>(supplyCollect, headers);
			ResponseEntity<ResultInfo> postForEntity = this.restTemplate.postForEntity(urls, entity, ResultInfo.class, aa);
			ResultInfo body = postForEntity.getBody();
			System.out.println(body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

