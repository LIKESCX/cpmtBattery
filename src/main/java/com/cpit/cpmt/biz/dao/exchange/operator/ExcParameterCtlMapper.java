package com.cpit.cpmt.biz.dao.exchange.operator;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.operator.ExcParameterCtl;

@MyBatisDao
public interface ExcParameterCtlMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ExcParameterCtl record);

    int insertSelective(ExcParameterCtl record);

    ExcParameterCtl selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ExcParameterCtl record);

    int updateByPrimaryKey(ExcParameterCtl record);

	Page<ExcParameterCtl> selectByParam(ExcParameterCtl excParameterCtl);
}