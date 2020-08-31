package com.cpit.cpmt.biz.impl.measure;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cpit.common.SequenceId;
import com.cpit.cpmt.biz.dao.measure.ConnectorChargeDao;
import com.cpit.cpmt.dto.measure.ConnectorCharge;

@Service
public class ConnectorChargeMgmt {

	private final static Logger logger = LoggerFactory.getLogger(ConnectorChargeMgmt.class);
	
	@Autowired
	private ConnectorChargeDao connectorChargeDao;
	
	@Transactional
	public void reportConnectorCharge(ConnectorCharge connectorCharge) {
		ConnectorCharge condition = new ConnectorCharge();
		condition.setOperatorId(connectorCharge.getOperatorId());
		condition.setStationId(connectorCharge.getStationId());
		condition.setEquipmentId(connectorCharge.getEquipmentId());
		condition.setConnectorId(connectorCharge.getConnectorId());
		condition.setInTime(connectorCharge.getInTime());
		List<ConnectorCharge> list = connectorChargeDao.selectByCondition(condition);
		if(list != null && !list.isEmpty()) {
			connectorCharge.setId(list.get(0).getId());
			connectorChargeDao.updateByPrimaryKeySelective(connectorCharge);
		}else {
			int id = SequenceId.getInstance().getId("thirdConnectorChargeId");
			connectorCharge.setId(id);
			connectorChargeDao.insertSelective(connectorCharge);
		}
	}

}
