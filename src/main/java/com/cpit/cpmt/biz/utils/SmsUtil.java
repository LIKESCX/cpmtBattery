package com.cpit.cpmt.biz.utils;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cpit.cpmt.biz.common.JsonUtil;
import com.cpit.cpmt.biz.common.SpringContextHolder;
import com.cpit.cpmt.biz.dto.Dispatcher;



public class SmsUtil {
	
	private final static Logger logger = LoggerFactory.getLogger(SmsUtil.class);

	private static String serverUrl = "";
	
	static {
		Environment environment = SpringContextHolder.getApplicationContext().getEnvironment();
		serverUrl = environment.getProperty("sms.server.url");

		//serverUrl = "http://172.16.11.100:8050/sms/";
	}
	
	public static JSONObject getToken(){
		JSONObject jsonObject = null;
		try {
			String appid = "e158e100ffad42c28da0073786ca0b81";
			String appsecret = "1c36490597e44e4292108c0843a1c1da";
			String url = serverUrl+"B/BasicApi/GetAccessToken?appid="+appid+"&appsecret="+appsecret;
			/*
			HttpClient httpClient = HttpClients
					.custom()
					.setRetryHandler(new HttpRequestRetryHandler() {
							@Override
							public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
								return false;
							}
						})
					.build();

			
			HttpGet httpGet = new HttpGet(URL);
			HttpResponse execute = httpClient.execute(httpGet);
			HttpEntity entity = execute.getEntity();
			String result = EntityUtils.toString(entity, "utf-8");
			*/
			String result = (String)new Dispatcher(new RestTemplate()).doGet(url, String.class, null);
			jsonObject = JSON.parseObject(result);
		} catch (Exception e) {
			logger.error("getToken fail:"+e);
		}
		return jsonObject;
	}
	
	public static String sendMessage(String accessToken,String phone,String SmsContent) {
		try {
			String authorSecret = "13edf1c070b44fb3a90c5da787dc7d6a";
			String url = serverUrl+"C/SmsApi/SendSmsToUser";
			
//			HttpClient httpClient = HttpClients.createDefault();
//			HttpPost httpPost = new HttpPost(url);
//			httpPost.setHeader("Content-Type", "application/json");
//			httpPost.setHeader("authorSecret", authorSecret);
//			httpPost.setHeader("accessToken", accessToken);
//			
//			Map<String, String> map = new HashMap<>();
//			map.put("Phone", phone);
//			map.put("SmsContent", SmsContent);
//			
//			String json = JsonUtil.beanToJson(map);
//			StringEntity param = new StringEntity(json,"utf-8");
//			httpPost.setEntity(param);
//			HttpResponse execute = httpClient.execute(httpPost);
//			HttpEntity entity = execute.getEntity();
//			return EntityUtils.toString(entity, "utf-8");
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			headers.add("authorSecret", authorSecret);
			headers.add("accessToken", accessToken);
			Map<String, String> map = new HashMap<>();
			map.put("Phone", phone);
			map.put("SmsContent", SmsContent);
			String json = JsonUtil.beanToJson(map);
			
			return (String)new Dispatcher(new RestTemplate()).doPost(headers, url, String.class, json);
		} catch (Exception e) {
			logger.error("sendMessage fail:"+e);
		}
		return "";
	}
	
	public static void main(String[] args) {
		System.out.println(getToken());
	}

}
