package com.cpit.cpmt.biz.controller.exchange.operator;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.exchange.operator.ExcOperFlow;
import com.cpit.cpmt.dto.exchange.operator.ExcParameterCtl;
import com.cpit.cpmt.dto.exchange.operator.ExcThirdAuthentication;


@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.DEFINED_PORT)
public class TestInteractionController {

	@Autowired
	private TestRestTemplate restTemplate;
	private String url = "http://localhost:28060/exchange/intact/";
	
	@Test
	public void getOperFlowByOperId() {
		try {
			String urls = url  + "getOperFlowByOperId/1";
			ResponseEntity<String> forEntity = this.restTemplate.getForEntity(urls, String.class);
			String body = forEntity.getBody();
			System.out.println(body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void addOperFlowByOperId() {
		try {
			String urls = url  + "addOperFlowByOperId";
			ExcOperFlow bb = new ExcOperFlow();
			bb.setOperatorId("5");
			bb.setUptdate(new Date());
			bb.setStatusCd((byte) 5);
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
	
	@Test
	public void updateOperFlowByOperId() {
		try {
			String urls = url  + "updateOperFlowByOperId";
			ExcOperFlow bb = new ExcOperFlow();
			bb.setOperatorId("5");
			bb.setUptdate(new Date());
			bb.setStatusCd((byte) 6);
			this.restTemplate.put(urls, bb);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getExcAuthenticationByParam() {
		try {
			String urls = url  + "getExcAuthenticationByParam/1/10";
			ExcThirdAuthentication bb = new ExcThirdAuthentication();
			bb.setStartDate(TimeConvertor.stringTime2Date("2019-08-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
			bb.setEndDate(TimeConvertor.stringTime2Date("2019-09-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			Map<String, String> aa = new HashMap<String, String>();
			HttpEntity<ExcThirdAuthentication> entity = new HttpEntity<ExcThirdAuthentication>(bb, headers);
			ResponseEntity<String> postForEntity = this.restTemplate.postForEntity(urls, entity, String.class, aa);
			String body = postForEntity.getBody();
			System.out.println(body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void addExcAuthentication() {
		try {
			String urls = url  + "addExcAuthentication";
			ExcThirdAuthentication bb = new ExcThirdAuthentication();
			List<Integer> list = new ArrayList<Integer>();
			list.add(10);
			list.add(11);
			list.add(12);
			list.add(13);
			bb.setInterFaceList(list);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			Map<String, String> aa = new HashMap<String, String>();
			HttpEntity<ExcThirdAuthentication> entity = new HttpEntity<ExcThirdAuthentication>(bb, headers);
			ResponseEntity<String> postForEntity = this.restTemplate.postForEntity(urls, entity, String.class, aa);
			String body = postForEntity.getBody();
			System.out.println(body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void updateExcAuthentication() {
		try {
			String urls = url  + "updateExcAuthentication";
			ExcThirdAuthentication bb = new ExcThirdAuthentication();
			bb.setAuthDesc("普天");
			bb.setAuthMethod((byte) 1);
			List<Integer> list = new ArrayList<Integer>();
			list.add(20);
			list.add(21);
			list.add(22);
			list.add(23);
			bb.setInterFaceList(list);
			bb.setAuthId(11);
			this.restTemplate.put(urls, bb);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getExcAuthenticationById() {
		try {
			String urls = url  + "getExcAuthenticationById/11";
			ResponseEntity<String> forEntity = this.restTemplate.getForEntity(urls, String.class);
			String body = forEntity.getBody();
			System.out.println(body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getAllInterfaceList() {
		try {
			String urls = url  + "getAllInterfaceList";
			ResponseEntity<String> forEntity = this.restTemplate.getForEntity(urls, String.class);
			String body = forEntity.getBody();
			System.out.println(body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getForeignIp() {
		try {
			String urls = url  + "getForeignIp";
			ResponseEntity<String> forEntity = this.restTemplate.getForEntity(urls, String.class);
			String body = forEntity.getBody();
			System.out.println(body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void delOperFlowByOperId() {
		try {
			String urls = url  + "delOperFlowByOperId/1";
			this.restTemplate.delete(urls);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getExcParameterCtlByParam() {
		try {
			String urls = url  + "getExcParameterCtlByParam/1/10";
			ExcParameterCtl bb = new ExcParameterCtl();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			Map<String, String> aa = new HashMap<String, String>();
			HttpEntity<ExcParameterCtl> entity = new HttpEntity<ExcParameterCtl>(bb, headers);
			ResponseEntity<String> postForEntity = this.restTemplate.postForEntity(urls, entity, String.class, aa);
			String body = postForEntity.getBody();
			System.out.println(body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void uptExcParameterCtl() {
		try {
			String urls = url  + "uptExcParameterCtl";
			ExcParameterCtl bb = new ExcParameterCtl();
			bb.setId(1);
			bb.setServerName("普天");
			this.restTemplate.put(urls, bb);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void delExcParameterCtl() {
		try {
			String urls = url  + "delExcParameterCtl/{1}";
			this.restTemplate.delete(urls);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void addExcParameterCtl() {
		try {
			String urls = url  + "addExcParameterCtl";
			ExcParameterCtl bb = new ExcParameterCtl();
			bb.setServerName("普天");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			Map<String, String> aa = new HashMap<String, String>();
			HttpEntity<ExcParameterCtl> entity = new HttpEntity<ExcParameterCtl>(bb, headers);
			ResponseEntity<String> postForEntity = this.restTemplate.postForEntity(urls, entity, String.class, aa);
			String body = postForEntity.getBody();
			System.out.println(body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
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
