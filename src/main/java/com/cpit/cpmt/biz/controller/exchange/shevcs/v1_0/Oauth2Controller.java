package com.cpit.cpmt.biz.controller.exchange.shevcs.v1_0;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


import com.cpit.cpmt.biz.utils.TokenCacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cpit.cpmt.biz.utils.validate.DataSigCheck;
import com.cpit.cpmt.biz.utils.validate.ReturnCode;



@RestController
@RequestMapping(value= {"/shevcs/v0.9","/shevcs/v1.0"},method = {RequestMethod.POST})
public class Oauth2Controller{
	
	private final static Logger logger = LoggerFactory.getLogger(Oauth2Controller.class);
	
    @Autowired
    private DiscoveryClient client;

	@Autowired
	private DataSigCheck dataSigCheck;

	@Autowired
	private TokenCacheUtil tokenCacheUtil;



	@RequestMapping({"/query_token","/backup/query_token"})
  	public Object getTokenForOperator(
  			HttpServletRequest request, @RequestBody String content
  			){

		Map<String, Object> resMap = new LinkedHashMap<String, Object>();

		JSONObject jo = JSON.parseObject(content);
		JSONObject decodedDataJO = jo.getJSONObject("Data");
		String clientId = decodedDataJO.getString("OperatorID");
		String secret = decodedDataJO.getString("OperatorSecret");

		//维护用，则不判断调用次数
		if(request.getRequestURI().indexOf("/backup/query_token") == -1){
			//限制运用商调用queryToken次数
			int result = tokenCacheUtil.checkQueryTokenCount(clientId);
			if(result == -1){
				logger.info("operatorId("+clientId+") query token too many times today");
				resMap.put("Ret", ReturnCode.CODE_BUSY);
				resMap.put("Msg", ReturnCode.MSG_BUSY_TOO_MANY_QUERY_TOKEN);
				resMap.put("Data", "");
				dataSigCheck.mkReturnMap(resMap);
				return resMap;
			}
		}

		Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
		dataMap.put("OperatorID", clientId);
		dataMap.put("SuccStat", 0);
		dataMap.put("AccessToken", null);
		dataMap.put("TokenAvailableTime", null);
		
		try {
			dataMap.put("OperatorID", clientId);
			OAuth2AccessToken token = createNewToken(clientId, secret);
			// logger.info("---token is "+token+", type="+token.getTokenType());
			dataMap.put("AccessToken", token.getValue());
			dataMap.put("TokenAvailableTime", token.getExpiresIn());
			dataMap.put("FailReason", 0);
			
			resMap.put("Ret", ReturnCode.CODE_OK);
			resMap.put("Msg", "");
			resMap.put("Data", dataMap);
		} catch (Exception ex) {
			logger.error("error in getTokenForOperator", ex);
			resMap.put("Ret", ReturnCode.CODE_4003);
			resMap.put("Msg", ReturnCode.MSG_4003_OperatorId_Invalid);
			resMap.put("Data", "");
		}
		dataSigCheck.mkReturnMap(resMap);
		return resMap;
	}




    //==================================private methods

	private OAuth2AccessToken createNewToken(String clientId,String secret){
		OAuth2AccessToken token = tokenCacheUtil.get(clientId);
		if(token != null){
			if(!token.isExpired()) { //未过期，读缓存
				return token;
			}
		}


		//获取新的token
		ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
		
		String tokenUri = obtainUrl();
		
 	    resource.setAccessTokenUri(tokenUri);
		resource.setClientId(clientId);
		resource.setTokenName("oauth_token");
		resource.setId("cpmt");
		resource.setClientSecret(secret);
		resource.setScope(Arrays.asList("read"));
		resource.setUsername("tom");
		resource.setPassword("sonia");
		
		ResourceOwnerPasswordAccessTokenProvider provider = new ResourceOwnerPasswordAccessTokenProvider();
		OAuth2AccessToken accessToken = provider.obtainAccessToken(resource, new DefaultAccessTokenRequest());

		//放缓存
		tokenCacheUtil.set(clientId,accessToken);
			
		return accessToken;
	}
	
	private String obtainUrl() {
		List<ServiceInstance> instances = client.getInstances("cpmt-gateway");
		if(instances != null && instances.size() != 0) {
			return "http://"+instances.get(0).getHost() + ":" + instances.get(0).getPort()+"/oauth/token";
		}
		return null;
	}

}
