package com.cpit.cpmt.biz.dao.system;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.system.PublicMonitor;

@MyBatisDao
public interface PublicMonitorDao {
    PublicMonitor selectByPrimaryKey(String id);

    Page<PublicMonitor> selectPublicMonitorByPage(PublicMonitor publicMonitor);

    void updateSelective(PublicMonitor publicMonitor);

    void insertSelective(PublicMonitor publicMonitor);
    
    void deleteByPrimaryKey(String id);
}
