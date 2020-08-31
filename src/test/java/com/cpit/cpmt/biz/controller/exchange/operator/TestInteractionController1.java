package com.cpit.cpmt.biz.controller.exchange.operator;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.cpit.cpmt.dto.exchange.operator.ExcOperFlow;
import com.cpit.cpmt.dto.exchange.operator.ExcParameterCtl;
import com.cpit.cpmt.dto.exchange.operator.ExcThirdAuthentication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.DEFINED_PORT)
public class TestInteractionController1 {

	@Autowired
	private TestRestTemplate restTemplate;
	
	@Value("http://localhost:28060/exchange/operator/")
	private String url;
	
	
	//获取运营商列表
	@Test
	public void getOperFlowByParam() {
		try {
			String urls = url  + "getOperFlowByParam/1/10";
			ExcOperFlow bb = new ExcOperFlow();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			Map<String, String> aa = new HashMap<String, String>();
			HttpEntity<ExcOperFlow> entity = new HttpEntity<ExcOperFlow>(bb, headers);
			ResponseEntity<String> postForEntity = this.restTemplate.postForEntity(urls, entity, String.class, aa);
			String body = postForEntity.getBody();
			System.out.println(body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
		
		
		
}
