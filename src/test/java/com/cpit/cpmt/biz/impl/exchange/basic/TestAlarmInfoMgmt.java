package com.cpit.cpmt.biz.impl.exchange.basic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONObject;
import com.cpit.common.JsonUtil;
import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfo;
import com.cpit.cpmt.dto.exchange.basic.BmsInfo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.DEFINED_PORT)
public class TestAlarmInfoMgmt {
	private final static Logger logger = LoggerFactory.getLogger(TestAlarmInfoMgmt.class);
	public String queryAlarmInfo() throws Exception {
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
		JSONObject jsonAll = new JSONObject();
		jsonAll.put("Data", data);
		jsonAll.put("OperatorID", "abc");
		jsonAll.put("TimeStamp", "20180101120000");
		jsonAll.put("Seq", "0001");
		jsonAll.put("Sig", "xyz");
		String jsonAllStr =JsonUtil.beanToJson(jsonAll, true);
		logger.info("\n打印组装参数:"+jsonAllStr);
		return jsonAllStr;
	}
	
	@Autowired AlarmInfoMgmt alarmInfoMgmt;
	@Test
	public void addAlarmInfo() throws Exception{
			AlarmInfo alarmInfo = new AlarmInfo();
			alarmInfo.setStationID("440202003");
			alarmInfo.setOperatorID("061402628");
			alarmInfo.setEquipmentOwnerID("987654321");
			alarmInfo.setStationName("奥特迅后广场充电站");
			alarmInfo.setEquipmentID("4402020030000009");
			alarmInfo.setConnectorID("4402020030000009001");
			alarmInfo.setAlarmStatus("1");
			alarmInfo.setAlarmCode("13");
			alarmInfo.setAlarmType("1");
			alarmInfo.setAlarmLevel("2");
			alarmInfo.setAlarmTime(new Date());
			alarmInfo.setNoteString("烟雾报警告警");
			
			BmsInfo BmsInfo = new BmsInfo();
			BmsInfo.setConnectorID("4402020030000009001");
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
			alarmInfoMgmt.addAlarmInfo(alarmInfo, false);
			
	}

}
