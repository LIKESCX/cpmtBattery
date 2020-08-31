package com.cpit.cpmt.biz.impl.exchange.basic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpit.cpmt.biz.dao.exchange.basic.ConnectorChargeInfoDao;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConnectorChargeInfoMgmt {
	
	@Autowired
	private ConnectorChargeInfoDao connectorChargeInfoDao;

	@Transactional
	public void deleteChargeInfo() {
		connectorChargeInfoDao.deleteByInTime();
	}
}
