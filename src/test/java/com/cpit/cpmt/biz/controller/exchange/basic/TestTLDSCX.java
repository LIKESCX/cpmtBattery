package com.cpit.cpmt.biz.controller.exchange.basic;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.cpit.common.Dispatcher;
import com.cpit.common.JsonUtil;
import com.cpit.common.SequenceId;
import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.dao.exchange.basic.ConnectorStatusInfoDao;
import com.cpit.cpmt.biz.impl.exchange.basic.StationsInfoMgmt;
import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.biz.utils.exchange.Consts;
import com.cpit.cpmt.biz.utils.exchange.JsonValidate;
import com.cpit.cpmt.biz.utils.exchange.SeqUtil;
import com.cpit.cpmt.biz.utils.validate.DataSigCheck;
import com.cpit.cpmt.dto.exchange.basic.ConnectorStatusInfo;
import com.cpit.cpmt.dto.exchange.basic.StationInfo;
import com.cpit.cpmt.dto.exchange.basic.StationStatusInfo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.NONE)
public class TestTLDSCX {
	private String token;
	@Autowired 
	private DataSigCheck dataSigCheck;
	@Autowired 
	private JsonValidate jsonValidate;
	@Autowired 
	private StationsInfoMgmt stationsInfoMgmt;
	@Autowired 
	private ConnectorStatusInfoDao connectorStatusInfoDao;
	@Test
	@Before
	public void queryToken() throws Exception {
		//String url = "http://hlht.telaidian.com.cn:9501/evcs/v20161110/query_token";//特来电
		//String url = "http://test.winlineev.com/internet-mutual/evcs/v1/query_token";//永联
		String url = "http://47.106.131.216/shevcs/quey_token";//水木华程
		//String url ="http://124.205.228.170:48080/evcs/20160701/query_token";
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
		System.out.println("+++ "+data);
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
		System.out.println("+++ "+json);
		Dispatcher dispatcher = new Dispatcher(restTemplate);
		String result = (String)dispatcher.doPost(url, String.class, json);
		System.out.println("=== "+result);
		reqMap = (Map<String,Object>)JsonUtil.jsonToBean(result, Map.class);
	String encodeData = (String) reqMap.get("Data");
	String o_data = dataSigCheck.decodeContentData(encodeData);

	 token = JSON.parseObject(o_data).getString("AccessToken");
		//token = (String)o_data.get("AccessToken");
		System.out.println("==="+token);
		
	}

	// 查询站点信息的接口.
	@Test
	public void test_queryStationInfo() throws Exception {
		//String url = "http://124.205.228.170:48080/evcs/20160701/query_stations_info";
		String url = "http://hlht.telaidian.com.cn:9501/evcs/v20161110/query_stations_info";
		String timeStamp = TimeConvertor.getDate(TimeConvertor.FORMAT_NONSYMBOL_24HOUR);
		String seq = SeqUtil.getUniqueInstance().getSeq();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("LastQueryTime", "");
		map.put("PageNo", 3);
		map.put("PageSize", 100);
		String beanToJson = JsonUtil.beanToJson(map);
		String data = dataSigCheck.encodeContentData(beanToJson);
		Map<String, Object> reqMap = new LinkedHashMap<String, Object>();
		reqMap.put("OperatorID", "45581668X");
		reqMap.put("Data", data);
		reqMap.put("TimeStamp", timeStamp);
		reqMap.put("Seq", "0001");
		String msg = "45581668X" + data + timeStamp + "0001";
		System.out.println("\n--msg " + msg);
		String sig = dataSigCheck.genSign(msg);
		reqMap.put("Sig", sig);
		String param = JsonUtil.beanToJson(reqMap);
		System.out.println("param:" + param);
		RestTemplate restTemplate = new RestTemplate();

		Dispatcher dispatcher = new Dispatcher(restTemplate);
		String retJson = (String) dispatcher.doPost(token, url, String.class, param);

		System.out.println("\nretJson:" + retJson);
		// String retJson = (String)new
		// Dispatcher(restTemplate).doPost(queryUrl,String.class, param);
		String receivedTime = TimeConvertor.getDate(TimeConvertor.FORMAT_MINUS_24HOUR);
		String objectName = "StationInfos";
		String result = JsonValidate.chgToStr(jsonValidate.validate(Consts.INTERFACE_VERSIONV1_0, objectName, retJson,null));
		System.out.println("\n校验结果result=" + result);
		String JsonStr = JSON.parseObject(result).getString("Data");
		JSONArray jsonArray = JSON.parseObject((String) JsonStr).getJSONArray("StationInfos");
		if(jsonArray.size()==0) {
			System.out.println("jsonArray.size()="+jsonArray.size());
			return;
		}
		List<StationInfo> stationsList = JsonUtil.mkList(jsonArray, StationInfo.class, true);
		System.out.println("stationsList length=" + stationsList.size());
		int i = 0;
		for (StationInfo stationInfo : stationsList) {
			stationsInfoMgmt.addQueryStation(stationInfo);
			i++;
		}

		/*Map<String, Object> resMap = new HashMap<String, Object>();
		resMap = (Map<String, Object>) JsonUtil.jsonToBean(retJson, Map.class);
		if (0 == (int) (resMap.get("Ret"))) {
			String encodeData = (String) resMap.get("Data");
			String o_data = dataSigCheck.decodeContentData(encodeData);
			System.out.println(o_data);
		} else {

		}*/
	}
	// 查询设备接口状态的接口.
	@Test
	public void test_queryStationsStatus() throws Exception {
		//String url = "http://124.205.228.170:48080/evcs/20160701/query_stations_status";
		String url ="http://hlht.telaidian.com.cn:9501/evcs/v20161110/query_station_status";
		String timeStamp = TimeConvertor.getDate(TimeConvertor.FORMAT_NONSYMBOL_24HOUR);
		String seq = SeqUtil.getUniqueInstance().getSeq();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		//普天的站
		/*String[] stationIDs = {"CABJ0A008408",
								"CABJ0A012008",
								"CABJ0A012408",
								"CABJ0A013508",
								"CABJ0A028708",
								"CABJ0A036008",
								"CABJ0B011208",
								"CABJ0B011706",
								"CABJ0B011906",
								"CABJ0B013606"};*/
		//特来电的站
		String[] stationIDs = {"4403060009",
				"3702120244",
				"4403060029",
				"4403050012",
				"4403040015",
				"4403040013",
				"4403060014",
				"4403030009",
				"4403050008",
				"4403040004",
				"4403060020",
				"4403040006",
				"4403060035",
				"4403050014",
				"4403050031",
				"4403060004",
				"4403030015",
				"4403030005",
				"4403070005",
				"4403060056",
				"4403070031",
				"4403060016",
				"4403050003",
				"4403050004",
				"4403050016",
				"4403060021",
				"4403070030",
				"4403060028",
				"4403030004",
				"4403040012",
				"4403060012",
				"4403070011",
				"4403050033",
				"4403060054",
				"4403070001",
				"4403060068",
				"4403070003",
				"4403050011",
				"4403050015",
				"4403030002",
				"4403060011",
				"4403040016",
				"4403060013",
				"4403060057",
				"4403060023",
				"4403070023",
				"4403030006",
				"4403070007",
				"4403050022",
				"4403070020"
				};
		map.put("StationIDs", stationIDs);
		String beanToJson = JsonUtil.beanToJson(map);
		System.out.println("beanToJson"+beanToJson);
		String data = dataSigCheck.encodeContentData(beanToJson);
		Map<String, Object> reqMap = new LinkedHashMap<String, Object>();
		reqMap.put("OperatorID", "45581668X");
		reqMap.put("Data", data);
		reqMap.put("TimeStamp", timeStamp);
		reqMap.put("Seq", "0001");
		String msg = "45581668X" + data + timeStamp + "0001";
		System.out.println("\n--msg " + msg);
		String sig = dataSigCheck.genSign(msg);
		reqMap.put("Sig", sig);
		String param = JsonUtil.beanToJson(reqMap);
		System.out.println("param:" + param);
		RestTemplate restTemplate = new RestTemplate();
		
		Dispatcher dispatcher = new Dispatcher(restTemplate);
		String retJson = (String) dispatcher.doPost(token, url, String.class, param);
		System.out.println("\nretJson:" + retJson);
		Date receivedTime = new Date();
		String objectName = "StationStatusInfos";
		//先跳过校验
		String result = JsonValidate.chgToStr(jsonValidate.validate(Consts.INTERFACE_VERSIONV1_0, objectName, retJson,null));		
		System.out.println("\n校验结果result=" + result);
		//假设result没问题.入库
		String JsonStr = JSON.parseObject(result).getString("Data");
		JSONArray jsonArray = JSON.parseObject((String)JsonStr).getJSONArray("StationStatusInfos");
		List<StationStatusInfo> infoList = JsonUtil.mkList(jsonArray, StationStatusInfo.class, true);
		
		for (StationStatusInfo stationStatusInfo : infoList) {
			List<ConnectorStatusInfo> connectorStatusInfos = stationStatusInfo.getConnectorStatusInfos();
			String inTime = TimeConvertor.getDate(TimeConvertor.FORMAT_MINUS_24HOUR);
			for (ConnectorStatusInfo connectorStatusInfo : connectorStatusInfos) {
				connectorStatusInfoDao.selectByConditions("395815801",connectorStatusInfo.getConnectorID());
				int cpmtBizConnectorStatusInfoId= SequenceId.getInstance().getId("cpmtBizConnectorStatusInfoId");
				connectorStatusInfo.setId(cpmtBizConnectorStatusInfoId);
				connectorStatusInfo.setOperatorID("395815801");
				connectorStatusInfo.setReceivedTime(receivedTime);
				connectorStatusInfo.setInTime(new Date());
				connectorStatusInfoDao.insertSelective(connectorStatusInfo);
			}
		}

		
		/*Map<String, Object> resMap = new HashMap<String, Object>();
		resMap = (Map<String, Object>) JsonUtil.jsonToBean(retJson, Map.class);
		if (0 == (int) (resMap.get("Ret"))) {
			String encodeData = (String) resMap.get("Data");
			String o_data = dataSigCheck.decodeContentData(encodeData);
			System.out.println(o_data);
		} else {
			
		}*/
	}
	// 查询充电统计信息的接口.
	@Test
	public void test_querStationChargeStats() throws Exception {
		String url = "http://124.205.228.170:48080/evcs/20160701/query_station_charge_stats";
		//String url ="http://hlht.telaidian.com.cn:9501/evcs/v20161110/query_station_stats";
		String timeStamp = TimeConvertor.getDate(TimeConvertor.FORMAT_NONSYMBOL_24HOUR);
		String seq = SeqUtil.getUniqueInstance().getSeq();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		//普天的站
		String[] stationIDs = {"CABJ0A008408",
								"CABJ0A012008",
								"CABJ0A012408",
								"CABJ0A013508",
								"CABJ0A028708",
								"CABJ0A036008",
								"CABJ0B011208",
								"CABJ0B011706",
								"CABJ0B011906",
								"CABJ0B013606"};
		//特来电的站
		//String[] stationIDs = {"3702120244"};
		for (String stationID : stationIDs) {
			map.put("StationID", stationID);
			map.put("StartTime", "2019-10-02");
			map.put("EndTime", "2019-10-24");
			String beanToJson = JsonUtil.beanToJson(map);
			System.out.println("\nbeanToJson"+beanToJson);
			String data = dataSigCheck.encodeContentData(beanToJson);
			Map<String, Object> reqMap = new LinkedHashMap<String, Object>();
			reqMap.put("OperatorID", "45581668X");
			reqMap.put("Data", data);
			reqMap.put("TimeStamp", timeStamp);
			reqMap.put("Seq", "0001");
			String msg = "45581668X" + data + timeStamp + "0001";
			System.out.println("\n--msg " + msg);
			String sig = dataSigCheck.genSign(msg);
			reqMap.put("Sig", sig);
			String param = JsonUtil.beanToJson(reqMap);
			System.out.println("param:" + param);
			RestTemplate restTemplate = new RestTemplate();
			
			Dispatcher dispatcher = new Dispatcher(restTemplate);
			String retJson = (String) dispatcher.doPost(token, url, String.class, param);
			
			System.out.println("\nretJson:" + retJson);
			// String retJson = (String)new
			// Dispatcher(restTemplate).doPost(queryUrl,String.class, param);
			String receivedTime = TimeConvertor.getDate(TimeConvertor.FORMAT_MINUS_24HOUR);
			String objectName = "StationStats";
			String result = JsonValidate.chgToStr(jsonValidate.validate(Consts.INTERFACE_VERSIONV1_0, objectName, retJson,null));
			System.out.println("\n校验结果result=" + result);
	//		String JsonStr = JSON.parseObject(result).getString("Data");
	//		JSONArray jsonArray = JSON.parseObject((String) JsonStr).getJSONArray("StationInfos");
	//		List<StationInfo> stationsList = JsonUtil.mkList(jsonArray, StationInfo.class, true);
	//		System.out.println("stationsList length=" + stationsList.size());
	//		int i = 0;
	//		for (StationInfo stationInfo : stationsList) {
	//			stationsInfoMgmt.addQueryStation(stationInfo);
	//			i++;
	//		}
			
			/*Map<String, Object> resMap = new HashMap<String, Object>();
			resMap = (Map<String, Object>) JsonUtil.jsonToBean(retJson, Map.class);
			if (0 == (int) (resMap.get("Ret"))) {
				String encodeData = (String) resMap.get("Data");
				String o_data = dataSigCheck.decodeContentData(encodeData);
				System.out.println(o_data);
			} else {
				
			}*/
		}
	}
	// 查询放电统计信息的接口.
	@Test
	public void test_querStationDisChargeStats() throws Exception {
		//String url = "http://124.205.228.170:48080/evcs/20160701/query_station_discharge_stats";
		String url ="http://hlht.telaidian.com.cn:9501/evcs/v20161110/query_station_discharge_stats";
		String timeStamp = TimeConvertor.getDate(TimeConvertor.FORMAT_NONSYMBOL_24HOUR);
		String seq = SeqUtil.getUniqueInstance().getSeq();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		//普天的站
		String[] stationIDs = {"CABJ0A008408",
								"CABJ0A012008",
								"CABJ0A012408",
								"CABJ0A013508",
								"CABJ0A028708",
								"CABJ0A036008",
								"CABJ0B011208",
								"CABJ0B011706",
								"CABJ0B011906",
								"CABJ0B013606"};
		//特来电的站
		/*String[] stationIDs = {"4403060009",
				"3702120244",
				"4403060029",
				"4403050012",
				"4403040015",
				"4403040013",
				"4403060014",
				"4403030009",
				"4403050008",
		"4403040004"};*/
		/*String[] stationIDs = {"3702120244"};*/
		for (String stationID : stationIDs) {
			map.put("StationID", stationID);
			map.put("StartTime", "2019-09-01");
			map.put("EndTime", "2019-10-01");
			String beanToJson = JsonUtil.beanToJson(map);
			System.out.println("\nbeanToJson"+beanToJson);
			String data = dataSigCheck.encodeContentData(beanToJson);
			Map<String, Object> reqMap = new LinkedHashMap<String, Object>();
			reqMap.put("OperatorID", "45581668X");
			reqMap.put("Data", data);
			reqMap.put("TimeStamp", timeStamp);
			reqMap.put("Seq", "0001");
			String msg = "45581668X" + data + timeStamp + "0001";
			System.out.println("\n--msg " + msg);
			String sig = dataSigCheck.genSign(msg);
			reqMap.put("Sig", sig);
			String param = JsonUtil.beanToJson(reqMap);
			System.out.println("param:" + param);
			RestTemplate restTemplate = new RestTemplate();
			
			Dispatcher dispatcher = new Dispatcher(restTemplate);
			String retJson = (String) dispatcher.doPost(token, url, String.class, param);
			
			System.out.println("\nretJson:" + retJson);
			// String retJson = (String)new
			// Dispatcher(restTemplate).doPost(queryUrl,String.class, param);
			String receivedTime = TimeConvertor.getDate(TimeConvertor.FORMAT_MINUS_24HOUR);
			String objectName = "StationStats";
			//		String result = JsonValidate.chgToStr(jsonValidate.validate(Consts.INTERFACE_VERSIONV1, objectName, retJson));
			//		System.out.println("\n校验结果result=" + result);
			//		String JsonStr = JSON.parseObject(result).getString("Data");
			//		JSONArray jsonArray = JSON.parseObject((String) JsonStr).getJSONArray("StationInfos");
			//		List<StationInfo> stationsList = JsonUtil.mkList(jsonArray, StationInfo.class, true);
			//		System.out.println("stationsList length=" + stationsList.size());
			//		int i = 0;
			//		for (StationInfo stationInfo : stationsList) {
			//			stationsInfoMgmt.addQueryStation(stationInfo);
			//			i++;
			//		}
			
			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap = (Map<String, Object>) JsonUtil.jsonToBean(retJson, Map.class);
			if (0 == (int) (resMap.get("Ret"))) {
				String encodeData = (String) resMap.get("Data");
				String o_data = dataSigCheck.decodeContentData(encodeData);
				System.out.println(o_data);
			} else {
				
			}
		}
	}
	
	
	@Test
	public void testSig() {
		String s ="45581668X3cYGcsYXezutkwUCXxOgo4F8WL1gNJNluWr0DJzyh0Xs3JLJTG+w6rP8jMmmD/kVs9wUBWpX0wpPZFzNd8iMRA==201910101436400001";
		String sig = dataSigCheck.genSign(s);
		System.out.println(sig.equals("E67BCFE520D99AB134324AEF5AFCD050"));
		
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
			System.out.println(data);
			
			//String s ="{\"total\":1,\"stationStatusInfo\":{\"operationID\":\"123456789\",\"stationID\":\"111111111111111\",\"connectorStatusInfos\":{\"connectorID\":1,\"equipmentID\":\"10000000000000000000001\",\"status\":4,\"currentA\":0,\"currentB\":0,\"currentC\":0,\"voltageA\":0,\"voltageB\":0,\"voltageC\":0,\"soc\":10,}}}";
			String en = dataSigCheck.encodeContentData(data);
			
			System.out.println("+++ "+en);
			            //il7B0BSEjFdzpyKzfOFpvg/Se1CP802RItKYFPfSLRxJ3jf0bVl9hvYOEktPAYW2nd7S8MBcyHYyacHKbISq5iTmDzG+ivnR+SZJv3USNTYVMz9rCQVSxd0cLlqsJauko79NnwQJbzDTyLooYoIwz75qBOH2/xOMirpeEqRJrF/EQjWekJmGk9RtboXePu2rka+Xm51syBPhiXJAq0GfbfaFu9tNqs/e2Vjja/ltE1M0lqvxfXQ6da6HrThsm5id4ClZFIi0acRfrsPLRixS/IQYtksxghvJwbqOsbIsITail9Ayy4tKcogeEZiOO+4Ed264NSKmk7l3wKwJLAFjCFogBx8GE3OBz4pqcAn/ydA=
			String ss ="il7B0BSEjFdzpyKzfOFpvg/Se1CP802RItKYFPfSLRxJ3jf0bVl9hvYOEktPAYW2nd7S8MBcyHYyacHKbISq5iTmDzG+ivnR+SZJv3USNTYVMz9rCQVSxd0cLlqsJauko79NnwQJbzDTyLooYoIwz75qBOH2/xOMirpeEqRJrF/EQjWekJmGk9RtboXePu2rka+Xm51syBPhiXJAq0GfbfaFu9tNqs/e2Vjja/ltE1M0lqvxfXQ6da6HrThsm5id4ClZFIi0acRfrsPLRixS/IQYtksxghvJwbqOsbIsITail9Ayy4tKcogeEZiOO+4Ed264NSKmk7l3wKwJLAFjCFogBx8GE3OBz4pqcAn/ydA=";
			//System.out.println("+++ "+en.equals(ss));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
  }
}
