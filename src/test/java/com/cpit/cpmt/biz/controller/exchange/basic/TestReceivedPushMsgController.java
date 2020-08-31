package com.cpit.cpmt.biz.controller.exchange.basic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONObject;
import com.cpit.common.JsonUtil;
import com.cpit.cpmt.biz.impl.exchange.basic.UnionStoreMgmt;
import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfo;
import com.cpit.cpmt.dto.exchange.basic.BasicReportMsgInfo;
import com.cpit.cpmt.dto.exchange.basic.BmsInfo;
import com.cpit.cpmt.dto.exchange.basic.DisEquipmentStatusInfo;
import com.cpit.cpmt.dto.exchange.basic.EventInfo;
//---scx
@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.DEFINED_PORT)
public class TestReceivedPushMsgController {
	private final static Logger logger = LoggerFactory.getLogger(TestReceivedPushMsgController.class);
	@Autowired
	private TestRestTemplate restTemplate;
	
	//@Autowired
	//private ServiceMgmt serviceMgmt;
	
	@Autowired
	private UnionStoreMgmt unionStoreMgmt;
	
	@Value("http://localhost:28060/exchange/collect/")
	private String url;
	
	//事件信息推送的接口(充电运营商充电站事件信息发生变化时，主动上报市级平台)
	@Test
	public void notificationEventInfo() {
		try {
			String urls = url  + "notification_eventInfo";
			List<EventInfo> list = new ArrayList<EventInfo>();
			for (int i = 0; i < 2; i++) {
				EventInfo eventInfo = new EventInfo();
				eventInfo.setStationID("111111111111"+i);
				eventInfo.setOperatorID("987654321");
				eventInfo.setEquipmentOwnerID("987654321");
				eventInfo.setStationName("某某充电站");
				eventInfo.setEventType("3");
				eventInfo.setEventCode("3001");
				eventInfo.setEventTime(new Date());
				eventInfo.setNoteString("注意内容：**停车场附近");
				list.add(eventInfo);
			}
			JSONObject data = new JSONObject();
			data.put("EventInfos", list);
			JSONObject jsonAll = new JSONObject();
			jsonAll.put("Data", data);
			jsonAll.put("OperatorID", "abc");
			jsonAll.put("TimeStamp", "20180101120000");
			jsonAll.put("Seq", "0001");
			jsonAll.put("Sig", "xyz");
			String jsonAllStr =JsonUtil.beanToJson(jsonAll, true);
			logger.info("\n打印组装参数:"+jsonAllStr);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> json = new HttpEntity<String>(jsonAllStr, headers);
			ResponseEntity<String> postForEntity = this.restTemplate.postForEntity(urls, json, String.class, "");
			String body = postForEntity.getBody();
			logger.info("\n测试类中打印返回结果:"+body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void notificationEventInfoServices() {
		try {
			String urls = url  + "notification_eventInfo";
			List<EventInfo> list = new ArrayList<EventInfo>();
			for (int i = 0; i < 2; i++) {
				EventInfo eventInfo = new EventInfo();
				eventInfo.setStationID("1111111111111"+i);
				eventInfo.setOperatorID("9876543210");
				eventInfo.setEquipmentOwnerID("987654321");
				eventInfo.setStationName("某某充电站");
				eventInfo.setEventType("3");
				eventInfo.setEventCode("3001");
				eventInfo.setEventTime(new Date());
				eventInfo.setNoteString("注意内容：**停车场附近");
				list.add(eventInfo);
			}
			JSONObject data = new JSONObject();
			data.put("EventInfos", list);
			String jsonData = JsonUtil.beanToJson(data, true);//字段中的小写首字母在json中变成了大写
			BasicReportMsgInfo reportMsg = new BasicReportMsgInfo();
			reportMsg.setJsonMsg(jsonData);
			reportMsg.setInfType("5");
			reportMsg.setRecTime(new Date());
			reportMsg.setValidateResult("1");
			logger.info("\n打印组装参数:"+reportMsg.getJsonMsg());
			unionStoreMgmt.storeDB(reportMsg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//告警信息推送的接口(充电运营商平台有设备告警事件发生时，主动上报市级平台)
	@Test
	public void notification_alarmInfo() {
		try {
			String urls = url  + "notification_alarmInfo";
			List<AlarmInfo> list = new ArrayList<AlarmInfo>();
			for (int i = 0; i < 2; i++) {
				AlarmInfo alarmInfo = new AlarmInfo();
				alarmInfo.setStationID("11111111111111"+i);
				alarmInfo.setOperatorID("987654321");
				alarmInfo.setEquipmentOwnerID("987654321");
				alarmInfo.setStationName("某某充电站");
				alarmInfo.setEquipmentID("10000000000000000000003");
				alarmInfo.setConnectorID("10000000000000000000000101");
				alarmInfo.setAlarmStatus("1");
				alarmInfo.setAlarmCode("101");
				alarmInfo.setAlarmType("2");
				alarmInfo.setAlarmLevel("2");
				alarmInfo.setAlarmTime(new Date());
				alarmInfo.setNoteString("注意内容：**停车场附近");
				BmsInfo bmsInfo = new BmsInfo();
				bmsInfo.setBMSCode("102");
				bmsInfo.setBMSVer("23");
				bmsInfo.setMaxChargeCurrent("60");
				bmsInfo.setMaxChargeCellVoltage("220");
				bmsInfo.setMaxTemp("55");
				bmsInfo.setRatedCapacity("60");
				bmsInfo.setTatalVoltage("380");
				bmsInfo.setTotalCurrent("60");
				bmsInfo.setSoc("90");
				bmsInfo.setVoltageH("4.20");
				bmsInfo.setVoltageL("3.70");
				bmsInfo.setTemptureH("55");
				bmsInfo.setTemptureL("45");
				alarmInfo.setBmsInfo(bmsInfo);
				list.add(alarmInfo);
			}
			JSONObject data = new JSONObject();
			data.put("AlarmInfos", list);
			JSONObject jsonAll = new JSONObject();
			jsonAll.put("Data", data);
			jsonAll.put("OperatorID", "abc");
			jsonAll.put("TimeStamp", "20180101120000");
			jsonAll.put("Seq", "0001");
			jsonAll.put("Sig", "xyz");
			String jsonAllStr =JsonUtil.beanToJson(jsonAll, true);
			logger.info("\n打印组装参数:"+jsonAllStr);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> json = new HttpEntity<String>(jsonAllStr, headers);
			ResponseEntity<String> postForEntity = this.restTemplate.postForEntity(urls, json, String.class, "");
			String body = postForEntity.getBody();
			logger.info("\n打印返回结果:"+body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		//告警信息推送的业务层调用(充电运营商平台有设备告警事件发生时，主动上报市级平台)
		@Test
		public void notification_alarmInfoServices() {
			try {
				String urls = url  + "notification_alarmInfo";
				List<AlarmInfo> list = new ArrayList<AlarmInfo>();
				for (int i = 0; i < 2; i++) {
					AlarmInfo alarmInfo = new AlarmInfo();
					alarmInfo.setStationID("11111111111111"+i);
					alarmInfo.setOperatorID("9876543;21");
					alarmInfo.setEquipmentOwnerID("987654321");
					alarmInfo.setStationName("某某充电站");
					alarmInfo.setEquipmentID("10000000000000000000003");
					alarmInfo.setConnectorID("10000000000000000000000101");
					alarmInfo.setAlarmStatus("1");
					alarmInfo.setAlarmCode("101");
					alarmInfo.setAlarmType("2");
					alarmInfo.setAlarmLevel("2");
					alarmInfo.setAlarmTime(new Date());
					alarmInfo.setNoteString("注意内容：**停车场附近");
					BmsInfo BmsInfo = new BmsInfo();
					BmsInfo.setConnectorID("10000000000000000000000101");
					BmsInfo.setBMSCode("102");
					BmsInfo.setBMSVer("23");
					BmsInfo.setMaxChargeCurrent("60");
					BmsInfo.setMaxChargeCellVoltage("220");
					BmsInfo.setMaxTemp("55");
					BmsInfo.setRatedCapacity("60");
					BmsInfo.setTatalVoltage("380");
					BmsInfo.setTotalCurrent("60");
					BmsInfo.setSoc("90");
					BmsInfo.setVoltageH("4.20");
					BmsInfo.setVoltageL("3.70");
					BmsInfo.setTemptureH("55");
					BmsInfo.setTemptureL("45");
					alarmInfo.setBmsInfo(BmsInfo);
					list.add(alarmInfo);
				}
				JSONObject data = new JSONObject();
				data.put("AlarmInfos", list);
				String jsonData =JsonUtil.beanToJson(data, true);
				BasicReportMsgInfo reportMsg = new BasicReportMsgInfo();
				reportMsg.setJsonMsg(jsonData);
				reportMsg.setInfType("4");
				reportMsg.setRecTime(new Date());
				logger.info("\n打印组装参数:"+reportMsg.getJsonMsg());
				unionStoreMgmt.storeDB(reportMsg);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	/*
	 * 配电信息查询接口测试入库
	 */
	@Test
	public void saveDisEquipmentStatusInfos() {
		List<DisEquipmentStatusInfo> desiInfoList = new ArrayList<DisEquipmentStatusInfo>();
		for (int i = 0; i < 2; i++) {
			DisEquipmentStatusInfo desiInfo = new DisEquipmentStatusInfo();
			desiInfo.setStationID("11111111111111"+i);
			desiInfo.setOperatorID("987654321");
			desiInfo.setDisequipmentID("987654321");
			desiInfo.setFoPEnergy((double)60);
			desiInfo.setRePEnergy((double)220);
			desiInfo.setFoQEnergy((double)220);
			desiInfo.setReQEnergy((double)220);
			desiInfo.setAVoltage((double)380);
			desiInfo.setBVoltage((double)60);
			desiInfo.setCVoltage((double)90);
			desiInfo.setACurrent(4.20);
			desiInfo.setBCurrent(3.70);
			desiInfo.setCCurrent((double)55);
			desiInfo.setCurPPower ((double)45);
			desiInfo.setCurQPower ((double)45);
			desiInfo.setFactor((double)45);
			desiInfo.setCurSPower((double)45);
			desiInfo.setCurPPower((double)45);
			desiInfo.setFrequency((double)45);
			desiInfo.setFeedState("45");
			desiInfo.setEnvTemp1(0);
			desiInfo.setEnvTemp2(0);
			desiInfo.setEnvHumi1(0);
			desiInfo.setEnvHumi2(0);
			desiInfo.setSmokAlam1(0);
			desiInfo.setSmokAlam2(0);
			desiInfo.setTranOverTemAlam(0);
			desiInfo.setReceivedTime(new Date());
			desiInfo.setInTime(new Date());
			desiInfoList.add(desiInfo);
		}
		//serviceMgmt.saveDisEquipmentStatusInfos(desiInfoList);
	}
}
