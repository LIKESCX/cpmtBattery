package com.cpit.cpmt.biz.dao.security.battery.normal;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.security.battery.normal.BatteryCollectMonthCharge;
import com.cpit.cpmt.dto.security.battery.other.BatteryDataConditions;
@MyBatisDao
public interface BatteryCollectMonthChargeDao {
    int deleteByPrimaryKey(String id);
    int insert(BatteryCollectMonthCharge record);
    int insertSelective(BatteryCollectMonthCharge record);
    BatteryCollectMonthCharge selectByPrimaryKey(String id);
    int updateByPrimaryKeySelective(BatteryCollectMonthCharge record);
    int updateByPrimaryKey(BatteryCollectMonthCharge record);
	List<BatteryCollectMonthCharge> queryBatteryCollectMonth(@Param("operatorId")String operatorId, @Param("startDate")Date startDate,@Param("endDate")Date endDate);
	void addBatchBatteryCollectMonth(List<BatteryCollectMonthCharge> infoList);
	BatteryCollectMonthCharge showBatteryMonthReport(BatteryDataConditions param);
	List<BatteryCollectMonthCharge> getEveryMonthSohAndRemainCapacity(BatteryDataConditions param);
	List<BatteryCollectMonthCharge> queryFirstLevelData(@Param("param")BatteryDataConditions param);
	Page<BatteryCollectMonthCharge> queryReportBmsCodeListData(@Param("param")BatteryDataConditions param);
}