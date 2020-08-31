package com.cpit.cpmt.biz.dao.exchange.operator;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.operator.AccessManage;

@MyBatisDao
public interface AccessManageDao {
    int deleteByPrimaryKey(String operatorId);

    int insert(AccessManage record);

    int insertSelective(AccessManage record);

    AccessManage selectByPrimaryKey(String operatorId);

    int updateByPrimaryKeySelective(AccessManage record);

    int updateByPrimaryKeyWithBLOBs(AccessManage record);

    int updateByPrimaryKey(AccessManage record);

	Page<AccessManage> getAccessManageList(AccessManage accessManage);
}