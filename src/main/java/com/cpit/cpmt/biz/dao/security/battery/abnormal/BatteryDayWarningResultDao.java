package com.cpit.cpmt.biz.dao.security.battery.abnormal;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.security.battery.abnormal.BatteryDayWarningResult;
import com.cpit.cpmt.dto.security.battery.abnormal.BatterySingleWarningResult;
import com.cpit.cpmt.dto.security.battery.other.BatteryDataConditions;
@MyBatisDao
public interface BatteryDayWarningResultDao {
    int deleteByPrimaryKey(String id);
    int insert(BatteryDayWarningResult record);
    int insertSelective(BatteryDayWarningResult record);
    BatteryDayWarningResult selectByPrimaryKey(String id);
    int updateByPrimaryKeySelective(BatteryDayWarningResult record);

    int updateByPrimaryKey(BatteryDayWarningResult record);
	List<BatteryDayWarningResult> queryBatteryDayWarningResult(@Param("operatorID")String operatorID,@Param("date")Date date);
	void addBatchBatteryDayWarningResult(List<BatteryDayWarningResult> infoList2);
	List<BatteryDayWarningResult> queryFourthLevelAlarmTypeDistribution(@Param("param")BatteryDataConditions param);
	List<BatteryDayWarningResult> queryFourthLevelAlarmLevelDistribution(@Param("param")BatteryDataConditions param);

}