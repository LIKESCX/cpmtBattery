package com.cpit.cpmt.biz.impl.exchange.operator;

import com.cpit.common.SequenceId;
import com.cpit.common.StringUtils;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.exchange.basic.DisEquipmentInfoDao;
import com.cpit.cpmt.dto.exchange.basic.DisEquipmentInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class DisEquipmentInfoMgmt {
    private final static Logger logger = LoggerFactory.getLogger(DisEquipmentInfoMgmt.class);
    @Autowired
    private DisEquipmentInfoDao disEquipmentInfoDao;

    @Autowired
    private HistoryInfoMgmt historyInfoMgmt;

    public Page<DisEquipmentInfo> selectByCondition(DisEquipmentInfo disEquipmentInfo){
        return disEquipmentInfoDao.selectByCondition(disEquipmentInfo);
    }

    public DisEquipmentInfo selectByPrimaryKey(Integer id){
        return  disEquipmentInfoDao.selectByPrimaryKey(id);
    }

    @Cacheable(cacheNames = "disequipment-id", key = "#root.caches[0].name+#disequipmentID+'-'+#operatorID", unless = "#result == null")
    public DisEquipmentInfo selectByPrimaryKey(String disequipmentID,String operatorID){
        return  disEquipmentInfoDao.selectByDisEquipmentId(disequipmentID,operatorID);
    }

    @Transactional
    public void insertDisEquipmentInfo(DisEquipmentInfo disEquipmentInfo){
        int id = SequenceId.getInstance().getId("excDisEquipmentInfoId");
        disEquipmentInfo.setId(id);
        disEquipmentInfo.setInTime(new Date());
        disEquipmentInfoDao.insertSelective(disEquipmentInfo);
        //添加配电设备历史记录
        historyInfoMgmt.insertDisEquipmentHisInfo(disEquipmentInfo);
    }

    @Transactional
    @CacheEvict(cacheNames = "disequipment-id", key = "#root.caches[0].name+#record.disequipmentID+'-'+#record.operatorID")
    public void updateByPrimaryKeySelective(DisEquipmentInfo record){
        disEquipmentInfoDao.updateByPrimaryKeySelective(record);
        //添加配电设备历史记录
        if (StringUtils.isNotEmpty(record.getOperatorID()) && StringUtils.isNotEmpty(record.getDisequipmentID())) {
            historyInfoMgmt.insertDisEquipmentHisInfo(record);
        }
    }
}
