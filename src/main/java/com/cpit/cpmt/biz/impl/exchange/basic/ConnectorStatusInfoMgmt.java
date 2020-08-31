package com.cpit.cpmt.biz.impl.exchange.basic;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cpit.cpmt.biz.dao.exchange.basic.ConnectorStatusInfoDao;
import com.cpit.cpmt.dto.exchange.basic.ConnectorStatusInfo;

@Service
public class ConnectorStatusInfoMgmt {
@Autowired ConnectorStatusInfoDao connectorStatusInfoDao;

@Transactional
public boolean insertList(List<ConnectorStatusInfo> connectorStatusInfos) {
	for(ConnectorStatusInfo record:connectorStatusInfos) {
		connectorStatusInfoDao.insert(record);
	}
	return true;
}

public void insert(ConnectorStatusInfo record) {
	connectorStatusInfoDao.insert(record);
}
}
