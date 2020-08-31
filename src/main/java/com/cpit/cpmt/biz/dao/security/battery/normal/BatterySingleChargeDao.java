package com.cpit.cpmt.biz.dao.security.battery.normal;

import com.cpit.cpmt.biz.common.MyBatisDao;
import com.cpit.cpmt.biz.dto.BatterySingleCharge;
@MyBatisDao
public interface BatterySingleChargeDao {
    int deleteByPrimaryKey(String id);
    int insert(BatterySingleCharge record);
    int insertSelective(BatterySingleCharge record);
    BatterySingleCharge selectByPrimaryKey(String id);
    int updateByPrimaryKeySelective(BatterySingleCharge record);
    int updateByPrimaryKey(BatterySingleCharge record);
//	Page<BatterySingleCharge> queryBmsCodeListData(@Param("param")BatteryDataConditions param);
//	List<BatterySingleCharge> queryFirstLevelData(@Param("param")BatteryDataConditions param);
//	Page<BatterySingleCharge> querySecondLevelData(@Param("param")BatteryDataConditions param);
//	Page<BatterySingleCharge> queryThirdLevelData(@Param("param")BatteryDataConditions param);
//	List<Integer> getSohListByparam(@Param("param")BatteryDataConditions param);
    
}