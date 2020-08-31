package com.cpit.cpmt.biz.dao.system;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.system.ExportLog;

@MyBatisDao
public interface ExportLogDao {
    int deleteByPrimaryKey(String id);

    int insert(ExportLog record);

    int insertSelective(ExportLog record);

    ExportLog selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ExportLog record);

    int updateByPrimaryKeyWithBLOBs(ExportLog record);

    int updateByPrimaryKey(ExportLog record);
    
    Page<ExportLog> selectByCondition(
    	@Param(value="log") ExportLog condition
    );
    
    
}