package com.cpit.cpmt.biz.dao.system;

import java.util.List;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.system.PublishAuditHis;

@MyBatisDao
public interface PublishAuditHisDao {
    int deleteByPrimaryKey(Integer policyHisId);

    int insert(PublishAuditHis record);

    int insertSelective(PublishAuditHis record);

    PublishAuditHis selectByPrimaryKey(Integer policyHisId);

    int updateByPrimaryKeySelective(PublishAuditHis record);

    int updateByPrimaryKey(PublishAuditHis record);

	List<PublishAuditHis> getPolicyAuditHisList(Integer processId);
}