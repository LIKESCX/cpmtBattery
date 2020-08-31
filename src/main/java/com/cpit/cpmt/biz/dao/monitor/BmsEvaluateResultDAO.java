package com.cpit.cpmt.biz.dao.monitor;

import com.cpit.cpmt.dto.monitor.BmsEvaluateResult;

import com.cpit.common.MyBatisDao;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@MyBatisDao
public interface BmsEvaluateResultDAO {

    int deleteByPrimaryKey(String id);

    int insertSelective(BmsEvaluateResult record);

    BmsEvaluateResult selectByPrimaryKey(String id);

    BmsEvaluateResult getBmsEvaluateResult(@Param("equipmentId")String equipmentId,@Param("operatorId")String operatorId);


    int updateByPrimaryKeySelective(BmsEvaluateResult record);

    int delEquipmentEvaluate(Date time);
}