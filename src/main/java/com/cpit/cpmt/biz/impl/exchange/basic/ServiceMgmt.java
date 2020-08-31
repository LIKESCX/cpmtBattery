package com.cpit.cpmt.biz.impl.exchange.basic;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cpit.cpmt.biz.dao.exchange.basic.AlarmInfoDao;
import com.cpit.cpmt.biz.dao.exchange.basic.BmsInfoDao;
import com.cpit.cpmt.biz.dao.exchange.basic.DisEquipmentStatusInfoDao;
import com.cpit.cpmt.biz.dao.exchange.basic.EventInfoDao;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfo;
import com.cpit.cpmt.dto.exchange.basic.BmsInfo;
import com.cpit.cpmt.dto.exchange.basic.DisEquipmentStatusInfo;
import com.cpit.cpmt.dto.exchange.basic.EventInfo;

@Service
public class ServiceMgmt {
	/*
	 * 插入事件信息推送
	 */
	@Autowired
	EventInfoDao eventInfoDao;
	
	@Autowired
	AlarmInfoDao alarmInfoDao;
	
	@Autowired
	BmsInfoDao bmsInfoDao;
	
	@Autowired
	DisEquipmentStatusInfoDao disEquipmentStatusInfoDao;
	
	/*
	 * 插入事件信息的推送和查询
	 */
	@Transactional
	public Map<String,Object> insertNotificationEventInfo(List<EventInfo> eventInfoList) {
		//循环遍历插入数据库
		for (EventInfo eventInfo : eventInfoList) {
			eventInfo.setInTime(new Date());//入库时间
			eventInfoDao.insertSelective(eventInfo);
		}
		Map<String,Object> retMap = new HashMap<String,Object>();
		//{"Ret":0,"Msg":"请求成功","Data":{"Status":0}}
		//retMap = {"Ret":0,"Msg":"请求成功","Data":{"Status":0}};
		return retMap;
	}
	
	/*
	 * 插入告警信息的推送和查询
	 */
	@Transactional
	public Map<String,Object> insertNotificationAlarmInfo(List<AlarmInfo> alarmInfoList) {
		//1.分析json校验结果
		Map<String,Object> map = new HashMap<String,Object>();
		//List<AlarmInfo> alarmInfoList = JSON.parseArray(getJsonAlarmInfos, AlarmInfo.class);
		//循环遍历插入数据库
		for (AlarmInfo alarmInfo : alarmInfoList) {
			//alarmInfo.setReceivedTime(receivedTime);//中心平台接收时间
			//String inTime = TimeConvertor.getDate(TimeConvertor.FORMAT_MINUS_24HOUR);//生成入库时间
			//alarmInfo.setInTime(inTime);//入库时间
			alarmInfoDao.insertSelective(alarmInfo);
			//获取设备接口编码
			String connectorId = alarmInfo.getConnectorID();
			BmsInfo bmsInfo = alarmInfo.getBmsInfo();
			bmsInfo.setConnectorID(connectorId);
			//bmsInfo.setReceivedTime(receivedTime);//平台收到时间
			//bmsInfo.setInTime(inTime);//入库时间
		
			bmsInfo.setOperatorID(alarmInfo.getOperatorID());
			bmsInfo.setStationID(alarmInfo.getStationID());
			bmsInfo.setEquipmentID(alarmInfo.getEquipmentID());
			//没有用到
			//bmsInfoDao.insertSelective(bmsInfo);
		}
		map.put("value","成功");
		return map;
	}
 	
	/*
	 * 9.15　配电信息查询
	 */
	@Transactional
	public void saveDisEquipmentStatusInfos(List<DisEquipmentStatusInfo> desiInfo) {
		for (DisEquipmentStatusInfo record : desiInfo) {
			disEquipmentStatusInfoDao.insertSelective(record);
		}
	}
	
}
