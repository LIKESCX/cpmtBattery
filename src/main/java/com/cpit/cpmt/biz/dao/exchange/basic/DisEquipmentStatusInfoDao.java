package com.cpit.cpmt.biz.dao.exchange.basic;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.exchange.basic.DisEquipmentStatusInfo;
@MyBatisDao
public interface DisEquipmentStatusInfoDao {

    int deleteByPrimaryKey(Integer id);

    int insert(DisEquipmentStatusInfo record);

    int insertSelective(DisEquipmentStatusInfo record);

    DisEquipmentStatusInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DisEquipmentStatusInfo record);

    int updateByPrimaryKey(DisEquipmentStatusInfo record);

	public  DisEquipmentStatusInfo selectDisequipmentstatusInfoByConditons(@Param("disequipmentID")String disequipmentID, @Param("operatorID")String operatorID);
}