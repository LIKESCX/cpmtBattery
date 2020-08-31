package com.cpit.cpmt.biz.dao.system;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.system.UserAreaKey;

@MyBatisDao
public interface UserAreaDao {
    //int deleteByPrimaryKey(UserAreaKey key);

    int insert(UserAreaKey record);

    //int insertSelective(UserAreaKey record);
    
    int deleteByUserId(String userId);
}