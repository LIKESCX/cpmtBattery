package com.cpit.cpmt.biz.dao.exchange.basic;

import java.util.List;
import java.util.Map;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.basic.BasicReportMsgInfo;
import com.cpit.cpmt.dto.exchange.basic.SupplyCollect;
@MyBatisDao
public interface BasicReportMsgInfoDao {
    int insert(BasicReportMsgInfo record);

    int insertSelective(BasicReportMsgInfo record);

}