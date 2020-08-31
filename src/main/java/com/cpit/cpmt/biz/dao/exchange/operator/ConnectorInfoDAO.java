package com.cpit.cpmt.biz.dao.exchange.operator;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.exchange.basic.ConnectorInfo;
import com.cpit.cpmt.dto.exchange.operator.ConnectorInfoShow;

import java.util.List;

import org.apache.ibatis.annotations.Param;

@MyBatisDao
public interface ConnectorInfoDAO {
    //查询充电设备下的充电接口
    List<ConnectorInfoShow> getConnectorList(@Param("equipmentId") String equipmentId,@Param("operatorId") String operatorId);

    //查询充电设备下的充电接口及状态信息
    List<ConnectorInfoShow> getConnectorStatusInfoList(@Param("equipmentId") String equipmentId,@Param("operatorId") String operatorId);


    //根据id查询接口信息
    ConnectorInfoShow getConnectorById(@Param("connectorID") String connectorID,@Param("operatorID") String operatorID);
    
    //根据id查询接口信息
    List<ConnectorInfoShow> getConnectorsByoperatorId(@Param("operatorID") String operatorID);

	int insertSelective(ConnectorInfoShow connectorInfo);
	int updateRecord(ConnectorInfoShow connectorInfo);

    int updateConnectorSelective(ConnectorInfoShow connectorInfo);

	//查询接口累计使用时间
    String selectConnectorUsedTimeSum(@Param("connectorID") String connectorID,@Param("operatorID") String operatorID);

    Integer selectConnectorNumber(@Param("stationId")String stationId,@Param("operatorId") String operatorId);

}
