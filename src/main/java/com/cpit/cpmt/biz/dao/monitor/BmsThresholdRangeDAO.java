package com.cpit.cpmt.biz.dao.monitor;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.monitor.BmsAveInfo;
import com.cpit.cpmt.dto.monitor.BmsThresholdRange;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface BmsThresholdRangeDAO {

    int deleteByPrimaryKey(String id);

    int insertSelective(BmsThresholdRange record);

    BmsThresholdRange selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(BmsThresholdRange record);

    List<BmsThresholdRange> selectBmsThresholdRangeAveLastest();

    List<BmsThresholdRange> selectBmsThresholdRangeAveLastestUnEffect();

}