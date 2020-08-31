package com.cpit.cpmt.biz.dao.security.battery.abnormal;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.security.battery.abnormal.BatterySingleWarningResult;
import com.cpit.cpmt.dto.security.battery.other.BatteryDataConditions;
@MyBatisDao
public interface BatterySingleWarningResultDao {
    int deleteByPrimaryKey(String id);
    int insert(BatterySingleWarningResult record);
    int insertSelective(BatterySingleWarningResult record);
    BatterySingleWarningResult selectByPrimaryKey(String id);
    int updateByPrimaryKeySelective(BatterySingleWarningResult record);
    int updateByPrimaryKey(BatterySingleWarningResult record);
	List<BatterySingleWarningResult> queryFourthLevelAlarmTypeDistribution(@Param("param")BatteryDataConditions param);
	List<BatterySingleWarningResult> queryFourthLevelAlarmLevelDistribution(@Param("param")BatteryDataConditions param);
}