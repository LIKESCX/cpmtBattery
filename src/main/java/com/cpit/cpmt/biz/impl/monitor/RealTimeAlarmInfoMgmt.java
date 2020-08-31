package com.cpit.cpmt.biz.impl.monitor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cpit.common.StringUtils;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.exchange.basic.BmsChargeStatDao;
import com.cpit.cpmt.biz.dao.monitor.RealTimeAlarmInfoDao;
import com.cpit.cpmt.biz.impl.exchange.operator.ConnectorMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.EquipmentInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.OperatorInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.StationInfoMgmt;
import com.cpit.cpmt.biz.impl.system.AreaMgmt;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfo;
import com.cpit.cpmt.dto.exchange.operator.ConnectorInfoShow;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import com.cpit.cpmt.dto.monitor.AlarmConditions;
import com.cpit.cpmt.dto.monitor.EquimentMonitorCondition;
import com.cpit.cpmt.dto.system.Area;

@Service
public class RealTimeAlarmInfoMgmt {
	@Autowired RealTimeAlarmInfoDao realTimeAlarmInfoDao;
	@Autowired BmsChargeStatDao bmsChargeStatDao;
	@Autowired OperatorInfoMgmt operatorInfoMgmt;
	@Autowired StationInfoMgmt stationInfoMgmt;
	@Autowired EquipmentInfoMgmt equipmentInfoMgmt;
	@Autowired ConnectorMgmt connectorMgmt;
	@Autowired AreaMgmt areaMgmt;
	public Page<AlarmInfo> queryRealTimeAlarmInfo(EquimentMonitorCondition emc) {
		Page<AlarmInfo> infoList  = realTimeAlarmInfoDao.queryRealTimeAlarmInfoByCondition(emc);
		for (AlarmInfo alarmInfo : infoList) {
			OperatorInfoExtend operatorInfo = operatorInfoMgmt.getOperatorInfoById(alarmInfo.getOperatorID());
			if(operatorInfo!=null)
				alarmInfo.setOperatorName(operatorInfo.getOperatorName());
			
			StationInfoShow stationInfo = stationInfoMgmt.selectByPrimaryKey(alarmInfo.getStationID(), alarmInfo.getOperatorID());
			if(stationInfo!=null) {
				alarmInfo.setStationName(stationInfo.getStationName());
				alarmInfo.setStreetName(stationInfo.getStationStreet());
				if(!StringUtils.isEmpty(stationInfo.getAreaCode())) {
					Area areaCode = areaMgmt.getAreaCode(stationInfo.getAreaCode());
					if(areaCode!=null) {
						alarmInfo.setAreaName(areaCode.getAreaName());
					}
				}
			}
			EquipmentInfoShow equipmentInfo = equipmentInfoMgmt.selectByPrimaryKey(alarmInfo.getEquipmentID(), alarmInfo.getOperatorID());
			if(equipmentInfo!=null)
				alarmInfo.setEquipmentName(equipmentInfo.getEquipmentName());
			ConnectorInfoShow connectorInfo = connectorMgmt.getConnectorById(alarmInfo.getConnectorID(), alarmInfo.getOperatorID());
			if(connectorInfo!=null) 
				alarmInfo.setConnectorName(connectorInfo.getConnectorName());
		}
		return infoList;
	}
	public Page<AlarmInfo> queryAlarmRealDtailInfo(AlarmConditions alarmConditions) {
		Page<AlarmInfo> infoList = realTimeAlarmInfoDao.queryAlarmDetailInfosByConditions(alarmConditions);
		for (AlarmInfo alarmInfo : infoList) {
			OperatorInfoExtend operatorInfo = operatorInfoMgmt.getOperatorInfoById(alarmInfo.getOperatorID());
			if(operatorInfo!=null)
				alarmInfo.setOperatorName(operatorInfo.getOperatorName());
			
			StationInfoShow stationInfo = stationInfoMgmt.selectByPrimaryKey(alarmInfo.getStationID(), alarmInfo.getOperatorID());
			if(stationInfo!=null) {
				alarmInfo.setStationName(stationInfo.getStationName());
				alarmInfo.setStreetName(stationInfo.getStationStreet());
				if(!StringUtils.isEmpty(stationInfo.getAreaCode())) {
					Area areaCode = areaMgmt.getAreaCode(stationInfo.getAreaCode());
					if(areaCode!=null) {
						alarmInfo.setAreaName(areaCode.getAreaName());
					}
				}
			}
			EquipmentInfoShow equipmentInfo = equipmentInfoMgmt.selectByPrimaryKey(alarmInfo.getEquipmentID(), alarmInfo.getOperatorID());
			if(equipmentInfo!=null)
				alarmInfo.setEquipmentName(equipmentInfo.getEquipmentName());
			ConnectorInfoShow connectorInfo = connectorMgmt.getConnectorById(alarmInfo.getConnectorID(), alarmInfo.getOperatorID());
			if(connectorInfo!=null) 
				alarmInfo.setConnectorName(connectorInfo.getConnectorName());
		}
		return infoList;
	}
	public List<Map<String,String>> queryAlarmRealGraphicDisplayInfo(AlarmConditions alarmConditions) {
//		AlarmConditions alarmConditions = new AlarmConditions();
//		alarmConditions.setOperatorID(operatorID);
//		alarmConditions.setConnectorID(connectorID);
//		Date endTime = new Date();
//		Calendar calendar = Calendar.getInstance();
//	       /* HOUR_OF_DAY 指示一天中的小时 */
//		calendar.setTime(endTime);
//	    calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 2);
//	    Date startTime = calendar.getTime();
//		alarmConditions.setStartTime(startTime);
//		alarmConditions.setEndTime(endTime);
		List<AlarmInfo> infoList1 = realTimeAlarmInfoDao.queryAlarmGraphicDisplayInfo2_1(alarmConditions);
		List<AlarmInfo> infoList2 = realTimeAlarmInfoDao.queryAlarmGraphicDisplayInfo2_2(alarmConditions);
		List<AlarmInfo> infoList3 = realTimeAlarmInfoDao.queryAlarmGraphicDisplayInfo2_3(alarmConditions);
		List<AlarmInfo> newInfoList1 = commonHanderMethod0(infoList1,alarmConditions);
		List<AlarmInfo> newInfoList2 = commonHanderMethod0(infoList2,alarmConditions);
		List<AlarmInfo> newInfoList3 = commonHanderMethod0(infoList3,alarmConditions);
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		for (int i = 0; i < 24; i++) {
			int alarmSumCount = 0;
			Map<String,String> map = new HashMap<String,String>();
			AlarmInfo al1 = newInfoList1.get(i);
			map.put("returnTime", DateFormatUtils.format(al1.getReturnTime(), "MM-dd HH:mm"));
			map.put("alarmLevel_1", String.valueOf(al1.getAlarmCount()));
			AlarmInfo al2 =newInfoList2.get(i);
			map.put("alarmLevel_2", String.valueOf(al2.getAlarmCount()));
			AlarmInfo al3 =newInfoList3.get(i);
			map.put("alarmLevel_3", String.valueOf(al3.getAlarmCount()));
			alarmSumCount = alarmSumCount + al1.getAlarmCount()+al2.getAlarmCount()+al3.getAlarmCount();
			map.put("alarmSumCount", String.valueOf(alarmSumCount));
			mapList.add(map);
		}
		return mapList;
	}
	public List<Map<String,String>> queryAlarmHistoryGraphicDisplayInfo(AlarmConditions alarmConditions) {
		List<AlarmInfo> infoList1 = realTimeAlarmInfoDao.queryAlarmGraphicDisplayInfo2_1(alarmConditions);
		List<AlarmInfo> infoList2 = realTimeAlarmInfoDao.queryAlarmGraphicDisplayInfo2_2(alarmConditions);
		List<AlarmInfo> infoList3 = realTimeAlarmInfoDao.queryAlarmGraphicDisplayInfo2_3(alarmConditions);
		List<AlarmInfo> newInfoList1 = commonHanderMethod1(infoList1,alarmConditions);
		List<AlarmInfo> newInfoList2 = commonHanderMethod1(infoList2,alarmConditions);
		List<AlarmInfo> newInfoList3 = commonHanderMethod1(infoList3,alarmConditions);
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		for (int i = 0; i < 12; i++) {
			int alarmSumCount = 0;
			Map<String,String> map = new HashMap<String,String>();
			AlarmInfo al1 = newInfoList1.get(i);
			map.put("returnTime", DateFormatUtils.format(al1.getReturnTime(), "MM-dd HH:mm"));
			map.put("alarmLevel_1", String.valueOf(al1.getAlarmCount()));
			AlarmInfo al2 =newInfoList2.get(i);
			map.put("alarmLevel_2", String.valueOf(al2.getAlarmCount()));
			AlarmInfo al3 =newInfoList3.get(i);
			map.put("alarmLevel_3", String.valueOf(al3.getAlarmCount()));
			alarmSumCount = alarmSumCount + al1.getAlarmCount()+al2.getAlarmCount()+al3.getAlarmCount();
			map.put("alarmSumCount", String.valueOf(alarmSumCount));
			mapList.add(map);
		}
		return mapList;
	}
	
	public List<Map<String,String>> queryWithinTwoHoursAlarmIndexSum(AlarmConditions alarmConditions) {
		/*AlarmConditions alarmConditions = new AlarmConditions();
		alarmConditions.setOperatorID(operatorID);
		alarmConditions.setConnectorID(connectorID);
		Date endTime = new Date();
		Calendar calendar = Calendar.getInstance();
	        HOUR_OF_DAY 指示一天中的小时 
		calendar.setTime(endTime);
	    calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 2);
	    Date startTime = calendar.getTime();
		alarmConditions.setStartTime(startTime);
		alarmConditions.setEndTime(endTime);*/
		List<AlarmInfo> infoList1 = realTimeAlarmInfoDao.queryWithiTwoHoursUndeterminedNewAlarmNums(alarmConditions);
		List<AlarmInfo> infoList2 = realTimeAlarmInfoDao.queryWithiTwoHoursDeterminedNewAlarmNums(alarmConditions);
		List<AlarmInfo> infoList3 = realTimeAlarmInfoDao.queryWithiTwoHoursUndeterminedOldAndNewAlarmNums(alarmConditions);
		List<AlarmInfo> infoList4 = realTimeAlarmInfoDao.queryWithiTwoHoursDeterminedOldAndNewAlarmNums(alarmConditions);
		List<AlarmInfo> newInfoList1 = commonHanderMethod(infoList1,alarmConditions);
		List<AlarmInfo> newInfoList2 = commonHanderMethod(infoList2,alarmConditions);
		List<AlarmInfo> newInfoList3 = commonHanderMethod(infoList3,alarmConditions);
		List<AlarmInfo> newInfoList4 = commonHanderMethod(infoList4,alarmConditions);
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		for (int i = 0; i < 24; i++) {
			Map<String,String> map = new HashMap<String,String>();
			AlarmInfo al1 = newInfoList1.get(i);
			map.put("returnTime", DateFormatUtils.format(al1.getReturnTime(), "MM-dd HH:mm"));
			map.put("zhibiao1", String.valueOf(al1.getAlarmCount()));
			AlarmInfo al2 =newInfoList2.get(i);
			map.put("zhibiao2", String.valueOf(al2.getAlarmCount()));
			AlarmInfo al3 =newInfoList3.get(i);
			map.put("zhibiao3", String.valueOf(al3.getAlarmCount()));
			AlarmInfo al4 =newInfoList4.get(i);
			map.put("zhibiao4", String.valueOf(al4.getAlarmCount()));
			mapList.add(map);
		}
		return mapList;
		
	}
	
	public List<AlarmInfo> commonHanderMethod(List<AlarmInfo> infoList,AlarmConditions alarmConditions){
		Date startTime = alarmConditions.getStartTime();
		Date endTime = alarmConditions.getEndTime();
		Calendar calendar = Calendar.getInstance();
		List<AlarmInfo> newInfoList = new ArrayList<AlarmInfo>();
		outer:for (AlarmInfo alarmInfo : infoList) {
			for (int i = 0; i < 24; i++) {
				calendar.setTime(startTime);
				calendar.add(Calendar.MINUTE, 5);
				startTime=calendar.getTime();
				if(startTime.compareTo(alarmInfo.getReturnTime())!=0) {
					AlarmInfo newAlarmInfo = new AlarmInfo();
					newAlarmInfo.setAlarmCount(0);
					newAlarmInfo.setReturnTime(startTime);
					newInfoList.add(newAlarmInfo);
					continue;
				}else {
					newInfoList.add(alarmInfo);
					continue outer;
				}
			}
		}
		
		for (; startTime.compareTo(endTime)==-1;) {
			calendar.setTime(startTime);
			calendar.add(Calendar.MINUTE, 5);
			startTime=calendar.getTime();
			AlarmInfo newAlarmInfo = new AlarmInfo();
			newAlarmInfo.setAlarmCount(0);
			newAlarmInfo.setReturnTime(startTime);
			newInfoList.add(newAlarmInfo);
		}
		
		return newInfoList;
	}
	
	public List<Map<String,String>> queryWithoutTwoHoursAlarmIndexSum(AlarmConditions alarmConditions) {
		List<AlarmInfo> infoList1 = realTimeAlarmInfoDao.queryWithoutTwoHoursOccurrenceStateOldAlarmNums(alarmConditions);
		List<AlarmInfo> infoList2 = realTimeAlarmInfoDao.queryWithoutTwoHoursDeterminedOldAlarmNums(alarmConditions);
		List<AlarmInfo> infoList3 = realTimeAlarmInfoDao.queryWithoutTwoHoursUndeterminedOldAlarmNums(alarmConditions);
		List<AlarmInfo> newInfoList1 = commonHanderMethod2(infoList1, alarmConditions);
		List<AlarmInfo> newInfoList2 = commonHanderMethod2(infoList2, alarmConditions);
		List<AlarmInfo> newInfoList3 = commonHanderMethod2(infoList3, alarmConditions);
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		for (int i = 0; i < 24; i++) {
			Map<String,String> map = new HashMap<String,String>();
			AlarmInfo al1 = newInfoList1.get(i);
			map.put("returnTime", DateFormatUtils.format(al1.getReturnTime(), "MM-dd HH"));
			map.put("zhibiao1", String.valueOf(al1.getAlarmCount()));
			AlarmInfo al2 =newInfoList2.get(i);
			map.put("zhibiao2", String.valueOf(al2.getAlarmCount()));
			AlarmInfo al3 =newInfoList3.get(i);
			map.put("zhibiao3", String.valueOf(al3.getAlarmCount()));
			mapList.add(map);
		}
		return mapList;
	}
	
	public List<AlarmInfo> commonHanderMethod2(List<AlarmInfo> infoList,AlarmConditions alarmConditions){
		Date startTime = alarmConditions.getStartTime();
		Date endTime = alarmConditions.getEndTime();
		Calendar calendar = Calendar.getInstance();
		List<AlarmInfo> newInfoList = new ArrayList<AlarmInfo>();
		outer:for (AlarmInfo alarmInfo : infoList) {
			for (int i = 0; i < 24; i++) {
				calendar.setTime(startTime);
				calendar.add(Calendar.HOUR, 1);
				startTime=calendar.getTime();
				if(startTime.compareTo(alarmInfo.getReturnTime())!=0) {
					AlarmInfo newAlarmInfo = new AlarmInfo();
					newAlarmInfo.setAlarmCount(0);
					newAlarmInfo.setReturnTime(startTime);
					newInfoList.add(newAlarmInfo);
					continue;
				}else {
					newInfoList.add(alarmInfo);
					continue outer;
				}
			}
		}
		
		for (; startTime.compareTo(endTime)==-1;) {
			calendar.setTime(startTime);
			calendar.add(Calendar.HOUR, 1);
			startTime=calendar.getTime();
			AlarmInfo newAlarmInfo = new AlarmInfo();
			newAlarmInfo.setAlarmCount(0);
			newAlarmInfo.setReturnTime(startTime);
			newInfoList.add(newAlarmInfo);
		}
		
		return newInfoList;
	}
	
	public List<AlarmInfo> commonHanderMethod0(List<AlarmInfo> infoList,AlarmConditions alarmConditions){
		Date startTime = alarmConditions.getStartTime();
		Date endTime = alarmConditions.getEndTime();
		Calendar calendar = Calendar.getInstance();
		List<AlarmInfo> newInfoList = new ArrayList<AlarmInfo>();
		outer:for (AlarmInfo alarmInfo : infoList) {
			for (int i = 0; i < 24; i++) {
				calendar.setTime(startTime);
				calendar.add(Calendar.MINUTE, 5);
				startTime=calendar.getTime();
				if(startTime.compareTo(alarmInfo.getReturnTime())!=0) {
					AlarmInfo newAlarmInfo = new AlarmInfo();
					newAlarmInfo.setAlarmCount(0);
					newAlarmInfo.setReturnTime(startTime);
					newInfoList.add(newAlarmInfo);
					continue;
				}else {
					newInfoList.add(alarmInfo);
					continue outer;
				}
			}
		}
		
		for (; startTime.compareTo(endTime)==-1;) {
			calendar.setTime(startTime);
			calendar.add(Calendar.MINUTE, 5);
			startTime=calendar.getTime();
			AlarmInfo newAlarmInfo = new AlarmInfo();
			newAlarmInfo.setAlarmCount(0);
			newAlarmInfo.setReturnTime(startTime);
			newInfoList.add(newAlarmInfo);
		}
		
		return newInfoList;
	}
	public List<AlarmInfo> commonHanderMethod1(List<AlarmInfo> infoList,AlarmConditions alarmConditions){
		Date startTime = alarmConditions.getStartTime();
		Date endTime = alarmConditions.getEndTime();
		Calendar calendar = Calendar.getInstance();
		List<AlarmInfo> newInfoList = new ArrayList<AlarmInfo>();
		outer:for (AlarmInfo alarmInfo : infoList) {
			for (int i = 0; i < 24; i++) {
				calendar.setTime(startTime);
				calendar.add(Calendar.MINUTE, 5);
				startTime=calendar.getTime();
				if(startTime.compareTo(alarmInfo.getReturnTime())!=0) {
					AlarmInfo newAlarmInfo = new AlarmInfo();
					newAlarmInfo.setAlarmCount(0);
					newAlarmInfo.setReturnTime(startTime);
					newInfoList.add(newAlarmInfo);
					continue;
				}else {
					newInfoList.add(alarmInfo);
					continue outer;
				}
			}
		}
		
		for (; startTime.compareTo(endTime)==-1;) {
			calendar.setTime(startTime);
			calendar.add(Calendar.MINUTE, 5);
			startTime=calendar.getTime();
			AlarmInfo newAlarmInfo = new AlarmInfo();
			newAlarmInfo.setAlarmCount(0);
			newAlarmInfo.setReturnTime(startTime);
			newInfoList.add(newAlarmInfo);
		}
		
		return newInfoList;
	}
	public List<AlarmInfo> commonHanderMethod3(List<AlarmInfo> infoList,AlarmConditions alarmConditions,int requiredHours){
		Date startTime = alarmConditions.getStartTime();
		Date endTime = alarmConditions.getEndTime();
		Calendar calendar = Calendar.getInstance();
		List<AlarmInfo> newInfoList = new ArrayList<AlarmInfo>();
		
		outer:for (AlarmInfo alarmInfo : infoList) {
			for (int i = 0; i <requiredHours ; i++) {
				calendar.setTime(startTime);
				calendar.add(Calendar.HOUR, 1);
				startTime=calendar.getTime();
				if(startTime.compareTo(alarmInfo.getReturnTime())!=0) {
					AlarmInfo newAlarmInfo = new AlarmInfo();
					newAlarmInfo.setAlarmCount(0);
					newAlarmInfo.setReturnTime(startTime);
					newInfoList.add(newAlarmInfo);
					continue;
				}else {
					newInfoList.add(alarmInfo);
					continue outer;
				}
			}
		}
		
		for (; startTime.compareTo(endTime)==-1;) {
			calendar.setTime(startTime);
			calendar.add(Calendar.HOUR, 1);
			startTime=calendar.getTime();
			AlarmInfo newAlarmInfo = new AlarmInfo();
			newAlarmInfo.setAlarmCount(0);
			newAlarmInfo.setReturnTime(startTime);
			newInfoList.add(newAlarmInfo);
		}
		
		return newInfoList;
	}
	public Page<AlarmInfo> queryAlarmHistoryDtailInfo(AlarmConditions alarmConditions) {
		Page<AlarmInfo> infoList = realTimeAlarmInfoDao.queryAlarmHistoryDtailInfoByConditions(alarmConditions);
		for (AlarmInfo alarmInfo : infoList) {
			OperatorInfoExtend operatorInfo = operatorInfoMgmt.getOperatorInfoById(alarmInfo.getOperatorID());
			if(operatorInfo!=null)
				alarmInfo.setOperatorName(operatorInfo.getOperatorName());
			
			StationInfoShow stationInfo = stationInfoMgmt.selectByPrimaryKey(alarmInfo.getStationID(), alarmInfo.getOperatorID());
			if(stationInfo!=null) {
				alarmInfo.setStationName(stationInfo.getStationName());
				alarmInfo.setStreetName(stationInfo.getStationStreet());
				if(!StringUtils.isEmpty(stationInfo.getAreaCode())) {
					Area areaCode = areaMgmt.getAreaCode(stationInfo.getAreaCode());
					if(areaCode!=null) {
						alarmInfo.setAreaName(areaCode.getAreaName());
					}
				}
			}
			EquipmentInfoShow equipmentInfo = equipmentInfoMgmt.selectByPrimaryKey(alarmInfo.getEquipmentID(), alarmInfo.getOperatorID());
			if(equipmentInfo!=null)
				alarmInfo.setEquipmentName(equipmentInfo.getEquipmentName());
			ConnectorInfoShow connectorInfo = connectorMgmt.getConnectorById(alarmInfo.getConnectorID(), alarmInfo.getOperatorID());
			if(connectorInfo!=null) 
				alarmInfo.setConnectorName(connectorInfo.getConnectorName());
		}
		return infoList;

	}
	
	public List<String> getBmsCodeListByFirstFourDigits(String value) {
		return bmsChargeStatDao.getBmsCodeListByFirstFourDigits(value);
	}
	
	@Transactional(readOnly=true)
	public Map<String, Object> queryAllAlarmHistoryDtailInfos(AlarmConditions alarmConditions) {
		Map<String,Object> map = new HashMap<String,Object>();
		Page<AlarmInfo> infoList = realTimeAlarmInfoDao.queryAlarmHistoryDtailInfoByConditions(alarmConditions);
		for (AlarmInfo alarmInfo : infoList) {
			OperatorInfoExtend operatorInfo = operatorInfoMgmt.getOperatorInfoById(alarmInfo.getOperatorID());
			if(operatorInfo!=null)
				alarmInfo.setOperatorName(operatorInfo.getOperatorName());
			
			StationInfoShow stationInfo = stationInfoMgmt.selectByPrimaryKey(alarmInfo.getStationID(), alarmInfo.getOperatorID());
			if(stationInfo!=null) {
				alarmInfo.setStationName(stationInfo.getStationName());
				alarmInfo.setStreetName(stationInfo.getStationStreet());
				if(!StringUtils.isEmpty(stationInfo.getAreaCode())) {
					Area areaCode = areaMgmt.getAreaCode(stationInfo.getAreaCode());
					if(areaCode!=null) {
						alarmInfo.setAreaName(areaCode.getAreaName());
					}
				}
			}
			EquipmentInfoShow equipmentInfo = equipmentInfoMgmt.selectByPrimaryKey(alarmInfo.getEquipmentID(), alarmInfo.getOperatorID());
			if(equipmentInfo!=null)
				alarmInfo.setEquipmentName(equipmentInfo.getEquipmentName());
			ConnectorInfoShow connectorInfo = connectorMgmt.getConnectorById(alarmInfo.getConnectorID(), alarmInfo.getOperatorID());
			if(connectorInfo!=null) 
				alarmInfo.setConnectorName(connectorInfo.getConnectorName());
		}
		map.put("infoList", infoList);
		map.put("total", infoList.getTotal());//总记录数
		map.put("pages", infoList.getPages());//总页数
		map.put("pageNum", infoList.getPageNum());//当前页
		long endTime = alarmConditions.getEndTime().getTime();
		long startTime = alarmConditions.getStartTime().getTime();
		long mills = endTime - startTime;
		int requiredHours = (int) (mills / 1000 / 3600);
		List<AlarmInfo> infoList1 = realTimeAlarmInfoDao.queryAlarmGraphicDisplayInfo3_1(alarmConditions);
		List<AlarmInfo> infoList2 = realTimeAlarmInfoDao.queryAlarmGraphicDisplayInfo3_2(alarmConditions);
		List<AlarmInfo> infoList3 = realTimeAlarmInfoDao.queryAlarmGraphicDisplayInfo3_3(alarmConditions);
		List<AlarmInfo> newInfoList1 = commonHanderMethod3(infoList1,alarmConditions,requiredHours);
		List<AlarmInfo> newInfoList2 = commonHanderMethod3(infoList2,alarmConditions,requiredHours);
		List<AlarmInfo> newInfoList3 = commonHanderMethod3(infoList3,alarmConditions,requiredHours);
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		for (int i = 0;i<requiredHours ; i++) {
			int alarmSumCount = 0;
			Map<String,String> map2 = new HashMap<String,String>();
			AlarmInfo al1 = newInfoList1.get(i);
			map2.put("returnTime", DateFormatUtils.format(al1.getReturnTime(), "MM-dd HH"));
			map2.put("alarmLevel_1", String.valueOf(al1.getAlarmCount()));
			AlarmInfo al2 =newInfoList2.get(i);
			map2.put("alarmLevel_2", String.valueOf(al2.getAlarmCount()));
			AlarmInfo al3 =newInfoList3.get(i);
			map2.put("alarmLevel_3", String.valueOf(al3.getAlarmCount()));
			alarmSumCount = alarmSumCount + al1.getAlarmCount()+al2.getAlarmCount()+al3.getAlarmCount();
			map2.put("alarmSumCount", String.valueOf(alarmSumCount));
			mapList.add(map2);
		}
		map.put("infoList2", mapList);
		
		List<AlarmInfo> infoList4 = realTimeAlarmInfoDao.queryWithoutTwoHoursOccurrenceStateOldAlarmNums(alarmConditions);
		List<AlarmInfo> infoList5 = realTimeAlarmInfoDao.queryWithoutTwoHoursDeterminedOldAlarmNums(alarmConditions);
		List<AlarmInfo> infoList6 = realTimeAlarmInfoDao.queryWithoutTwoHoursUndeterminedOldAlarmNums(alarmConditions);
		List<AlarmInfo> newInfoList4 = commonHanderMethod2(infoList4, alarmConditions);
		List<AlarmInfo> newInfoList5 = commonHanderMethod2(infoList5, alarmConditions);
		List<AlarmInfo> newInfoList6 = commonHanderMethod2(infoList6, alarmConditions);
		List<Map<String,String>> mapList3 = new ArrayList<Map<String,String>>();
		for (int i = 0; i < requiredHours; i++) {
			Map<String,String> map3 = new HashMap<String,String>();
			AlarmInfo al1 = newInfoList4.get(i);
			map3.put("returnTime", DateFormatUtils.format(al1.getReturnTime(), "MM-dd HH"));
			map3.put("zhibiao1", String.valueOf(al1.getAlarmCount()));
			AlarmInfo al2 =newInfoList5.get(i);
			map3.put("zhibiao2", String.valueOf(al2.getAlarmCount()));
			AlarmInfo al3 =newInfoList6.get(i);
			map3.put("zhibiao3", String.valueOf(al3.getAlarmCount()));
			mapList3.add(map3);
		}
		map.put("infoList3", mapList3);
		return map;
	}
}
