package com.cpit.cpmt.biz.dao.analyze.preprocess;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.analyze.preprocess.StationStatisticsHour;

@MyBatisDao
public interface StationStatisticsDao {
	List<StationStatisticsHour> selectAll();
	
	List<StationStatisticsHour> selectByPrimary(@Param("operatorId")String operatorId,@Param("stationId")String stationId,@Param("insertTime")String insertTime);
	
	void insertStationStatisticsHour(StationStatisticsHour stationStatisticsHour);
	
	void updateStationStatisticsHour(@Param("stationStatisticsHour")StationStatisticsHour stationStatisticsHour,@Param("operator_id")String operator_id,@Param("station_id")String station_id,@Param("insert_time")String insert_time);
	
	//更新在线时间
	void updateOnlineDurationByPrimary(@Param("onlineDuration")Double onlineDuration,@Param("operatorId")String operatorId,@Param("stationId")String stationId,@Param("insertTime")String insertTime);
	
	
	//更新充电量和充电时间
	void updateChargingDurationAndChargingCapacity(@Param("chargingDuration")Double chargingDuration,@Param("chargingCapacity")Double chargingCapacity,@Param("operatorId")String operatorId,@Param("stationId")String stationId,@Param("insertTime")String insertTime);
	
	//更新充电次数
	void updateChargingNumByPrimary(@Param("ChargingNum")int ChargingNum,@Param("operatorId")String operatorId,@Param("stationId")String stationId,@Param("insertTime")String insertTime);
	
	
	//更新类型充电时长
	void updateTypeCharging(@Param("type")int type,@Param("duration")Double durantion,@Param("operatorId")String operatorId,@Param("stationId")String stationId,@Param("insertTime")String insertTime);
}
