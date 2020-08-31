package com.cpit.cpmt.biz.dao.monitor;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.basic.BmsInfo;
import com.cpit.cpmt.dto.monitor.EquimentMonitorCondition;

@MyBatisDao
public interface RealTimeBmsInfoDao {

	public Page<BmsInfo> queryRealTimeBmsInfoByCondition(EquimentMonitorCondition emc);
	
	public BmsInfo queryBmsDetailInfosByConditions(@Param("operatorID")String operatorID,@Param("connectorID") String connectorID);
	
	public List<BmsInfo> queryBmsGraphicDisplayInfoByConditions(@Param("operatorID")String operatorID,@Param("connectorID") String connectorID);

}
