package com.cpit.cpmt.biz.dao.exchange.operator;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.operator.StationHistoryInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface StationHistoryInfoDAO {

    int insertSelective(StationHistoryInfo record);

    StationHistoryInfo selectByPrimaryKey(String sID);

    //用operatorid ,operatorHisID分别去查接口 不管空否
    Page<StationHistoryInfo> selectStationHistory(StationHistoryInfo record);

    int updateByPrimaryKeySelective(StationHistoryInfo record);

    //查询最新的一条信息
    StationHistoryInfo selectStationHisNewestOne(@Param("stationID") String stationID, @Param("operatorID") String operatorID);
}