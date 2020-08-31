package com.cpit.cpmt.biz.dao.security.battery.normal;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.security.battery.normal.BatteryCollectWeekCharge;
import com.cpit.cpmt.dto.security.battery.other.BatteryDataConditions;
@MyBatisDao
public interface BatteryCollectWeekChargeDao {
    int deleteByPrimaryKey(String id);
    int insert(BatteryCollectWeekCharge record);
    int insertSelective(BatteryCollectWeekCharge record);
    BatteryCollectWeekCharge selectByPrimaryKey(String id);
    int updateByPrimaryKeySelective(BatteryCollectWeekCharge record);
    int updateByPrimaryKey(BatteryCollectWeekCharge record);
    
	List<BatteryCollectWeekCharge> queryBatteryCollectWeek(@Param("operatorId")String operatorId,@Param("startDate")Date startDate, @Param("endDate")Date endDate);
	void addBatchBatteryCollectWeek(List<BatteryCollectWeekCharge> infoList);
	List<BatteryCollectWeekCharge> queryFirstLevelData(@Param("param")BatteryDataConditions param);
}