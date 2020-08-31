package com.cpit.cpmt.biz.dao.exchange.operator;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.operator.ExcThirdAuthentication;

@MyBatisDao
public interface ExcThirdAuthenticationMapper {
    int deleteByPrimaryKey(Integer authId);

    int insert(ExcThirdAuthentication record);

    int insertSelective(ExcThirdAuthentication record);

    ExcThirdAuthentication selectByPrimaryKey(Integer authId);

    int updateByPrimaryKeySelective(ExcThirdAuthentication record);

    int updateByPrimaryKey(ExcThirdAuthentication record);

	Page<ExcThirdAuthentication> selectByParam(ExcThirdAuthentication excThirdAuthentication);

}