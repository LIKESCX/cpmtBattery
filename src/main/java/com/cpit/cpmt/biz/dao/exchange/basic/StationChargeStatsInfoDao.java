package com.cpit.cpmt.biz.dao.exchange.basic;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.exchange.basic.StationChargeStatsInfo;
@MyBatisDao
public interface StationChargeStatsInfoDao{
    int deleteByPrimaryKey(String id);

    int insert(StationChargeStatsInfo record);

    int insertSelective(StationChargeStatsInfo record);

    StationChargeStatsInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(StationChargeStatsInfo record);

    int updateByPrimaryKey(StationChargeStatsInfo record);
    
    StationChargeStatsInfo get(StationChargeStatsInfo info);
    
    void updateStats(StationChargeStatsInfo info);
}