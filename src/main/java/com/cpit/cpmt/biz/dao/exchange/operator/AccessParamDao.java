package com.cpit.cpmt.biz.dao.exchange.operator;

import java.util.List;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.operator.AccessParam;

@MyBatisDao
public interface AccessParamDao {
	
    int deleteByPrimaryKey(Integer accessId);

    int insert(AccessParam record);

    int insertSelective(AccessParam record);

    AccessParam selectByPrimaryKey(Integer accessId);

    int updateByPrimaryKeySelective(AccessParam record);

    int updateByPrimaryKey(AccessParam record);

	List<AccessParam> getAccessParamByOperatorId(String operatorId);

	Page<AccessParam> getAccessParamList(AccessParam accessParam);

	AccessParam getAccessParamByCondion(AccessParam accessParam);
}