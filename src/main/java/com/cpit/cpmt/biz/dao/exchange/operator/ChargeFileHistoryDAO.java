package com.cpit.cpmt.biz.dao.exchange.operator;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.operator.ChargeFileHistory;
@MyBatisDao
public interface ChargeFileHistoryDAO {

    int deleteByPrimaryKey(Integer fileId);

    int insertSelective(ChargeFileHistory record);

    ChargeFileHistory selectByPrimaryKey(Integer fileId);

    Page<ChargeFileHistory> selectChargeFileHistory(ChargeFileHistory record);

    int updateByPrimaryKeySelective(ChargeFileHistory record);
}