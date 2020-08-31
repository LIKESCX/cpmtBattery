package com.cpit.cpmt.biz.controller.system;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import com.cpit.common.JsonUtil;
import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.system.OperateLog;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.DEFINED_PORT)
public class TestLogController {
	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void getOperationLog() throws Exception{
		String url = "/system/getOperationLog/1"; 
		
		OperateLog condition = new OperateLog();
 
    	String jsonStr = JsonUtil.beanToJson(condition);	
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> entity = new HttpEntity<String>(jsonStr,headers);

		ResultInfo result = restTemplate.postForObject(url, entity, ResultInfo.class);
		if(result.getResult() == ResultInfo.OK) {
			System.out.println("=================>>>>"+result.getData());
		}
	}		


}
