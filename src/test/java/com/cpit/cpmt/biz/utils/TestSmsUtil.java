package com.cpit.cpmt.biz.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cpit.cpmt.biz.main.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.NONE)
public class TestSmsUtil {
	
	@Test
	public void obtainToken() {
		JSONObject jo = SmsUtil.getToken();
		System.out.println("token is"+jo);
	}
	
	@Test
	public void sendMsg() {
		JSONObject tokenInfo = (JSONObject) SmsUtil.getToken();
		System.out.println("getToken result is:"+tokenInfo);
		if(null != tokenInfo) {
			try {
				String resultData = tokenInfo.getString("resultData");
				JSONObject resultDataObject = JSON.parseObject(resultData);
				String accessToken = resultDataObject.getString("accessToken");
				System.out.println("accesstoken:"+accessToken);
				SmsUtil.sendMessage(accessToken, "", "hello");
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}


}
