package com.cpit.cpmt.biz.dao.exchange.basic;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.basic.EventInfo;
import com.cpit.cpmt.dto.exchange.basic.EventItem;
@MyBatisDao
public interface EventInfoDao {
    int deleteByPrimaryKey(Integer id);

    int insert(EventInfo record);

    int insertSelective(EventInfo record);

    EventInfo selectByPrimaryKey(Integer id);

    //页面分页接口
   Page<EventInfo> selectByCondition(EventInfo event);

    int updateByPrimaryKeySelective(EventInfo record);

    int updateByPrimaryKey(EventInfo record);

	void batchInsert(List<EventInfo> eventInfoList);

	public EventItem checkCurrentEventValid(@Param("eventCode")Integer eventCode,@Param("eventType")Integer eventType);
}