package com.cpit.cpmt.biz.dao.system;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.system.InterfacePublish;

@MyBatisDao
public interface InterfacePublishDao {
    int deleteByPrimaryKey(Integer faceId);

    int insert(InterfacePublish record);

    int insertSelective(InterfacePublish record);

    InterfacePublish selectByPrimaryKey(Integer faceId);

    int updateByPrimaryKeySelective(InterfacePublish record);

    int updateByPrimaryKey(InterfacePublish record);

	Page<InterfacePublish> getInterfacePublishList(InterfacePublish interfacePublish);
}