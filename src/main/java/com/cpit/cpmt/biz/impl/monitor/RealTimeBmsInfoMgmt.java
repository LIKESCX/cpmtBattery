package com.cpit.cpmt.biz.impl.monitor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.monitor.RealTimeBmsInfoDao;
import com.cpit.cpmt.dto.exchange.basic.BmsInfo;
import com.cpit.cpmt.dto.monitor.EquimentMonitorCondition;

@Service
public class RealTimeBmsInfoMgmt {
	@Autowired RealTimeBmsInfoDao realTimeBmsInfoDao;
	public Page<BmsInfo> queryRealTimeBmsInfo(EquimentMonitorCondition emc) {
		return realTimeBmsInfoDao.queryRealTimeBmsInfoByCondition(emc);
	}
	public BmsInfo queryBmsRealDtailInfo(String operatorID, String connectorID) {
		return realTimeBmsInfoDao.queryBmsDetailInfosByConditions(operatorID,connectorID);
	}
	
	public List<BmsInfo> queryBmsGraphicDisplayInfo(String operatorID, String connectorID) {
		return realTimeBmsInfoDao.queryBmsGraphicDisplayInfoByConditions(operatorID,connectorID);
	}

}
