package com.cpit.cpmt.biz.dao.system;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.system.RolePower;

@MyBatisDao
public interface RolePowerDao {
    //int deleteByPrimaryKey(RolePower key);

    //int insert(RolePower record);

    int insertSelective(RolePower record);
    
    int deleteByRoleId(String roleId);
}