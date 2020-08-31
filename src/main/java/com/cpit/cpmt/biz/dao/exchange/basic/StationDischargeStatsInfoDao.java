package com.cpit.cpmt.biz.dao.exchange.basic;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.exchange.basic.StationDischargeStatsInfo;
@MyBatisDao
public interface StationDischargeStatsInfoDao {
    int deleteByPrimaryKey(String id);

    int insert(StationDischargeStatsInfo record);

    int insertSelective(StationDischargeStatsInfo record);

    StationDischargeStatsInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(StationDischargeStatsInfo record);

    int updateByPrimaryKey(StationDischargeStatsInfo record);
}