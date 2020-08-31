package com.cpit.cpmt.biz.dao.system;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.system.OperateLog;

@MyBatisDao
public interface OperateLogDao {
    int deleteByPrimaryKey(String id);

    int insert(OperateLog record);

    int insertSelective(OperateLog record);

    OperateLog selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(OperateLog record);

    int updateByPrimaryKeyWithBLOBs(OperateLog record);

    int updateByPrimaryKey(OperateLog record);
    
    Page<OperateLog> selectByCondition(
    	@Param(value="log") OperateLog condition
    );
    
    
}