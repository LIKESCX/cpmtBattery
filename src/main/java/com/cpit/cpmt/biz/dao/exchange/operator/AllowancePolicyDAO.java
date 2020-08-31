package com.cpit.cpmt.biz.dao.exchange.operator;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.exchange.operator.AllowancePolicy;
import com.cpit.cpmt.dto.system.PoliciesPublish;

import java.util.List;

@MyBatisDao
public interface AllowancePolicyDAO {

    int deleteByPrimaryKey(String batchId);

    int insert(AllowancePolicy record);

    int insertSelective(AllowancePolicy record);

    AllowancePolicy selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(AllowancePolicy record);

    List<PoliciesPublish> selectPolicyList(String batchId);

    int updateByPrimaryKey(AllowancePolicy record);
}