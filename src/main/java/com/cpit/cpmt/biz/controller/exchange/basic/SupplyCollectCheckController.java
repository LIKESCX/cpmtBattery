package com.cpit.cpmt.biz.controller.exchange.basic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.cpit.common.JsonUtil;
import com.cpit.cpmt.biz.impl.exchange.basic.SupplyCollectCheckMgmt;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfo;
import com.cpit.cpmt.dto.exchange.basic.BmsInfo;
import com.cpit.cpmt.dto.exchange.basic.ConnectorInfo;
import com.cpit.cpmt.dto.exchange.basic.ConnectorStatusInfo;
import com.cpit.cpmt.dto.exchange.basic.EquipmentInfo;
import com.cpit.cpmt.dto.exchange.basic.EventInfo;
import com.cpit.cpmt.dto.exchange.basic.StationInfo;
import com.cpit.cpmt.dto.exchange.basic.StationStatusInfo;

@RestController
public class SupplyCollectCheckController {
	private final static Logger logger = LoggerFactory.getLogger(SupplyCollectCheckController.class);

	@Autowired SupplyCollectCheckMgmt supplyCollectCheckMgmt;
	@RequestMapping("/supplement_stationStatus")
	public String supplyCollectCheck1() throws Exception {
		Map<String,Object> map = new HashMap<String,Object>();
		StationStatusInfo stationStatusInfo = new StationStatusInfo();
		stationStatusInfo.setStationID("123847859098");
		List<ConnectorStatusInfo> cSInfo = new ArrayList<ConnectorStatusInfo>();
		List<StationStatusInfo> sSInfo = new ArrayList<StationStatusInfo>();
		
		ConnectorStatusInfo connectorStatusInfo = new ConnectorStatusInfo();
		connectorStatusInfo.setOperatorID("1212121");
		connectorStatusInfo.setConnectorID("232200");
		connectorStatusInfo.setStatus("3");
		connectorStatusInfo.setParkStatus("10");
		connectorStatusInfo.setLockStatus("50");
		connectorStatusInfo.setParkStatus("10");
		connectorStatusInfo.setLockStatus("10");
		connectorStatusInfo.setCurrentA("10");
		connectorStatusInfo.setCurrentB("10");
		connectorStatusInfo.setCurrentC("10");
		connectorStatusInfo.setVoltageA("200");
		connectorStatusInfo.setVoltageB("10");
		connectorStatusInfo.setVoltageC("10");
		connectorStatusInfo.setSOC("10");
		connectorStatusInfo.setChargeElectricity(10.15);
		connectorStatusInfo.setDischargeElectricity(4.35);
		cSInfo.add(connectorStatusInfo);
		stationStatusInfo.setConnectorStatusInfos(cSInfo);
		sSInfo.add(stationStatusInfo);
		//map.put("StationStatusInfos", sSInfo);
		String json = JsonUtil.beanToJson(sSInfo, true);
		map.put("StationStatusInfos", JSONArray.parseArray(json));
		String json3 = JsonUtil.beanToJson(map, true);
		map.put("SupplyConnectorStatusInfo", json3);
		map.put("InterfaceName", "supplement_stationStatus");
		String result = supplyCollectCheckMgmt.supplyCollectCheck(map);
		return result;
	}
	@RequestMapping("/supplement_bmsInfo")
	public String supplyCollectCheck2() throws Exception {
		Map<String,Object> map = new HashMap<String,Object>();
		BmsInfo bmsInfo = new BmsInfo();
		bmsInfo.setBMSCode("102");
		bmsInfo.setBMSVer("23");
		bmsInfo.setMaxChargeCurrent("100.00");
		bmsInfo.setMaxChargeCellVoltage("220.00");
		bmsInfo.setMaxTemp("55");
		bmsInfo.setSoc("");
		bmsInfo.setRatedCapacity("60");
		bmsInfo.setTatalVoltage("380");
		bmsInfo.setTotalCurrent("60");
		bmsInfo.setVoltageH("4.20");
		bmsInfo.setVoltageL("3.70");
		bmsInfo.setTemptureH("55.00");
		bmsInfo.setTemptureL("0.0");
		
		map.put("ConnectorID", "111111111111111");
		map.put("Status", "3");
		map.put("BmsInfo", bmsInfo);
		String json = JsonUtil.beanToJson(map, true);
		map.put("SupplyBmsInfo", json);
		map.put("InterfaceName", "supplement_bmsInfo");
		//System.out.println(json);
		String result = supplyCollectCheckMgmt.supplyCollectCheck(map);
		logger.debug("result[{}]",result);
		return result;
	}
	
	@RequestMapping("/supplement_alarmInfo")
	public String supplyCollectCheck3() throws Exception {
		Map<String,Object> map = new HashMap<String,Object>();
		AlarmInfo alarmInfo = new AlarmInfo();
		alarmInfo.setStationID("10000000000");
		alarmInfo.setOperatorID("987654321");
		alarmInfo.setEquipmentOwnerID("987654321");
		alarmInfo.setStationName("某某充电站");
		alarmInfo.setEquipmentID("100000000");
		alarmInfo.setConnectorID("100000000");
		alarmInfo.setAlarmStatus("1");
		alarmInfo.setAlarmCode("101");
		alarmInfo.setAlarmType("2");
		alarmInfo.setAlarmLevel("2");
		alarmInfo.setAlarmTime(new Date());
		alarmInfo.setNoteString("注意内容：**停车场附近");
		
		BmsInfo bmsInfo = new BmsInfo();
		bmsInfo.setConnectorID("100000000");
		bmsInfo.setBMSCode("102");
		bmsInfo.setBMSVer("23");
		bmsInfo.setMaxChargeCurrent("60.01");
		bmsInfo.setMaxChargeCellVoltage("220.00");
		bmsInfo.setMaxTemp("55");
		bmsInfo.setRatedCapacity("60");
		bmsInfo.setTatalVoltage("380");
		bmsInfo.setTotalCurrent("60");
		bmsInfo.setSoc("1");
		bmsInfo.setVoltageH("4.20");
		bmsInfo.setVoltageL("3.70");
		bmsInfo.setTemptureH("55");
		bmsInfo.setTemptureL("45");
		alarmInfo.setBmsInfo(bmsInfo);
		String json = JsonUtil.beanToJson(alarmInfo, true);
		map.put("SupplyAlarmInfo", json);
		map.put("InterfaceName", "supplement_alarmInfo");
		String result = supplyCollectCheckMgmt.supplyCollectCheck(map);
		logger.debug("result[{}]",result);
		return result;
	}
	@RequestMapping("/supplement_eventInfo")
	public String supplyCollectCheck4() throws Exception {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("InterfaceName", "supplement_eventInfo");
		EventInfo eventInfo = new EventInfo();
		eventInfo.setStationID("440209002");
		eventInfo.setOperatorID("061402628");
		eventInfo.setEquipmentOwnerID("061402628");
		eventInfo.setStationName("某某充电站");
		eventInfo.setEventType("3");
		eventInfo.setEventCode("3001");
		eventInfo.setEventTime(new Date());
		eventInfo.setNoteString("注意内容：**停车场附近");
		String json = JsonUtil.beanToJson(eventInfo, true);
		map.put("SupplyEventInfo", json);
		logger.debug("json[{}]",json);
		String result = supplyCollectCheckMgmt.supplyCollectCheck(map);
		logger.debug("result[{}]",result);
		return result;
	}
	@RequestMapping("/supplement_stationInfo")
	public String supplyCollectCheck5() throws Exception {
		Map<String,Object> map = new HashMap<String,Object>();
		List<StationInfo> info = new ArrayList<StationInfo>();
		StationInfo stationInfo = new StationInfo();
		stationInfo.setOperatorID("MA5DRRDX1");
		stationInfo.setStationID("DSTCDZ"); 
		stationInfo.setStationName("大水田充电站");
		stationInfo.setEquipmentOwnerID("MA5DRRDX1");
		stationInfo.setCountryCode("CN");
		stationInfo.setAreaCode("440343");
		stationInfo.setAddress("深圳市龙华新区观澜街道大水田社区环观南路与3号路交叉口");
		stationInfo.setStationTel("12345678911");
		stationInfo.setServiceTel("12345678911");
		stationInfo.setStationType(1);
		stationInfo.setStationStatus(50);
		stationInfo.setParkNums(3);
		stationInfo.setStationLng(119.97049);
		stationInfo.setStationLat(31.717877);
		stationInfo.setOpenAllDay(1);
		stationInfo.setConstruction(1);
		stationInfo.setParkFree(1);
		stationInfo.setOpratorType(1);
		String[] pictures = new String[]{"http://www.xxx.com/uploads/image/isbiLogo_1.jpg","http://www.xxx.com/uploads/image/isbiLogo_2.jpg"};
		stationInfo.setPictures(pictures);
		List<EquipmentInfo> elist = new ArrayList<EquipmentInfo>();
		for (int j = 0; j < 2; j++) {
			EquipmentInfo equipmentInfo = new EquipmentInfo();
			equipmentInfo.setEquipmentID("10000000000"+(int)(Math.random()*10000+1000));
			System.out.println("子串:"+equipmentInfo.getEquipmentID().substring(0, equipmentInfo.getEquipmentID().length()-3));
			equipmentInfo.setEquipmentName("01");
			equipmentInfo.setManufacturerID("MA5DRRDX1");
			equipmentInfo.setEquipmentModel("p3");
			equipmentInfo.setProductionDate(new Date());
			equipmentInfo.setEquipmentType(3);
			equipmentInfo.setPower(60.00);
			equipmentInfo.setEquipmentStatus(50); 
			equipmentInfo.setEquipmentPower(3.3); 
			equipmentInfo.setNewNationalStandard(1);
			List<ConnectorInfo> clist = new ArrayList<ConnectorInfo>();
			for (int k = 0; k < 2; k++) {
				ConnectorInfo connectorInfo = new ConnectorInfo();
				connectorInfo.setConnectorID(""+100);
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
		info.add(stationInfo);
		String json = JsonUtil.beanToJson(info, true);
		map.put("StationInfos", JSONArray.parseArray(json));
		String json3 = JsonUtil.beanToJson(map, true);
		map.put("SupplyStationInfo", json3);
		map.put("InterfaceName", "supplement_stationInfo");
		String result = supplyCollectCheckMgmt.supplyCollectCheck(map);
		logger.debug("result[{}]",result);
		return result;
	
	}
}
