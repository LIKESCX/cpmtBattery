package com.cpit.cpmt.biz.dao.exchange.bigscreen;

import com.cpit.common.MyBatisDao;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@MyBatisDao
public interface ChargeDao {

    List<Map> getChargeTimesByCondition(@Param(value="startTime") Date startTime, @Param(value="endTime") Date endTime);

    List<Map> getChargeStatusByCondition(@Param(value="startTime") Date startTime, @Param(value="endTime") Date endTime, @Param(value="time")Integer time);

}
