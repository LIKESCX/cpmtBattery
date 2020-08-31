package com.cpit.cpmt.biz.dao.exchange.basic;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.exchange.basic.ConnectorOnlineInfo;;
@MyBatisDao
public interface ConnectorOnlineInfoDao {
	 public void addDto(ConnectorOnlineInfo dto);
	 
	 public void updateEndTime(ConnectorOnlineInfo dto);
	 /**
	  * stationId,operatorId,equipmentId,connectorId
	  * @param dto
	  * @return
	  */
	 public List<ConnectorOnlineInfo> getById(ConnectorOnlineInfo dto);
	 
	 /**
	  * stationId,operatorId,equipmentId,connectorId
	  * @param dto
	  * @return
	  */
	 public ConnectorOnlineInfo getUnfinished(ConnectorOnlineInfo dto);
	 public ConnectorOnlineInfo getByIdAndStartDate(ConnectorOnlineInfo dto);
	 public ConnectorOnlineInfo getLastUnfinished(ConnectorOnlineInfo dto);
	 
	 //统计前一天的在线时长
	 public List<ConnectorOnlineInfo> selectYesterday();
}
