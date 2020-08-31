package com.cpit.cpmt.biz.dao.system;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.system.UserOperatorKey;

@MyBatisDao
public interface UserOperatorDao {
    //int deleteByPrimaryKey(UserOperatorKey key);

    int insert(UserOperatorKey record);

    int deleteByUserId(String userId);
}