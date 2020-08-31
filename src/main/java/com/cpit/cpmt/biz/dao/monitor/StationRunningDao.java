package com.cpit.cpmt.biz.dao.monitor;

import java.util.Map;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.monitor.StationRunning;

@MyBatisDao
public interface StationRunningDao {

	//获取充电次数
	public Page<StationRunning> getCharge(Map condition);
	
	//获取告警次数
	public Page<StationRunning> getAlarm(Map condition);

}
