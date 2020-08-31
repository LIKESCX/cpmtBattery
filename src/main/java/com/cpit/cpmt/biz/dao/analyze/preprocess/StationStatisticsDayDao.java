package com.cpit.cpmt.biz.dao.analyze.preprocess;

import java.util.List;

import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.analyze.preprocess.StationStatisticsDay;

@MyBatisDao
public interface StationStatisticsDayDao {
	List<StationStatisticsDay> selectAll();
	
	List<StationStatisticsDay> selectByPrimary(@Param("operatorId")String operatorId,@Param("stationId")String stationId,@Param("insertTime")String insertTime);
	
	void stationStatisticTask();
	//根据主键插入 (用于充电量和充电时间,主键没重复)
	void insertByPrimary(@Param("chargingCapacity")double chargingCapacity,@Param("chargingDuration")double chargingDuration,@Param("chargingNum")int chargingNum,@Param("poleNum")int poleNum,@Param("operatorId")String operatorId,@Param("stationId")String stationId,@Param("insertTime")String insertTime);
	
	//根据主键插入（用于充电量和充电时间,主键重复)
	void updateByPrimary(@Param("chargingCapacity")double chargingCapacity,@Param("chargingDuration")double chargingDuration,@Param("chargingNum")int chargingNum,@Param("operatorId")String operatorId,@Param("stationId")String stationId,@Param("insertTime")String insertTime);
	
	
	//根据主键插入(用于在线时长，主键没重复)
	void insertByPrimaryForOnlineDuration(@Param("onlineDuration")double onlineDuration,@Param("poleNum")int poleNum,@Param("operatorId")String operatorId,@Param("stationId")String stationId,@Param("insertTime")String insertTime);
	
	//根据主键插入(用于在线时长，主键重复)
	void updateByPrimaryForOnlineDuration(@Param("onlineDuration")double onlineDuration,@Param("operatorId")String operatorId,@Param("stationId")String stationId,@Param("insertTime")String insertTime);
	
	
	//根据类型更新充电时长
	
	void updateTypeChargingDay(@Param("type")int type,@Param("duration")Double durantion,@Param("operatorId")String operatorId,@Param("stationId")String stationId,@Param("insertTime")String insertTime);

	//按月份统计各指标累计值
	StationStatisticsDay selectBigScreenMonthInfo(StationInfoShow stationInfoShow);

	//按天统计各指标累计值
	StationStatisticsDay selectBigScreenDayInfo(StationInfoShow stationInfoShow);
}
