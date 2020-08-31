package com.cpit.cpmt.biz.dao.exchange.basic;
import java.util.List;
import java.util.Map;

import com.cpit.common.db.Page;
import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfo;
import com.cpit.cpmt.dto.exchange.basic.AlarmItem;
import com.cpit.cpmt.dto.security.battery.other.AbnormalAlarmDataMiningConditions;
@MyBatisDao
public interface AlarmInfoDao {
  
    int deleteByPrimaryKey(Integer id);

    int insert(AlarmInfo record);

    int insertSelective(AlarmInfo record);

    AlarmInfo selectByPrimaryKey(Integer id);
 
    int updateByPrimaryKeySelective(AlarmInfo record);

    int updateByPrimaryKey(AlarmInfo record);

	AlarmItem checkCurrentAlarmValid(@Param("alarmCode")Integer alarmCode,@Param("alarmType")Integer alarmType,@Param("alarmLevel")Integer alarmLevel);

	//查询充电站最新一次告警信息
    String selectStationAlarmLastest (@Param("stationId") String stationId,@Param("operatorId") String operatorId);

    Page<AlarmInfo> selectEquipmentAlarm (@Param("equipmentId") String equipmentId, @Param("operatorId") String operatorId);

    //告警次数
	int getAlarmNum(AlarmInfo record);

    //异常告警数据挖掘分析 第一级钻取 小时
	public List<AlarmInfo> queryFirstLevelHourAbnormalAlarmData(@Param("param")AbnormalAlarmDataMiningConditions param);
	//异常告警数据挖掘分析 第一级钻取 天
	public List<AlarmInfo> queryFirstLevelDayAbnormalAlarmData(@Param("param")AbnormalAlarmDataMiningConditions param);
	//异常告警数据挖掘分析 第一级钻取 周
	public List<AlarmInfo> queryFirstLevelWeekAbnormalAlarmData(@Param("param")AbnormalAlarmDataMiningConditions param);
	//异常告警数据挖掘分析 第一级钻取 季份
	public List<AlarmInfo> queryFirstLevelMonthAbnormalAlarmData(@Param("param")AbnormalAlarmDataMiningConditions param);
	//异常告警数据挖掘分析 第一级钻取 年份
	public List<AlarmInfo> queryFirstLevelSeasonAbnormalAlarmData(@Param("param")AbnormalAlarmDataMiningConditions param);

	public List<AlarmInfo> querySecondLevelAbnormalAlarmData(@Param("param")AbnormalAlarmDataMiningConditions param);

	Page<AlarmInfo> queryThirdLevelAbnormalAlarmData(@Param("param")AbnormalAlarmDataMiningConditions param);

	public AlarmInfo queryAlikeAlaram(AlarmInfo alarmInfo);
	
}