package com.cpit.cpmt.biz.impl.exchange.basic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cpit.common.JsonUtil;
import com.cpit.cpmt.dto.exchange.basic.DisEquipmentStatusInfo;

@Service
public class TestDisEquipmentStatusInfoMgmt {
	private final static Logger logger = LoggerFactory.getLogger(TestDisEquipmentStatusInfoMgmt.class);
	public String queryDisEquipmentStatusInfos() throws Exception {
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
			desiInfo.setFeedState("1");
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
		JSONObject data = new JSONObject();
		data.put("DisEquipmentStatusInfos", desiInfoList);
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
}
