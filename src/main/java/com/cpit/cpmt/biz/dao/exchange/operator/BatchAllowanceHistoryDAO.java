package com.cpit.cpmt.biz.dao.exchange.operator;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.operator.BatchAllowanceHistory;

@MyBatisDao
public interface BatchAllowanceHistoryDAO {
    int deleteByPrimaryKey(String id);

    int insertSelective(BatchAllowanceHistory record);

    BatchAllowanceHistory selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(BatchAllowanceHistory record);

    Page<BatchAllowanceHistory> selectCheckedHistory(BatchAllowanceHistory record);
}