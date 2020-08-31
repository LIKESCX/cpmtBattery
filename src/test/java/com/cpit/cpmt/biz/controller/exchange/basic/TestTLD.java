package com.cpit.cpmt.biz.controller.exchange.basic;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.cpit.common.Dispatcher;
import com.cpit.common.JsonUtil;
import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.dao.exchange.operator.ConnectorInfoDAO;
import com.cpit.cpmt.biz.impl.exchange.operator.StationInfoMgmt;
import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.biz.utils.exchange.Consts;
import com.cpit.cpmt.biz.utils.exchange.JsonValidate;
import com.cpit.cpmt.biz.utils.exchange.SeqUtil;
import com.cpit.cpmt.biz.utils.validate.DataSigCheck;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfo;
import com.cpit.cpmt.dto.exchange.basic.StationInfo;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import com.alibaba.fastjson.JSONArray;
@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.NONE)
public class TestTLD {
	private String token;
	@Autowired 
	private DataSigCheck dataSigCheck;
	@Autowired StationInfoMgmt stationInfoMgmt;
	@Autowired ConnectorInfoDAO connectorInfoDao;
	
	@Test
	public void testDao() {
	//	connectorInfoDao.getConnectorList("100000000001052", "566232124");
	//	2322001
		//connectorInfoDao.getConnectorById("2322001");
	}
	
	
	@Test
	@Before
	public void queryToken() throws Exception {
		//String url = "http://hlht.telaidian.com.cn:9501/evcs/v20161110/query_token";
		String url ="http://124.205.228.170:48080/evcs/20160701/query_token";
		//String url = "https://120.241.28.5:8020/cp/exchange/collect/query_token";
		//String url = "https://10.3.10.167:36011/cp/exchange/collect/query_token";
		RestTemplate restTemplate =new RestTemplate();
		Map<String,Object> dataMap = new LinkedHashMap<String,Object>();
		dataMap.put("OperatorID", "45581668X");
		dataMap.put("OperatorSecret", "ABCDEF0123456789");
        
		String operatorID = "45581668X";
		//String timeStamp = TimeConvertor.getDate(TimeConvertor.FORMAT_NONSYMBOL_24HOUR);
		String timeStamp = "20191010143640";
		String seq = "0001";
		
		String data = JsonUtil.beanToJson(dataMap);
		//System.out.println("+++ "+data);
		data = dataSigCheck.encodeContentData(data);
		
		String msg = operatorID+data+timeStamp+seq;
		String sig = dataSigCheck.genSign(msg);
        
		Map<String,Object> reqMap = new LinkedHashMap<String,Object>();
		reqMap.put("OperatorID", operatorID);
		reqMap.put("Data", data);
		reqMap.put("TimeStamp", timeStamp);
		reqMap.put("Seq", seq);
		reqMap.put("Sig",sig);
	
		String json = JsonUtil.beanToJson(reqMap);
		//System.out.println("+++ "+json);
		Dispatcher dispatcher = new Dispatcher(restTemplate);
		String result = (String)dispatcher.doPost(url, String.class, json);
		//System.out.println("=== "+result);
		reqMap = (Map<String,Object>)JsonUtil.jsonToBean(result, Map.class);
	String encodeData = (String) reqMap.get("Data");
	String o_data = dataSigCheck.decodeContentData(encodeData);

	 token = JSON.parseObject(o_data).getString("AccessToken");
		//token = (String)o_data.get("AccessToken");
		//System.out.println("==="+token);
		
	}
	
	@Test
	public void test_queryStationInfo() throws Exception {
		String url ="http://124.205.228.170:48080/evcs/20160701/query_stations_info";
		//String url = "http://hlht.telaidian.com.cn:9501/evcs/v20161110/query_stations_info";
		String timeStamp = TimeConvertor.getDate(TimeConvertor.FORMAT_NONSYMBOL_24HOUR);
		String seq = SeqUtil.getUniqueInstance().getSeq();
		Map<String,Object> map = new LinkedHashMap<String,Object>();
		map.put("LastQueryTime", "");
		map.put("PageNo", 1);
		map.put("PageSize", 10);
		String beanToJson = JsonUtil.beanToJson(map);
		String data = dataSigCheck.encodeContentData(beanToJson);
		Map<String,Object> reqMap =new LinkedHashMap<String,Object>();
		reqMap.put("OperatorID", "45581668X");
		reqMap.put("Data", data);
		reqMap.put("TimeStamp", timeStamp);
		reqMap.put("Seq", "0001");
		String msg = "45581668X"+data+timeStamp+"0001";
		//System.out.println("--msg "+msg);
		String sig = dataSigCheck.genSign(msg);
		reqMap.put("Sig", sig);
		String param = JsonUtil.beanToJson(reqMap);
		
		RestTemplate restTemplate = new RestTemplate();
		
		 
		Dispatcher dispatcher = new Dispatcher(restTemplate);
		String retJson = (String)dispatcher.doPost(token, url, String.class, param);
		
		//String retJson = (String)new Dispatcher(restTemplate).doPost(queryUrl,String.class, param);
		String receivedTime = TimeConvertor.getDate(TimeConvertor.FORMAT_MINUS_24HOUR);
		

	//System.out.println(retJson);
	
	Map<String,Object> resMap =new HashMap<String,Object>();
	resMap = (Map<String,Object>)JsonUtil.jsonToBean(retJson, Map.class);
	if(0==(int)(resMap.get("Ret"))) {
	String encodeData = (String) resMap.get("Data");
	String o_data = dataSigCheck.decodeContentData(encodeData);
	System.out.println(o_data);
	JSONArray jsonArray = JSON.parseObject(o_data).getJSONArray("StationInfos");
	List<StationInfo> stationInfos = JsonUtil.mkList(jsonArray, StationInfo.class, true);
	for(StationInfo s:stationInfos) {
		stationInfoMgmt.addQueryStation(s);
	}
	
	
	}else {
		
	}
	}
	
	
	@Test
	public void testSig() {
		String s ="45581668X3cYGcsYXezutkwUCXxOgo4F8WL1gNJNluWr0DJzyh0Xs3JLJTG+w6rP8jMmmD/kVs9wUBWpX0wpPZFzNd8iMRA==201910101436400001";
		String sig = dataSigCheck.genSign(s);
	//	System.out.println(sig.equals("E67BCFE520D99AB134324AEF5AFCD050"));
		
	}
	
  @Test
  public void testEncode() {

		Map<String,Object> dataMap = new LinkedHashMap<String,Object>();
		dataMap.put("OperatorID", "123456789");
		dataMap.put("OperatorSecret", "1234567890abcdef");
		//String data ="{\r\n" + 
		//		"\"OperatorID\":\"123456789\",\r\n" + 
		//		"\"OperatorSecret\":\"1234567890abcdef\"\r\n" + 
		//		"}";
		//String data ="{\"OperatorID\":\"123456789\",\r\n\"OperatorSecret\":\"1234567890abcdef\"}";
		try {
			String data = JsonUtil.beanToJson(dataMap);
		//	System.out.println(data);
			
			//String s ="{\"total\":1,\"stationStatusInfo\":{\"operationID\":\"123456789\",\"stationID\":\"111111111111111\",\"connectorStatusInfos\":{\"connectorID\":1,\"equipmentID\":\"10000000000000000000001\",\"status\":4,\"currentA\":0,\"currentB\":0,\"currentC\":0,\"voltageA\":0,\"voltageB\":0,\"voltageC\":0,\"soc\":10,}}}";
			String en = dataSigCheck.encodeContentData(data);
			
		//	System.out.println("+++ "+en);
			            //il7B0BSEjFdzpyKzfOFpvg/Se1CP802RItKYFPfSLRxJ3jf0bVl9hvYOEktPAYW2nd7S8MBcyHYyacHKbISq5iTmDzG+ivnR+SZJv3USNTYVMz9rCQVSxd0cLlqsJauko79NnwQJbzDTyLooYoIwz75qBOH2/xOMirpeEqRJrF/EQjWekJmGk9RtboXePu2rka+Xm51syBPhiXJAq0GfbfaFu9tNqs/e2Vjja/ltE1M0lqvxfXQ6da6HrThsm5id4ClZFIi0acRfrsPLRixS/IQYtksxghvJwbqOsbIsITail9Ayy4tKcogeEZiOO+4Ed264NSKmk7l3wKwJLAFjCFogBx8GE3OBz4pqcAn/ydA=
			String ss ="il7B0BSEjFdzpyKzfOFpvg/Se1CP802RItKYFPfSLRxJ3jf0bVl9hvYOEktPAYW2nd7S8MBcyHYyacHKbISq5iTmDzG+ivnR+SZJv3USNTYVMz9rCQVSxd0cLlqsJauko79NnwQJbzDTyLooYoIwz75qBOH2/xOMirpeEqRJrF/EQjWekJmGk9RtboXePu2rka+Xm51syBPhiXJAq0GfbfaFu9tNqs/e2Vjja/ltE1M0lqvxfXQ6da6HrThsm5id4ClZFIi0acRfrsPLRixS/IQYtksxghvJwbqOsbIsITail9Ayy4tKcogeEZiOO+4Ed264NSKmk7l3wKwJLAFjCFogBx8GE3OBz4pqcAn/ydA=";
			//System.out.println("+++ "+en.equals(ss));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
  }
}
