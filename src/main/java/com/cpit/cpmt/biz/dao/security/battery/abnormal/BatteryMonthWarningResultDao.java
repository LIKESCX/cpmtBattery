package com.cpit.cpmt.biz.dao.security.battery.abnormal;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.security.battery.abnormal.BatteryDayWarningResult;
import com.cpit.cpmt.dto.security.battery.abnormal.BatteryMonthWarningResult;
import com.cpit.cpmt.dto.security.battery.other.BatteryDataConditions;
@MyBatisDao
public interface BatteryMonthWarningResultDao {
    int deleteByPrimaryKey(String id);
    int insert(BatteryMonthWarningResult record);
    int insertSelective(BatteryMonthWarningResult record);
    BatteryMonthWarningResult selectByPrimaryKey(String id);
    int updateByPrimaryKeySelective(BatteryMonthWarningResult record);
    int updateByPrimaryKey(BatteryMonthWarningResult record);
	List<BatteryMonthWarningResult> queryBatteryMonthWarningResult(@Param("operatorId")String operatorId,@Param("startDate")Date startDate,@Param("endDate") Date endDate);
	void addBatchBatteryMonthWarningResult(List<BatteryMonthWarningResult> infoList2);
	List<BatteryMonthWarningResult> getEveryMonthBatteryWarningCodeDistribution(BatteryDataConditions param);
	List<BatteryMonthWarningResult> getEveryMonthBatteryWarningLevelDistribution(BatteryDataConditions param);
	List<BatteryMonthWarningResult> queryFourthLevelAlarmTypeDistribution(@Param("param")BatteryDataConditions param);
	List<BatteryMonthWarningResult> queryFourthLevelAlarmLevelDistribution(@Param("param")BatteryDataConditions param);
}