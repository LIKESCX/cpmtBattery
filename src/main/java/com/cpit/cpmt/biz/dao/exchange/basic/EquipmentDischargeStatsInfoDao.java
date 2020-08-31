package com.cpit.cpmt.biz.dao.exchange.basic;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.exchange.basic.EquipmentDischargeStatsInfo;
@MyBatisDao
public interface EquipmentDischargeStatsInfoDao {
    int deleteByPrimaryKey(String equipmentId);

    int insert(EquipmentDischargeStatsInfo record);

    int insertSelective(EquipmentDischargeStatsInfo record);

    EquipmentDischargeStatsInfo selectByPrimaryKey(String equipmentId);

    int updateByPrimaryKeySelective(EquipmentDischargeStatsInfo record);

    int updateByPrimaryKey(EquipmentDischargeStatsInfo record);
}