package com.cpit.cpmt.biz.dao.message;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.message.ExcMessage;

@MyBatisDao
public interface ExcMessageDao {
    int deleteByPrimaryKey(Integer smsId);

    int insert(ExcMessage record);

    int insertSelective(ExcMessage record);

    ExcMessage selectByPrimaryKey(Integer smsId);

    int updateByPrimaryKeySelective(ExcMessage record);

    int updateByPrimaryKey(ExcMessage record);

    ExcMessage getLastedCheckCodeMsg(String phoneNum);

	public ExcMessage queryMessageRecord(ExcMessage excMessage);

	//根据查询条件查询一天内发了几条短信
	Integer queryMessageOneDay(ExcMessage excMessage);
}