package com.cpit.cpmt.biz.dao.monitor;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfo;
import com.cpit.cpmt.dto.monitor.AlarmConditions;
import com.cpit.cpmt.dto.monitor.EquimentMonitorCondition;

@MyBatisDao
public interface RealTimeAlarmInfoDao {

	public Page<AlarmInfo> queryRealTimeAlarmInfoByCondition(EquimentMonitorCondition emc);
	
	public Page<AlarmInfo> queryAlarmDetailInfosByConditions(AlarmConditions alarmConditions);
	
	public List<AlarmInfo> queryAlarmGraphicDisplayInfoByConditions(@Param("operatorID")String operatorID,@Param("connectorID") String connectorID);
	
	public List<AlarmInfo> queryAlarmSumCountByConditions(@Param("operatorID")String operatorID,@Param("connectorID") String connectorID);

	public List<AlarmInfo> queryWithiTwoHoursUndeterminedNewAlarmNums(AlarmConditions alarmConditions);
	
	public List<AlarmInfo> queryWithiTwoHoursDeterminedNewAlarmNums(AlarmConditions alarmConditions);

	public List<AlarmInfo> queryWithiTwoHoursDeterminedOldAndNewAlarmNums(AlarmConditions alarmConditions);

	public List<AlarmInfo> queryWithiTwoHoursUndeterminedOldAndNewAlarmNums(AlarmConditions alarmConditions);

	public List<AlarmInfo> queryWithoutTwoHoursOccurrenceStateOldAlarmNums(AlarmConditions alarmConditions);

	public List<AlarmInfo> queryWithoutTwoHoursDeterminedOldAlarmNums(AlarmConditions alarmConditions);

	public List<AlarmInfo> queryWithoutTwoHoursUndeterminedOldAlarmNums(AlarmConditions alarmConditions);

	public List<AlarmInfo> queryAlarmGraphicDisplayInfo2_1(AlarmConditions alarmConditions);

	public List<AlarmInfo> queryAlarmGraphicDisplayInfo2_2(AlarmConditions alarmConditions);

	public List<AlarmInfo> queryAlarmGraphicDisplayInfo2_3(AlarmConditions alarmConditions);

	public Page<AlarmInfo> queryAlarmHistoryDtailInfoByConditions(AlarmConditions alarmConditions);

	public List<AlarmInfo> queryAlarmGraphicDisplayInfo3_1(AlarmConditions alarmConditions);

	public List<AlarmInfo> queryAlarmGraphicDisplayInfo3_2(AlarmConditions alarmConditions);

	public List<AlarmInfo> queryAlarmGraphicDisplayInfo3_3(AlarmConditions alarmConditions);
}
