package com.cpit.cpmt.biz.dao.exchange.operator;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.operator.ExcOperFlow;
import com.cpit.cpmt.dto.exchange.operator.ExcThirdAuthentication;

@MyBatisDao
public interface ExcOperFlowMapper {
    int deleteByPrimaryKey(String operatorId);

    int insert(ExcOperFlow record);

    int insertSelective(ExcOperFlow record);

    ExcOperFlow selectByPrimaryKey(String operatorId);

    int updateByPrimaryKeySelective(ExcOperFlow record);

    int updateByPrimaryKey(ExcOperFlow record);

	Page<ExcOperFlow> getOperFlowByParam(ExcOperFlow excOperFlow);
}