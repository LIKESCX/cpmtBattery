package com.cpit.cpmt.biz.dao.exchange.basic;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.basic.DisEquipmentInfo;
import org.apache.ibatis.annotations.Param;

@MyBatisDao
public interface DisEquipmentInfoDao {

    int deleteByPrimaryKey(Integer id);

    int insertSelective(DisEquipmentInfo record);

    DisEquipmentInfo selectByPrimaryKey(Integer id);

    DisEquipmentInfo selectByDisEquipmentId(@Param("disequipmentID") String disequipmentID, @Param("operatorID") String operatorID);

    Page<DisEquipmentInfo> selectByCondition(DisEquipmentInfo disEquipmentInfo);

    int updateByPrimaryKeySelective(DisEquipmentInfo record);
    
    int updateByPrimaryKey(DisEquipmentInfo record);
    

}