package com.cpit.cpmt.biz.dao.security;

import java.util.List;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.security.DangerAuditHis;

@MyBatisDao
public interface DangerAuditHisDao {
    int deleteByPrimaryKey(Integer dangerHisId);

    int insert(DangerAuditHis record);

    int insertSelective(DangerAuditHis record);

    DangerAuditHis selectByPrimaryKey(Integer dangerHisId);

    int updateByPrimaryKeySelective(DangerAuditHis record);

    int updateByPrimaryKey(DangerAuditHis record);
    
    List<DangerAuditHis> getDangerAuditHisList(Integer dangerId);
}