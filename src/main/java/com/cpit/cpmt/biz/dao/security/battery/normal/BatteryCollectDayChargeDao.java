package com.cpit.cpmt.biz.dao.security.battery.normal;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.security.battery.normal.BatteryCollectDayCharge;
import com.cpit.cpmt.dto.security.battery.other.BatteryDataConditions;
@MyBatisDao
public interface BatteryCollectDayChargeDao {
    int deleteByPrimaryKey(String id);
    int insert(BatteryCollectDayCharge record);
    int insertSelective(BatteryCollectDayCharge record);
    BatteryCollectDayCharge selectByPrimaryKey(String id);
    int updateByPrimaryKeySelective(BatteryCollectDayCharge record);
    int updateByPrimaryKey(BatteryCollectDayCharge record);
    
    public List<BatteryCollectDayCharge> queryBatteryCollectDay(@Param("operatorId")String operatorId,@Param("date")Date date);
	void addBatchBatteryCollectDay(List<BatteryCollectDayCharge> infoList);
	List<BatteryCollectDayCharge> getEveryDaySohAndRemainCapacity(BatteryDataConditions param);
	List<BatteryCollectDayCharge> queryFirstLevelData(@Param("param")BatteryDataConditions param);
}