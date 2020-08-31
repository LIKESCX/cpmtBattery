package com.cpit.cpmt.biz.controller.exchange.basic;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.impl.exchange.basic.ReceivedPushMsgMgmt;
import com.cpit.cpmt.biz.utils.validate.ReturnCode;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value="/biz")
public class ReceivedPushMsgController {
	private final static Logger logger = LoggerFactory.getLogger(ReceivedPushMsgController.class);
	public final static String VERSION = "V1.0";
	
	@Autowired
	ReceivedPushMsgMgmt receivedPushMsgMgmt;
	//1
//	@ApiOperation(value = "事件信息推送的接口(充电运营商充电站事件信息发生变化时，主动上报市级平台)")
//	@RequestMapping("/notification_eventInfo")
//	public Object notificationEventInfo(HttpServletRequest request, @RequestBody String content){
//		//中心平台收到推送消息的时间
//		String receivedTime = TimeConvertor.getDate(TimeConvertor.FORMAT_MINUS_24HOUR);
//		logger.info("notification_eventInfo request receivedTime is:\n "+receivedTime);
//		//打印接受到的推送消息
//		logger.info("notification_eventInfo request is:\n "+content);
//		Map<String, Integer> dataMap = new HashMap<String, Integer>();
//		try {
//			//1.校验推送信息
//			String objectName="EventInfo";
//			String getJsonEventInfos = JSON.parseObject(content).getString("EventInfos");
//			ReturnCode result = JsonValidate.jsonValidate(VERSION, objectName, getJsonEventInfos);
//			//3.分析校验结果及插入数据库
//			Map<String,Object>  map = receivedPushMsgMgmt.insertNotificationEventInfo(result,getJsonEventInfos,receivedTime);
//			logger.info("notification_eventInfo end is:\n "+map);
//			/////根据地标规范需要修改返回值
//			if(map.get("value")!=null&&map.get("value")!="") {
//				String value = (String) map.get("value");
//				if(value.indexOf("成功")!=-1){
//					//map.put("Ret", ReturnCode.CODE_OK);
//					//map.put("Msg", ReturnCode.MSG_OK);
//					dataMap.put("Status", 0);
//					//map.put("Data", dataMap);
//				}else{
//					dataMap.put("Status", 1);
//					//map.put("Ret", ReturnCode.CODE_500);
//					//map.put("Msg", ReturnCode.MSG_500+"1");
//				}
//			}else {
//				dataMap.put("Status", 1);
//			}
//			return dataMap;
//		} catch (Exception e) {
//			logger.error("e:"+e.getLocalizedMessage());
//			dataMap.put("Status", 1);
//			return dataMap;
//		}
//	}
	
	//2
//	@ApiOperation(value = "告警信息推送的接口(充电运营商平台有设备告警事件发生时，主动上报市级平台)")
//	@RequestMapping("/notification_alarmInfo")
//	public Object notification_alarmInfo(HttpServletRequest request, @RequestBody String content){
//		//中心平台收到推送消息的时间
//		String receivedTime = TimeConvertor.getDate(TimeConvertor.FORMAT_MINUS_24HOUR);
//		logger.info("notification_alarmInfo request receivedTime is:\n "+receivedTime);
//		//打印接受到的推送消息
//		logger.info("notification_alarmInfo request is:\n "+content);
//		Map<String, Integer> dataMap = new HashMap<String, Integer>();
//		try {
//			//1.校验推送信息
//			String objectName="AlarmInfo";
//			String getJsonAlarmInfos = JSON.parseObject(content).getString("AlarmInfos");
//			ReturnCode result = JsonValidate.jsonValidate(VERSION, objectName, getJsonAlarmInfos);
//			//3.分析校验结果及插入数据库
//			Map<String,Object>  map = receivedPushMsgMgmt.insertNotificationAlarmInfo(result,getJsonAlarmInfos,receivedTime);
//			logger.info("notification_alarmInfo end is:\n "+map);
//			//System.out.println("map.get(\"value\")"+map.get("value"));
//			/////根据地标规范需要修改返回值
//			if(map.get("value")!=null&&map.get("value")!="") {
//				String value = (String) map.get("value");
//				if(value.indexOf("成功")!=-1){
//					//map.put("Ret", ReturnCode.CODE_OK);
//					//map.put("Msg", ReturnCode.MSG_OK);
//					dataMap.put("Status", 0);
//					//map.put("Data", dataMap);
//				}else{
//					dataMap.put("Status", 1);
//					//map.put("Ret", ReturnCode.CODE_500);
//					//map.put("Msg", ReturnCode.MSG_500+"1");
//				}
//			}else {
//				dataMap.put("Status", 1);
//			}
//			return dataMap;
//		} catch (Exception e) {
//			logger.error("e:"+e.getLocalizedMessage());
//			dataMap.put("Status", 1);
//			return dataMap;
//		}
//	}
	//3
	/**
	 * 
	 * @param StationID  type="String"
	 * @param OperatorID type="String"
	 * @param StartTime  type="String" format="yyyy-MM-dd"
	 * @param EndTime    type="String" format="yyyy-MM-dd"
	 * @return
	 */
	@ApiOperation(value = "市级平台可查询某一时间范围内的运营商充电站事件信息)")
	@RequestMapping("/query_event_info")
	public Object queryEventInfo(String StationID,String OperatorID,String StartTime,String EndTime) {
		logger.info("query_event_info begin param is:\n StationID="+StationID+"\n OperatorID="+OperatorID+",\n StartTime="+StartTime+",\n EndTime="+EndTime);
		try {
			receivedPushMsgMgmt.queryEventInfo(StationID,OperatorID,StartTime,EndTime);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
}
