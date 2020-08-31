package com.cpit.cpmt.biz.dao.exchange.supplement;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.supplement.SupplementInfo;

@MyBatisDao
public interface SupplementInfoDao {
public void addDto(SupplementInfo info);
public Page<SupplementInfo> search(@Param("operatorID")String operatorID,@Param("infName")String infName,@Param("startTime") String startTime,@Param("endTime") String endTime);
public Page<SupplementInfo> searchById(@Param("operatorID")String operatorID,@Param("stationID")String stationID,@Param("infName")String infName,@Param("startTime") String startTime,@Param("endTime") String endTime);
public Page<SupplementInfo> searchStationInfoById(@Param("operatorID")String operatorID,@Param("stationID")String stationID,@Param("infName")String infName,@Param("startTime") String startTime,@Param("endTime") String endTime);


/**
 * 根据oid 
 * @param operatorID
 * @param infName
 * @param missingTime
 * @return
 */
public List<SupplementInfo> getByIdTime(@Param("operatorID")String operatorID,@Param("infName")String infName,@Param("infVer")String infVer,@Param("originalTime")String originalTime);

public List<SupplementInfo> getBmsInfoTime(@Param("operatorID")String operatorID,@Param("infVer")String infVer,@Param("originalTime")String originalTime);

public SupplementInfo getById(SupplementInfo info);
/**
 * get by oid,missingtime,infName,infVer
 * @param info
 * @return
 */
public SupplementInfo getByInfo(SupplementInfo info);
public void updateSupplyResultById(SupplementInfo result);

public Page<SupplementInfo> getNeedSupply(@Param("startTime") String startTime,@Param("endTime") String endTime);

public void delDto(SupplementInfo info);
public List<SupplementInfo> getCalFail();
}
