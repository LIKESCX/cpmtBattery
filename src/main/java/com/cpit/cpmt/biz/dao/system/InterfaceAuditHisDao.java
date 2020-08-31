package com.cpit.cpmt.biz.dao.system;

import java.util.List;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.system.InterfaceAuditHis;

@MyBatisDao
public interface InterfaceAuditHisDao {
    int deleteByPrimaryKey(Integer hisId);

    int insert(InterfaceAuditHis record);

    int insertSelective(InterfaceAuditHis record);

    InterfaceAuditHis selectByPrimaryKey(Integer hisId);

    int updateByPrimaryKeySelective(InterfaceAuditHis record);

    int updateByPrimaryKey(InterfaceAuditHis record);

	List<InterfaceAuditHis> getInterfaceAuditHisList(Integer faceId);
}