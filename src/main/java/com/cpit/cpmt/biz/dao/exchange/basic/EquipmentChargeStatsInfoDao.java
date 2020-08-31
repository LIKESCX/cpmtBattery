package com.cpit.cpmt.biz.dao.exchange.basic;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.exchange.basic.EquipmentChargeStatsInfo;
@MyBatisDao
public interface EquipmentChargeStatsInfoDao {
    int deleteByPrimaryKey(String equipmentId);

    int insert(EquipmentChargeStatsInfo record);

    int insertSelective(EquipmentChargeStatsInfo record);

    EquipmentChargeStatsInfo selectByPrimaryKey(String equipmentId);

    int updateByPrimaryKeySelective(EquipmentChargeStatsInfo record);

    int updateByPrimaryKey(EquipmentChargeStatsInfo record);
    
    EquipmentChargeStatsInfo getByFK(@Param("stationOrder") String stationOrder,@Param("equipmentID") String equipmentID);
    void updateChargeStats(EquipmentChargeStatsInfo record);
}