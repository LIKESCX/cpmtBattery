package com.cpit.cpmt.biz.dao.security.battery.other;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.security.battery.other.BatteryAnalysisRecord;
@MyBatisDao
public interface BatteryAnalysisRecordDao {

    int deleteByPrimaryKey(String id);

    int insert(BatteryAnalysisRecord record);

    int insertSelective(BatteryAnalysisRecord record);

    BatteryAnalysisRecord selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(BatteryAnalysisRecord record);

    int updateByPrimaryKey(BatteryAnalysisRecord record);
    
}