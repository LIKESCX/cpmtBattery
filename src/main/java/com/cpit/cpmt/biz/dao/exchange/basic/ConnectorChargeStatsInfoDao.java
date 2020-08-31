package com.cpit.cpmt.biz.dao.exchange.basic;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.exchange.basic.ConnectorChargeStatsInfo;
@MyBatisDao
public interface ConnectorChargeStatsInfoDao {
    int deleteByPrimaryKey(String connectorId);

    int insert(ConnectorChargeStatsInfo record);

    int insertSelective(ConnectorChargeStatsInfo record);

    ConnectorChargeStatsInfo selectByPrimaryKey(String connectorId);

    int updateByPrimaryKeySelective(ConnectorChargeStatsInfo record);

    int updateByPrimaryKey(ConnectorChargeStatsInfo record);
    ConnectorChargeStatsInfo getbyFK(@Param("stationOrder") String stationOrder,@Param("equipmentID") String equipmentID,@Param("connectorID") String connectorID);
    
    void updateChargeStats(ConnectorChargeStatsInfo record);

    Double selectConnectCharge(String connectorId);

}