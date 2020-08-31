package com.cpit.cpmt.biz.impl.exchange.operator;

import com.cpit.common.SequenceId;
import com.cpit.common.StringUtils;
import com.cpit.cpmt.biz.dao.exchange.operator.ConnectorInfoDAO;
import com.cpit.cpmt.dto.exchange.operator.ConnectorInfoShow;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConnectorMgmt {
    private final static Logger logger = LoggerFactory.getLogger(ConnectorMgmt.class);

    @Autowired
    private ConnectorInfoDAO connectorInfoDAO;

    @Autowired
    private EquipmentInfoMgmt equipmentInfoMgmt;

    //查询设备下的充电接口列表
    public List<ConnectorInfoShow> getConnectorList(String equipmentId,String operatorId){
        return connectorInfoDAO.getConnectorList(equipmentId,operatorId);
    }


    @Transactional
    public void insertSelective(ConnectorInfoShow connectorInfo){
        connectorInfo.setCid(SequenceId.getInstance().getId("cpmtConnectorId","",6));
        connectorInfoDAO.insertSelective(connectorInfo);

        //更新设备信息的总枪数gunSum
        String equipmentId = connectorInfo.getEquipmentID();
        String operatorId = connectorInfo.getOperatorID();
        if(StringUtils.isNotEmpty(equipmentId)&&StringUtils.isNotEmpty(operatorId)){
            List<ConnectorInfoShow> connectorList = connectorInfoDAO.getConnectorList(equipmentId, operatorId);
            if(connectorList!=null){
                EquipmentInfoShow equipmentInfo = new EquipmentInfoShow();
                equipmentInfo.setEquipmentID(equipmentId);
                equipmentInfo.setOperatorID(operatorId);
                equipmentInfo.setGunSum(connectorList.size());
                equipmentInfoMgmt.updateEquipmentInfo(equipmentInfo);
            }

        }
    }

    @Cacheable(cacheNames="connector-id",key="#root.caches[0].name+#connectorID+'-'+#operatorID",unless="#result == null")
    public ConnectorInfoShow getConnectorById( String connectorID,String operatorID){
        return connectorInfoDAO.getConnectorById(connectorID, operatorID);
    }

    @Transactional
    @CacheEvict(cacheNames="connector-id",key="#root.caches[0].name+#connectorInfo.connectorID+'-'+#connectorInfo.operatorID")
    public void updateRecord(ConnectorInfoShow connectorInfo){
        connectorInfoDAO.updateRecord(connectorInfo);

        /*//更新设备信息的总枪数gunSum
        String equipmentId = connectorInfo.getEquipmentId();
        String operatorId = connectorInfo.getOperatorId();
        if(StringUtils.isNotEmpty(equipmentId)&&StringUtils.isNotEmpty(operatorId)){
            List<ConnectorInfoShow> connectorList = connectorInfoDAO.getConnectorList(equipmentId, operatorId);
            if(connectorList!=null){
                EquipmentInfoShow equipmentInfo = new EquipmentInfoShow();
                equipmentInfo.setEquipmentID(equipmentId);
                equipmentInfo.setOperatorID(operatorId);
                equipmentInfo.setGunSum(connectorList.size());
                equipmentInfoMgmt.updateEquipmentInfo(equipmentInfo);
            }

        }*/
    }

    /*更新接口父类信息*/
    @Transactional
    @CacheEvict(cacheNames="connector-id",key="#root.caches[0].name+#connectorInfo.connectorID+'-'+#connectorInfo.operatorID")
    public void updateConnectorSelective(ConnectorInfoShow connectorInfo){
        connectorInfoDAO.updateConnectorSelective(connectorInfo);
    }

}
