package com.cpit.cpmt.biz.dao.exchange.operator;

import java.util.List;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.exchange.operator.ExcThirdInterface;

@MyBatisDao
public interface ExcThirdInterfaceMapper {
    int deleteByPrimaryKey(Integer interfaceId);

    int insert(ExcThirdInterface record);

    int insertSelective(ExcThirdInterface record);

    ExcThirdInterface selectByPrimaryKey(Integer interfaceId);

    int updateByPrimaryKeySelective(ExcThirdInterface record);

    int updateByPrimaryKey(ExcThirdInterface record);

	List<ExcThirdInterface> getAllInterfaceList();
}