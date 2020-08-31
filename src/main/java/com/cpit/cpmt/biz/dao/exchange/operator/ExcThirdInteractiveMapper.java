package com.cpit.cpmt.biz.dao.exchange.operator;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.operator.ExcThirdInteractive;

@MyBatisDao
public interface ExcThirdInteractiveMapper {
    int deleteByPrimaryKey(Integer interactiveId);

    int insert(ExcThirdInteractive record);

    int insertSelective(ExcThirdInteractive record);

    ExcThirdInteractive selectByPrimaryKey(Integer interactiveId);

    int updateByPrimaryKeySelective(ExcThirdInteractive record);

    int updateByPrimaryKey(ExcThirdInteractive record);

	void deleteByAuthId(Integer authId);

	Page<ExcThirdInteractive> getExcAutListByParam(ExcThirdInteractive excThirdInteractive);
}