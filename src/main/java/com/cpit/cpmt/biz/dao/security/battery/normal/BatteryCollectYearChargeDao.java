package com.cpit.cpmt.biz.dao.security.battery.normal;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.security.battery.normal.BatteryCollectYearCharge;
import com.cpit.cpmt.dto.security.battery.other.BatteryDataConditions;
@MyBatisDao
public interface BatteryCollectYearChargeDao {
    int deleteByPrimaryKey(String id);
    int insert(BatteryCollectYearCharge record);
    int insertSelective(BatteryCollectYearCharge record);
    BatteryCollectYearCharge selectByPrimaryKey(String id);
    int updateByPrimaryKeySelective(BatteryCollectYearCharge record);
    int updateByPrimaryKey(BatteryCollectYearCharge record);
	List<BatteryCollectYearCharge> queryBatteryCollectYear(@Param("operatorId")String operatorId,@Param("startDate")String startDate, @Param("endDate")String endDate);
	void addBatchBatteryCollectYear(List<BatteryCollectYearCharge> infoList);
	BatteryCollectYearCharge showBatteryYearReport(BatteryDataConditions param);
	Page<BatteryCollectYearCharge> queryReportBmsCodeListData(@Param("param")BatteryDataConditions param);
}