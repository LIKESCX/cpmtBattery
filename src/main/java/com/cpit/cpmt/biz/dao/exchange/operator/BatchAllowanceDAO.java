package com.cpit.cpmt.biz.dao.exchange.operator;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.operator.BatchAllowance;

@MyBatisDao
public interface BatchAllowanceDAO {

    int deleteByPrimaryKey(String batchId);

    int insertSelective(BatchAllowance record);

    BatchAllowance selectByPrimaryKey(String batchId);

    int updateByPrimaryKeySelective(BatchAllowance record);

    Page<BatchAllowance> selectAllowanceInfo(BatchAllowance record);
}