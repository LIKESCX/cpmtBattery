package com.cpit.cpmt.biz.impl.exchange.basic;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.cpit.cpmt.biz.dao.exchange.basic.AlarmInfoDao;
import com.cpit.cpmt.biz.dao.exchange.basic.BmsInfoDao;
import com.cpit.cpmt.biz.dao.exchange.basic.EventInfoDao;
import com.cpit.cpmt.biz.utils.validate.Protocol2Parse;
import com.cpit.cpmt.biz.utils.validate.ReturnCode;
import com.cpit.cpmt.biz.utils.validate.Util;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfo;
import com.cpit.cpmt.dto.exchange.basic.BmsInfo;
import com.cpit.cpmt.dto.exchange.basic.EventInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ReceivedPushMsgMgmt {
	private final static Logger logger = LoggerFactory.getLogger(ReceivedPushMsgMgmt.class);
	public final static String VERSION = "V1.0";
	@Autowired
	EventInfoDao eventInfoDao;
	
	@Autowired
	AlarmInfoDao alarmInfoDao;
	
	@Autowired
	BmsInfoDao bmsInfoDao;
	
	
	/*
	 * 插入事件信息推送
	 */
	@Transactional
	public Map<String,Object> insertNotificationEventInfo(ReturnCode result, String getJsonEventInfos, Date receivedTime) {
		//1.分析json校验结果
		Map<String,Object> map = new HashMap<String,Object>();
		
		if(result == null){
			logger.error("===validate get null");
			map.put("Ret", ReturnCode.CODE_500);
			map.put("Msg", ReturnCode.MSG_500+"0");
			return map;
		}		
		
		if(result.getCode() != ReturnCode.CODE_OK){
			map.put("Ret", result.getCode());
			map.put("Msg", result.getErrorMsg());
			return map;
		}else{
			List<EventInfo> eventInfoList = JSON.parseArray(getJsonEventInfos, EventInfo.class);
			//循环遍历插入数据库
			for (EventInfo eventInfo : eventInfoList) {
				eventInfo.setReceivedTime(receivedTime);//中心平台接收时间
				eventInfo.setInTime(new Date());//入库时间
				eventInfoDao.insertSelective(eventInfo);
			}
			map.put("value","成功");
			return map;
		}

	}
	
	/*
	 * 插入告警事件信息推送
	 */
	@Transactional
	public Map<String,Object> insertNotificationAlarmInfo(ReturnCode result,String getJsonAlarmInfos,Date receivedTime) {
		//1.分析json校验结果
		Map<String,Object> map = new HashMap<String,Object>();
		
		if(result == null){
			logger.error("===validate get null");
			map.put("Ret", ReturnCode.CODE_500);
			map.put("Msg", ReturnCode.MSG_500+"0");
			return map;
		}		
		
		if(result.getCode() != ReturnCode.CODE_OK){
			map.put("Ret", result.getCode());
			map.put("Msg", result.getErrorMsg());
			return map;
		}else{
			List<AlarmInfo> alarmInfoList = JSON.parseArray(getJsonAlarmInfos, AlarmInfo.class);
			//循环遍历插入数据库
			for (AlarmInfo alarmInfo : alarmInfoList) {
				alarmInfo.setReceivedTime(receivedTime);//中心平台接收时间
				alarmInfo.setInTime(new Date());//入库时间
				alarmInfoDao.insertSelective(alarmInfo);
				//获取设备接口编码
				//String connectorId = alarmInfo.getConnectorId();
				BmsInfo bmsInfo = alarmInfo.getBmsInfo();
				//bmsInfo.setConnectorId(connectorId);
				bmsInfo.setReceivedTime(receivedTime);//平台收到时间
				bmsInfo.setInTime(new Date());//入库时间
				bmsInfo.setOperatorID(alarmInfo.getOperatorID());
				bmsInfo.setStationID(alarmInfo.getStationID());
				bmsInfo.setEquipmentID(alarmInfo.getEquipmentID());
				bmsInfo.setConnectorID(alarmInfo.getConnectorID());
			//没有用到
				//	bmsInfoDao.insertSelective(bmsInfo);
			}
			map.put("value","成功");
			return map;
		}
		
	}
	
	public String queryEventInfo(String stationID, String operatorID, String startTime, String endTime) {
		//根据operatorID获取域名 格式:http(s)://[域名]/shevcs/v[版本号]/[接口名称]。
		//String url = "http(s)://[域名]/shevcs/v[版本号]/[接口名称]?StationID="+stationID+"&operatorID="+operatorID+",startTime="+startTime+",endTime="+endTime;
		//String content = (String)new Dispatcher(restTemplate).doPost(url, String.class, "");
		//校验返回值字段类型
		String content = Util.ReadFile("json/Response/"+VERSION+"/query_event_info.json");
		String objectName = "EventInfos";
		return chgToStr(validate(VERSION, objectName, content));
	}
	
	//==========================private methods
	
	private Object validate( String version, String objectName, String json){
		ReturnCode result = null;
		try{
			result = Protocol2Parse.validate2Parameter(version, objectName, json);
		}catch(Exception ex){
			logger.error("error in validateParameter",ex);
		}

		Map<String,Object> map = new HashMap<String,Object>();
		
		if(result == null){
			logger.error("===validate get null");
			map.put("Ret", ReturnCode.CODE_500);
			map.put("Msg", ReturnCode.MSG_500+"0");
			return map;
		}		
		if(result.getCode() != ReturnCode.CODE_OK){
			map.put("Ret", result.getCode());
			map.put("Msg", result.getErrorMsg());
			return map;
		}else{
			//String value = operatorMgmt.addStationInfoCollect(json);
			String value = null;
			Map dataMap = new HashMap<String, Integer>();
			/////根据地标规范需要修改返回值
			if(value.indexOf("成功")>0){
				map.put("Ret", ReturnCode.CODE_OK);
				map.put("Msg", ReturnCode.MSG_OK);
				dataMap.put("Status", 0);
				map.put("Data", dataMap);
			}
			else{
				map.put("Ret", ReturnCode.CODE_500);
				map.put("Msg", ReturnCode.MSG_500+"1");
			}
			return map;
		}
		
	}
	public static String chgToStr(Object obj){
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			return "";
		}		
	}
}
