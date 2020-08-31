package com.cpit.cpmt.biz.dao.exchange.basic;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.exchange.basic.ConnectorChargeInfo;

@MyBatisDao
public interface ConnectorChargeInfoDao {
 public void addDto(ConnectorChargeInfo dto);
 
 public void updateEndTime(ConnectorChargeInfo dto);
 /**
  * stationId,operatorId,equipmentId,connectorId
  * @param dto
  * @return
  */
 public List<ConnectorChargeInfo> getById(ConnectorChargeInfo dto);
 
 /**
  * stationId,operatorId,equipmentId,connectorId
  * @param dto
  * @return
  */
 public ConnectorChargeInfo getUnfinished(ConnectorChargeInfo dto);
 public ConnectorChargeInfo getByIdAndStartDate(ConnectorChargeInfo dto);
 public ConnectorChargeInfo getLastUnfinished(ConnectorChargeInfo dto);
 
 public ConnectorChargeInfo getByIdForStationStatistic(ConnectorChargeInfo dto);
 /**
  * 删除7天之前的
  */
 public void deleteByInTime();
}
