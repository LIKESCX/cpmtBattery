package com.cpit.cpmt.biz.dao.system;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.system.UserStreetKey;

@MyBatisDao
public interface UserStreetDao {
    int insert(UserStreetKey record);

    int deleteByUserId(String userId);
}