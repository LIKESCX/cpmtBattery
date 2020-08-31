package com.cpit.cpmt.biz.controller.exchange.operator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.cpit.cpmt.biz.main.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.DEFINED_PORT)
public class TestBigScreenShowBmsInfoController {
	@Autowired
	private TestRestTemplate restTemplate;
	private String url = "http://localhost:28060/exchange/operator/";
	
	@Test
	public void bigScreenShowBmsInfo() {
		Integer pageNumber = 1;
		Integer pageSize = 10;
		String operatorId = "061402628";
		String stationId = "440201007";
		try {
			String urls = url  + "bigScreenShowBmsInfo/"+pageNumber+"/"+pageSize+"?operatorId="+operatorId+"&stationId="+stationId;
			ResponseEntity<String> forEntity = this.restTemplate.getForEntity(urls, String.class);
			String body = forEntity.getBody();
			System.out.println(body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getBmsInfoByConditions() {
		String operatorId = "667089963";
		String equipmentId = "2026001";
		String connectorId = "2026001001";
		try {
			String urls = url  + "getBmsInfoByConditions?operatorId="+operatorId+"&equipmentId="+equipmentId+"&connectorId="+connectorId;
			ResponseEntity<String> forEntity = this.restTemplate.getForEntity(urls, String.class);
			String body = forEntity.getBody();
			System.out.println(body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
