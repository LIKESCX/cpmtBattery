package com.cpit.cpmt.biz.dao.exchange.operator;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.basic.OperatorInfo;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;

@MyBatisDao
public interface OperatorInfoDao {
	int deleteByPrimaryKey(String operatorId);

	int insert(OperatorInfoExtend record);

	int insertSelective(OperatorInfoExtend operatorInfoExtend);

	OperatorInfoExtend selectByPrimaryKey(String operatorId);

	int updateByPrimaryKeySelective(OperatorInfoExtend operatorInfoExtend);

	int updateByPrimaryKey(OperatorInfo record);

	Page<OperatorInfoExtend> getOperatorInfoList(OperatorInfoExtend operatorInfoExtend);

	Page<OperatorInfoExtend> getOperatorListWithStationCount(OperatorInfoExtend operatorInfoExtend);

	List<OperatorInfoExtend> getAuditPassOperatorList();

	List<OperatorInfoExtend> getTotalElectric(String operatorId);

	List<EquipmentInfoShow> getTotalAllowance(String operatorId);

	OperatorInfoExtend getTotalPower(String operatorId);

	List getChargeInDay(Map<String,String> map);

	List getAllowancePriceInMonth(Map<String, String> map);
	
	List getPowerInMonth(Map<String, String> map);


	OperatorInfoExtend getOperatorInfoByName(String operatorName);

	List<OperatorInfoExtend> getOperatorWithAccess();

	List<String> getStationOperatorListByArea(OperatorInfoExtend operatorInfoExtend);

	List<OperatorInfoExtend> getOperatorTotalEquipment();

	List<OperatorInfoExtend> getOperatorTotalCharge();

	List<OperatorInfoExtend> getAreaTotalEquipment();

	List<OperatorInfoExtend> getAreaTotalCharge();

}