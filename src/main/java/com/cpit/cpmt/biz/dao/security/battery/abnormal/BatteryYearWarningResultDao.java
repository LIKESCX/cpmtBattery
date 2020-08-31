package com.cpit.cpmt.biz.dao.security.battery.abnormal;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.security.battery.abnormal.BatteryMonthWarningResult;
import com.cpit.cpmt.dto.security.battery.abnormal.BatteryYearWarningResult;
import com.cpit.cpmt.dto.security.battery.other.BatteryDataConditions;
@MyBatisDao
public interface BatteryYearWarningResultDao {
    int deleteByPrimaryKey(String id);
    int insert(BatteryYearWarningResult record);
    int insertSelective(BatteryYearWarningResult record);
    BatteryYearWarningResult selectByPrimaryKey(String id);
    int updateByPrimaryKeySelective(BatteryYearWarningResult record);
    int updateByPrimaryKey(BatteryYearWarningResult record);
	List<BatteryYearWarningResult> queryBatteryYearWarningResult(@Param("operatorId")String operatorId,@Param("startDate")String startDate, @Param("endDate")String endDate);
	void addBatchBatteryYearWarningResult(List<BatteryYearWarningResult> infoList2);
	List<BatteryYearWarningResult> queryFourthLevelAlarmTypeDistribution(@Param("param")BatteryDataConditions param);
	List<BatteryYearWarningResult> queryFourthLevelAlarmLevelDistribution(@Param("param")BatteryDataConditions param);
	List<BatteryYearWarningResult> getEveryYearBatteryWarningCodeDistribution(BatteryDataConditions param);
	List<BatteryYearWarningResult> getEveryYearBatteryWarningLevelDistribution(BatteryDataConditions param);

}