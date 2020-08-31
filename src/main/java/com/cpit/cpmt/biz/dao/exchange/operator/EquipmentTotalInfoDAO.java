package com.cpit.cpmt.biz.dao.exchange.operator;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.exchange.operator.EquipmentTotalInfo;

@MyBatisDao
public interface EquipmentTotalInfoDAO {

    int insertSelective(EquipmentTotalInfo record);

    EquipmentTotalInfo selectByPrimaryKey(EquipmentTotalInfo key);

    int updateByPrimaryKeySelective(EquipmentTotalInfo record);

}