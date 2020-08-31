package com.cpit.cpmt.biz.impl.system;

import com.cpit.common.SequenceId;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.system.PublicMonitorDao;
import com.cpit.cpmt.dto.system.PublicMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class PublicMonitorMgmt {
    @Autowired
    private PublicMonitorDao publicMonitorDao;

    public Page<PublicMonitor> selectPublicMonitorByPage(PublicMonitor publicMonitor){
        return publicMonitorDao.selectPublicMonitorByPage(publicMonitor);
    }

    public PublicMonitor selectByPrimaryKey(String id){
        return publicMonitorDao.selectByPrimaryKey(id);
    }

    @Transactional
    public void insertSelective(PublicMonitor publicMonitor){
        publicMonitor.setId(SequenceId.getInstance().getId("sysPublicMonitorId","",6));
        publicMonitor.setNetStatus(1);
        publicMonitor.setInDate(new Date());
        publicMonitorDao.insertSelective(publicMonitor);
    }

    @Transactional
    public void updateSelective(PublicMonitor publicMonitor){
        publicMonitorDao.updateSelective(publicMonitor);
    }
    
    @Transactional
    public void delete(String id){
        publicMonitorDao.deleteByPrimaryKey(id);
    }
}
