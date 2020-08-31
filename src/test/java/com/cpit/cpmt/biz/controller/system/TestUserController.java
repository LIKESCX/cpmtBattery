package com.cpit.cpmt.biz.controller.system;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.cpit.cpmt.dto.system.User;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.DEFINED_PORT)
public class TestUserController {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void selectByCondition() throws Exception{
		String url = "/system/selectByCondition?pageNumber=1";
		
    	User condition = new User();
    	List<Integer> statusList = new ArrayList<Integer>();
    	//condition.setStatus(User.STATUS_LOCKED);
    	statusList.add(User.STATUS_DELETED);
    	statusList.add(User.STATUS_LOCKED);
    	statusList.add(User.STATUS_OK);
    	condition.setStatusList(statusList);

    	String jsonStr = JsonUtil.beanToJson(condition);	
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> entity = new HttpEntity<String>(jsonStr,headers);

		for(int i=0;i<5;i++) {
			ResultInfo result = restTemplate.postForObject(url, entity, ResultInfo.class);
			if(result.getResult() == ResultInfo.OK) {
				Map map = (Map)result.getData();
				List list = (List)map.get("infoList");
				if(list != null)
					System.out.println("-------size:"+list.size());
			}			
		}

	}		

}
