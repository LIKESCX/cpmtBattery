package com.cpit.cpmt.biz.dao.exchange.operator;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.operator.DisEquipmentHistoryInfo;

@MyBatisDao
public interface DisEquipmentHistoryInfoDAO {

    int deleteByPrimaryKey(Integer id);

    int insertSelective(DisEquipmentHistoryInfo record);

    DisEquipmentHistoryInfo selectByPrimaryKey(Integer id);

    Page<DisEquipmentHistoryInfo> selectDisEquipmentHistory(DisEquipmentHistoryInfo record);

    int updateByPrimaryKeySelective(DisEquipmentHistoryInfo record);
}