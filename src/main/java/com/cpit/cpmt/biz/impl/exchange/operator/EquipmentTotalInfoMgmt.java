package com.cpit.cpmt.biz.impl.exchange.operator;

import com.cpit.cpmt.biz.dao.exchange.operator.EquipmentInfoDAO;
import com.cpit.cpmt.biz.dao.exchange.operator.EquipmentTotalInfoDAO;
import com.cpit.cpmt.dto.exchange.basic.ConnectorChargeInfo;
import com.cpit.cpmt.dto.exchange.operator.EquipmentTotalInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class EquipmentTotalInfoMgmt {
    private final static Logger logger = LoggerFactory.getLogger(EquipmentTotalInfoMgmt.class);

    @Autowired
    private EquipmentTotalInfoDAO equipmentTotalInfoDAO;

    @Transactional
    public void EquipmentTotalStatisticsOperator(ConnectorChargeInfo connectorChargeInfo){
        String operatorID = connectorChargeInfo.getOperatorID();
        String equipmentID = connectorChargeInfo.getEquipmentID();
        Double chargeElec = connectorChargeInfo.getChargeElec();
        Double disChargeElec = connectorChargeInfo.getDisChargeElec();
        Double chargeLastTime = connectorChargeInfo.getChargeLastTime();


        EquipmentTotalInfo equipmentTotalInfo = new EquipmentTotalInfo();
        equipmentTotalInfo.setOperatorId(operatorID);
        equipmentTotalInfo.setEquipmentId(equipmentID);
        EquipmentTotalInfo equipmentTotal = equipmentTotalInfoDAO.selectByPrimaryKey(equipmentTotalInfo);
        if(equipmentTotal ==null){
            equipmentTotalInfo.setStationId(connectorChargeInfo.getStationID());
            equipmentTotalInfo.setChargingCapacity(chargeElec);
            equipmentTotalInfo.setDisChargingCapacity(disChargeElec);
            equipmentTotalInfo.setChargingDuration(chargeLastTime);
            equipmentTotalInfo.setChargingNum(1);
            equipmentTotalInfoDAO.insertSelective(equipmentTotalInfo);
        }else {
            Double chargingCapacity = equipmentTotal.getChargingCapacity();
            Double disChargingCapacity = equipmentTotal.getDisChargingCapacity();
            Double chargingDuration = equipmentTotal.getChargingDuration();
            Integer chargingNum = equipmentTotal.getChargingNum();

            equipmentTotalInfo.setChargingCapacity(chargingCapacity+chargeElec);
            equipmentTotalInfo.setDisChargingCapacity(disChargingCapacity+disChargeElec);
            equipmentTotalInfo.setChargingDuration(chargingDuration+chargeLastTime);
            equipmentTotalInfo.setChargingNum(chargingNum+1);
            equipmentTotalInfo.setUpdateTime(new Date());
            equipmentTotalInfoDAO.updateByPrimaryKeySelective(equipmentTotalInfo);
        }

    }
}
