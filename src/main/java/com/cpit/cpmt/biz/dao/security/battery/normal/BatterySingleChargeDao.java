package com.cpit.cpmt.biz.dao.security.battery.normal;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.security.battery.normal.BatterySingleCharge;
import com.cpit.cpmt.dto.security.battery.other.BatteryDataConditions;
@MyBatisDao
public interface BatterySingleChargeDao {
    int deleteByPrimaryKey(String id);
    int insert(BatterySingleCharge record);
    int insertSelective(BatterySingleCharge record);
    BatterySingleCharge selectByPrimaryKey(String id);
    int updateByPrimaryKeySelective(BatterySingleCharge record);
    int updateByPrimaryKey(BatterySingleCharge record);
	Page<BatterySingleCharge> queryBmsCodeListData(@Param("param")BatteryDataConditions param);
	List<BatterySingleCharge> queryFirstLevelData(@Param("param")BatteryDataConditions param);
	Page<BatterySingleCharge> querySecondLevelData(@Param("param")BatteryDataConditions param);
	Page<BatterySingleCharge> queryThirdLevelData(@Param("param")BatteryDataConditions param);
	List<Integer> getSohListByparam(@Param("param")BatteryDataConditions param);
    
}