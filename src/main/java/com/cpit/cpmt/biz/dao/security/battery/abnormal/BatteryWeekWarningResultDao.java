package com.cpit.cpmt.biz.dao.security.battery.abnormal;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.security.battery.abnormal.BatteryDayWarningResult;
import com.cpit.cpmt.dto.security.battery.abnormal.BatteryWeekWarningResult;
import com.cpit.cpmt.dto.security.battery.other.BatteryDataConditions;
@MyBatisDao
public interface BatteryWeekWarningResultDao {
    int deleteByPrimaryKey(String id);
    int insert(BatteryWeekWarningResult record);
    int insertSelective(BatteryWeekWarningResult record);
    BatteryWeekWarningResult selectByPrimaryKey(String id);
    int updateByPrimaryKeySelective(BatteryWeekWarningResult record);
    int updateByPrimaryKey(BatteryWeekWarningResult record);
	List<BatteryWeekWarningResult> queryBatteryWeekWarningResult(@Param("operatorId")String operatorId,@Param("startDate")Date startDate, @Param("endDate")Date endDate);
	void addBatchBatteryWeekWarningResult(List<BatteryWeekWarningResult> infoList2);
	List<BatteryWeekWarningResult> queryFourthLevelAlarmTypeDistribution(@Param("param")BatteryDataConditions param);
	List<BatteryWeekWarningResult> queryFourthLevelAlarmLevelDistribution(@Param("param")BatteryDataConditions param);

}