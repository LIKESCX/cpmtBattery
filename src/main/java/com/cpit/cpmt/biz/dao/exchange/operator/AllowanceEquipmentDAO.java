package com.cpit.cpmt.biz.dao.exchange.operator;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.exchange.operator.AllowanceEquipment;

import java.util.List;

@MyBatisDao
public interface AllowanceEquipmentDAO {

    int deleteByPrimaryKey(String id);

    int insertSelective(AllowanceEquipment record);

    AllowanceEquipment selectByPrimaryKey(String id);

    AllowanceEquipment selectBySelect(AllowanceEquipment record);

    int updateByPrimaryKeySelective(AllowanceEquipment record);

    List<String> getStationByBatchId(String batchId);

    List<String> getEidListByBatchId(String batchId);

    Double selectChargeNum(String batchId);

    Integer selectallowanceTypeByEid(String eid);
}