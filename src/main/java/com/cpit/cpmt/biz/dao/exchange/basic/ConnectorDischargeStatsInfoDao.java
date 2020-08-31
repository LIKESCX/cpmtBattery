package com.cpit.cpmt.biz.dao.exchange.basic;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.exchange.basic.ConnectorDischargeStatsInfo;
@MyBatisDao
public interface ConnectorDischargeStatsInfoDao {
    int deleteByPrimaryKey(String connectorId);

    int insert(ConnectorDischargeStatsInfo record);

    int insertSelective(ConnectorDischargeStatsInfo record);

    ConnectorDischargeStatsInfo selectByPrimaryKey(String connectorId);

    int updateByPrimaryKeySelective(ConnectorDischargeStatsInfo record);

    int updateByPrimaryKey(ConnectorDischargeStatsInfo record);

    Double selectConnectDisCharge(String connectorId);

}