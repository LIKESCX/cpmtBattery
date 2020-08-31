package com.cpit.cpmt.biz.dao.exchange.operator;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.operator.EquipmentHistoryInfo;
import org.apache.ibatis.annotations.Param;

@MyBatisDao
public interface EquipmentHistoryInfoDAO {

    int insertSelective(EquipmentHistoryInfo record);

    EquipmentHistoryInfo selectByPrimaryKey(String eID);

    int updateByPrimaryKeySelective(EquipmentHistoryInfo record);

    //分页查询
    Page<EquipmentHistoryInfo> selectEquipmentHistoryInfo(EquipmentHistoryInfo record);

    //查询最新的一条信息
    EquipmentHistoryInfo selectEquNewestOne(@Param("equipmentID") String equipmentID, @Param("operatorID") String operatorID);

    EquipmentHistoryInfo selectEquipmentHisOneInfo(EquipmentHistoryInfo record);
}