package com.cpit.cpmt.biz.dao.exchange.basic;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.exchange.basic.AlarmTimesStatistics;
@MyBatisDao
public interface AlarmTimesStatisticsDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AlarmTimesStatistics record);

    int insertSelective(AlarmTimesStatistics record);

    AlarmTimesStatistics selectByPrimaryKey(Integer id);

    //int updateByPrimaryKeySelective(AlarmTimesStatistics record);

    int updateByPrimaryKey(AlarmTimesStatistics record);

	AlarmTimesStatistics selectByCondition(AlarmTimesStatistics alarmTimesStatistics);
}


