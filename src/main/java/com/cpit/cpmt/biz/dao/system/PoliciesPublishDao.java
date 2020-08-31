package com.cpit.cpmt.biz.dao.system;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.system.PoliciesPublish;

@MyBatisDao
public interface PoliciesPublishDao {
    int deleteByPrimaryKey(Integer policyId);

    int insert(PoliciesPublish record);

    int insertSelective(PoliciesPublish record);

    PoliciesPublish selectByPrimaryKey(Integer policyId);

    int updateByPrimaryKeySelective(PoliciesPublish record);

    int updateByPrimaryKeyWithBLOBs(PoliciesPublish record);

    int updateByPrimaryKey(PoliciesPublish record);

	Page<PoliciesPublish> getPoliciesPublishList(PoliciesPublish policiesPublish);

    Page<PoliciesPublish> getPoliciesPublishesByType(PoliciesPublish policiesPublish);
}