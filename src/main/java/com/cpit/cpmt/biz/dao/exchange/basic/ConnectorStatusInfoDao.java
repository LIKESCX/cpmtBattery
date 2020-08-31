package com.cpit.cpmt.biz.dao.exchange.basic;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.basic.ConnectorStatusInfo;
import com.cpit.cpmt.dto.exchange.operator.ChargeEquipmentAndConnector;

import java.util.List;

@MyBatisDao
public interface ConnectorStatusInfoDao {
    int deleteByPrimaryKey(Integer id);

    int insert(ConnectorStatusInfo record);

    int insertSelective(ConnectorStatusInfo record);

    ConnectorStatusInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ConnectorStatusInfo record);

    int updateByPrimaryKey(ConnectorStatusInfo record);

	ConnectorStatusInfo selectByConditions(@Param("operatorID")String operatorID, @Param("connectorID")String connectorID);

	//空闲
	Integer selectStationIfFree(@Param("operatorID")String operatorID,@Param("stationID")String stationID);

	//查询充电设备接口状态
    ConnectorStatusInfo getConnectorStatusInfo(String connectorID);
    
    //根据运营商ID+加场站ID获取正在充电的设备ID和枪ID集合
    Page<ChargeEquipmentAndConnector> getChargeEquipments(@Param("operatorID")String operatorID,@Param("stationID")String stationID);
    
 }
