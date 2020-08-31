package com.cpit.cpmt.biz.impl.exchange.basic;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cpit.common.JsonUtil;
import com.cpit.cpmt.dto.exchange.basic.ConnectorInfo;
import com.cpit.cpmt.dto.exchange.basic.EquipmentInfo;
import com.cpit.cpmt.dto.exchange.basic.StationInfo;

@Service
public class TestStationsInfoMgmt {
	private final static Logger logger = LoggerFactory.getLogger(TestStationsInfoMgmt.class);
	public String queryStationsInfo() throws Exception {
		List<StationInfo> list = new ArrayList<StationInfo>();
		for (int i = 0; i < 2; i++) {
			StationInfo stationInfo = new StationInfo();
			stationInfo.setOperatorID("123456789");
			stationInfo.setStationID("000000000000001");
			stationInfo.setStationName("北京某某充电站");
			stationInfo.setEquipmentOwnerID("123456789");
			stationInfo.setCountryCode("CN");
			stationInfo.setAreaCode("441781");
			stationInfo.setAddress("北京市海淀区北二街6号地下停车场");
			stationInfo.setServiceTel("123456789");
//			stationInfo.setStationType("1");
//			stationInfo.setStationStatus("50");
//			stationInfo.setParkNums("3");
//			stationInfo.setStationLng("119.97049");
//			stationInfo.setStationLat("31.717877");
//			stationInfo.setOpenAllDay("1");
//			stationInfo.setConstruction(0);
//			stationInfo.setParkFree("1");
			String[] pictures = new String[]{"http://www.xxx.com/uploads/image/isbiLogo_1.jpg","http://www.xxx.com/uploads/image/isbiLogo_2.jpg"};
			stationInfo.setPictures(pictures);
			List<EquipmentInfo> elist = new ArrayList<EquipmentInfo>();
			for (int j = 0; j < 2; j++) {
				EquipmentInfo equipmentInfo = new EquipmentInfo();
				equipmentInfo.setEquipmentID("10000000000000000000003");
				equipmentInfo.setEquipmentName("电桩001");
				equipmentInfo.setManufacturerID("123456789");
				equipmentInfo.setEquipmentModel("p3");
//				equipmentInfo.setProductionDate("2016-04-26");
//				equipmentInfo.setEquipmentType(3);
//				equipmentInfo.setPower(60.00);
//				equipmentInfo.setEquipmentStatus("50");
//				equipmentInfo.setEquipmentPower("3.3");
//				equipmentInfo.setNewNationalStandard("1");
				List<ConnectorInfo> clist = new ArrayList<ConnectorInfo>();
				for (int k = 0; k < 2; k++) {
					ConnectorInfo connectorInfo = new ConnectorInfo();
					connectorInfo.setConnectorID("1");
					connectorInfo.setConnectorName("枪1");
					connectorInfo.setConnectorType(1);
					connectorInfo.setVoltageUpperLimits(220);
					connectorInfo.setVoltageLowerLimits(220);
					connectorInfo.setCurrent(15);
					connectorInfo.setPower(3.3);
					connectorInfo.setVoltage(220);
					connectorInfo.setBMSPowerType(1);
					clist.add(connectorInfo);
				}
				equipmentInfo.setConnectorInfos(clist);
				elist.add(equipmentInfo);
			}
			stationInfo.setEquipmentInfos(elist);
			list.add(stationInfo);
		}		
		JSONObject data = new JSONObject();
		data.put("StationsInfos", list);
		JSONObject jsonAll = new JSONObject();
		jsonAll.put("Data", data);
		jsonAll.put("OperatorID", "abc");
		jsonAll.put("TimeStamp", "20180101120000");
		jsonAll.put("Seq", "0001");
		jsonAll.put("Sig", "xyz");
		jsonAll.put("ItemSize", list.size());
		jsonAll.put("PageCount", 1);
		jsonAll.put("PageNo", 1);
		String jsonAllStr =JsonUtil.beanToJson(jsonAll, true);
		logger.info("\n打印组装参数:"+jsonAllStr);
		return jsonAllStr;
	}

}
