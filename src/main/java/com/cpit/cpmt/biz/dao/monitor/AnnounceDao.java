package com.cpit.cpmt.biz.dao.monitor;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.monitor.Announce;

@MyBatisDao
public interface AnnounceDao {
    int deleteByPrimaryKey(String id);

    int insertSelective(Announce record);

    Announce selectByPrimaryKey(String id);
    
    int updateByPrimaryKeySelective(Announce record);

    Page<Announce> selectByCondition(Announce condition);
}
