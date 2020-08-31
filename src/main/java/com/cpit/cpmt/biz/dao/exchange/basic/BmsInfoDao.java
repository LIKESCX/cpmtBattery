package com.cpit.cpmt.biz.dao.exchange.basic;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.exchange.basic.BmsInfo;
import com.cpit.cpmt.dto.exchange.operator.EquipmentHistoryInfo;
import com.cpit.cpmt.dto.security.battery.other.AbnormalAlarmDataMiningConditions;

import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@MyBatisDao
public interface BmsInfoDao {
    int deleteByPrimaryKey(String connectorId);

    int insert(BmsInfo record);

    int insertSelective(BmsInfo record);

    BmsInfo selectByPrimaryKey(String connectorId);

    int updateByPrimaryKeySelective(BmsInfo record);

    int updateByPrimaryKey(BmsInfo record);

	void updateByConditions(BmsInfo bmsInfo);

    //充电设施发生故障时，过程信息中的所有充电设施
    List<BmsInfo> queryBmsequipmentIDList(@Param("beginTime")Date beginTime, @Param("endTime")Date endTime);

    //充电设施发生故障时，过程信息的各个参数的平均值
    List<BmsInfo> queryBmsAverageList(@Param("operatorID") String operatorID, @Param("equipmentID") String equipmentID,@Param("beginTime")Date beginTime, @Param("endTime")Date endTime);

    //bms历史数据信息
    List<BmsInfo> queryBmsHistoryList(EquipmentHistoryInfo equipmentHistoryInfo);
    
    //获取电池基本信息
	BmsInfo queryBatteryBasicInformation(String bMSCode);

	BmsInfo queryBmsInfoByAlarmInfoId(@Param("param")AbnormalAlarmDataMiningConditions param);
}