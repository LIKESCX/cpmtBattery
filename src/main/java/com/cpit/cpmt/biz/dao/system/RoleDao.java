package com.cpit.cpmt.biz.dao.system;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.system.Role;

@MyBatisDao
public interface RoleDao {
    int deleteByPrimaryKey(String id);

    //int insert(Role record);

    int insertSelective(Role record);

    Role selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Role record);

    //int updateByPrimaryKey(Role record);
    
    Page<Role> selectByCondition(Role condition);

    
}