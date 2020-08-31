package com.cpit.cpmt.biz.dao.system;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.system.Power;

@MyBatisDao
public interface PowerDao {
    //int deleteByPrimaryKey(Integer id);

    //int insert(Power record);

    //int insertSelective(Power record);

    Power selectByPrimaryKey(Integer id);
    
    Power selectByPower(String name);

    //int updateByPrimaryKeySelective(Power record);

    //int updateByPrimaryKey(Power record);
    
    Page<Power> getAllPower();
    
    Page<Power> selectByCondition(Power condition);
    
    Page<Power> getPowersOfUser(String userId);

}