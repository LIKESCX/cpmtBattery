package com.cpit.cpmt.biz.dao.security.battery.normal;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.security.battery.abnormal.BatteryMonthWarningResult;
import com.cpit.cpmt.dto.security.battery.abnormal.BatterySeasonWarningResult;
import com.cpit.cpmt.dto.security.battery.normal.BatteryCollectSeasonCharge;
import com.cpit.cpmt.dto.security.battery.normal.BatteryCollectYearCharge;
import com.cpit.cpmt.dto.security.battery.other.BatteryDataConditions;
@MyBatisDao
public interface BatteryCollectSeasonChargeDao {
    int deleteByPrimaryKey(String id);
    int insert(BatteryCollectSeasonCharge record);
    int insertSelective(BatteryCollectSeasonCharge record);
    BatteryCollectSeasonCharge selectByPrimaryKey(String id);
    int updateByPrimaryKeySelective(BatteryCollectSeasonCharge record);
    int updateByPrimaryKey(BatteryCollectSeasonCharge record);
	List<BatteryCollectSeasonCharge> queryBatteryCollectSeason(@Param("operatorId")String operatorId,@Param("startDate")String startDate, @Param("endDate")String endDate);
	void addBatchBatteryCollectSeason(List<BatteryCollectSeasonCharge> infoList);
	BatteryCollectSeasonCharge showBatterySeasonReport(BatteryDataConditions param);
	List<BatteryCollectSeasonCharge> queryFirstLevelData(@Param("param")BatteryDataConditions param);
	List<BatteryMonthWarningResult> getEveryMonthBatteryWarningCodeDistribution(BatteryDataConditions param);
	List<BatteryMonthWarningResult> getEveryMonthBatteryWarningLevelDistribution(BatteryDataConditions param);
	//BatteryCollectYearCharge showBatteryYearReport(BatteryDataConditions param);
	Page<BatteryCollectSeasonCharge> queryReportBmsCodeListData(@Param("param")BatteryDataConditions param);
}