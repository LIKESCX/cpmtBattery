package com.cpit.cpmt.biz.dao.exchange.supplement;

import org.apache.ibatis.annotations.Param;

import com.cpit.cpmt.biz.common.MyBatisDao;
import com.cpit.cpmt.biz.common.Page;
import com.cpit.cpmt.biz.dto.SupplementLog;

@MyBatisDao
public interface SupplementLogDao {
public void addDto(SupplementLog log);
public Page<SupplementLog> search(@Param("condition")SupplementLog condition,@Param("startTime")String startTime,@Param("endTime")String endTime);
public Page<SupplementLog> searchById(@Param("supplyID")String supplyId);


}
