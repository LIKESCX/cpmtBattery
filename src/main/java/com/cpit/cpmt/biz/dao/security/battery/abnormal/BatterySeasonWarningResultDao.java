package com.cpit.cpmt.biz.dao.security.battery.abnormal;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.security.battery.abnormal.BatterySeasonWarningResult;
import com.cpit.cpmt.dto.security.battery.other.BatteryDataConditions;
@MyBatisDao
public interface BatterySeasonWarningResultDao {
    int deleteByPrimaryKey(String id);
    int insert(BatterySeasonWarningResult record);
    int insertSelective(BatterySeasonWarningResult record);
    BatterySeasonWarningResult selectByPrimaryKey(String id);
    int updateByPrimaryKeySelective(BatterySeasonWarningResult record);
    int updateByPrimaryKey(BatterySeasonWarningResult record);
	List<BatterySeasonWarningResult> queryBatterySeasonWarningResult(@Param("operatorId")String operatorId,@Param("startDate")String startDate, @Param("endDate")String endDate);
	void addBatchBatterySeasonWarningResult(List<BatterySeasonWarningResult> infoList2);
	List<BatterySeasonWarningResult> queryFourthLevelAlarmTypeDistribution(@Param("param")BatteryDataConditions param);
	List<BatterySeasonWarningResult> queryFourthLevelAlarmLevelDistribution(@Param("param")BatteryDataConditions param);
	List<BatterySeasonWarningResult> getEverySeasonBatteryWarningLevelDistribution(BatteryDataConditions param);
	List<BatterySeasonWarningResult> getEverySeasonBatteryWarningCodeDistribution(BatteryDataConditions param);
}