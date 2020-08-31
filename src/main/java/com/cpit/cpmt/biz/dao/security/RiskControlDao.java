package com.cpit.cpmt.biz.dao.security;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.security.RiskControl;

import java.util.List;

@MyBatisDao
public interface RiskControlDao {
    int deleteByPrimaryKey(Integer riskId);


    int insertSelective(RiskControl record);

    RiskControl selectByPrimaryKey(Integer riskId);

    int updateByPrimaryKeySelective(RiskControl record);

	Page<RiskControl> getRiskControlList(RiskControl riskControl);

	List<RiskControl> getCountByLevel();

	List<RiskControl> getCountByType();

	Integer getCountByRiskControl(RiskControl riskControl);

	int getPendingCount(String operatorId);
}