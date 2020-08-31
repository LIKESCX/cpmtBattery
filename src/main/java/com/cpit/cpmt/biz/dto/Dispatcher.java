package com.cpit.cpmt.biz.dto;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


public class Dispatcher {

	private RestTemplate restTemplate = null;
	

	public Dispatcher(RestTemplate restTemplate){
		this.restTemplate = restTemplate;
	}
	
	/**
	 * get method
	 * @param url
	 * 如果url有参数，可直接在url中写死，这样params为null;或可通过params来设置。
	 * 方式1如rul="/order?name=xxx&age=18"
	 * 方式2如url="/order?name={name}&age={age}"
	 * params.put("name","xxx");
	 * params.put("age","18");
	 * @param responseClz 返回值的类型
	 * @param params 提交参数
	 * @return
	 */
	public Object doGet(String url,Class responseClz, Map<String,String> params){
		if(params == null){
			return restTemplate.getForObject(url, responseClz);
		}else{
			if(url.indexOf("?") == -1){
				//logger.error("if params is not empty, url must has ?");
				return null;
			}
			return restTemplate.getForObject(url, responseClz,params);
		}
		
	}

	
	/**
	 * post method
	 * @param url 
	 * 参考doGet
	 * 例外:如果url没有设参数，但params不为空，则将params作为header提交.效果和url带参数一样
	 * @param responseClz
	 * @param params
	 * @return
	 */
	/*
	public Object doPost(String url,Class responseClz, Map<String,String> params){
		if(params == null){
			return restTemplate.postForObject(url,HttpEntity.EMPTY, responseClz);
		}else if(url.indexOf("?") == -1){
			MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<String, String>();
			bodyMap.setAll(params);
			return restTemplate.postForObject(url,bodyMap,responseClz);
		}else{
			return restTemplate.postForObject(url,HttpEntity.EMPTY, responseClz,params);
		}
		
	}	
	*/
	
	/**
	 * Json提交
	 * @param url
	 * @param responseClz
	 * @param jsonObject
	 * @return
	 */
	public Object doPost(String url,Class responseClz, String jsonStr){
		if(jsonStr == null){
			return null;
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> entity = new HttpEntity<String>(jsonStr,headers);
		return restTemplate.postForObject(url,entity,responseClz);
	}
	
	/**
	 * 带token的Json提交
	 * @param token
	 * @param url
	 * @param responseClz
	 * @param jsonObject
	 * @return
	 */
	public Object doPost(String token, String url,Class responseClz, String jsonStr){
		if(jsonStr == null){
			return null;
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		headers.add("Authorization", "Bearer "+token);
		HttpEntity<String> entity = new HttpEntity<String>(jsonStr,headers);
		return restTemplate.postForObject(url,entity,responseClz);
	}
	
	/**
	 * 带header的提交请求
	 * @param headers
	 * @param url
	 * @param responseClz
	 * @param jsonStr
	 * @return
	 */
	public Object doPost(HttpHeaders headers, String url,Class responseClz, String jsonStr){
		if(jsonStr == null){
			return null;
		}
		HttpEntity<String> entity = new HttpEntity<String>(jsonStr,headers);
		return restTemplate.postForObject(url,entity,responseClz);
	}	
	
	/**
	 * put method
	 * @param url
	 * 如果url有参数，可直接在url中写死，这样params为null;或可通过params来设置。
	 * 方式1如rul="/order?name=xxx&age=18"
	 * 方式2如url="/order?name={name}&age={age}"
	 * params.put("name","xxx");
	 * params.put("age","18");
	 * @param params 提交参数
	 * @return
	 */
	public void doDelete(String url, Map<String,String> params){
		if(params == null){
			restTemplate.delete(url);
		}else{
			if(url.indexOf("?") == -1){
				return;
			}
			restTemplate.delete(url, params);
		}
		
	}
	
	/**
	 * put method
	 * @param url
	 * 如果url有参数，可直接在url中写死，这样params为null;或可通过params来设置。
	 * 方式1如rul="/order?name=xxx&age=18"
	 * 方式2如url="/order?name={name}&age={age}"
	 * params.put("name","xxx");
	 * params.put("age","18");
	 * @param updateObj 修改对象
	 * @param params 提交参数
	 * @return
	 */
	public void doPut(String url,Object updateObj, Map<String,String> params){
		if(params == null){
			restTemplate.put(url, updateObj);
		}else{
			if(url.indexOf("?") == -1){
				return;
			}
			restTemplate.put(url, updateObj,params);
		}
		
	}
	
}
