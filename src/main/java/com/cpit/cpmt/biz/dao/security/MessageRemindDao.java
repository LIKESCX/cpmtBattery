package com.cpit.cpmt.biz.dao.security;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.security.MessageRemind;

@MyBatisDao
public interface MessageRemindDao {
    int deleteByPrimaryKey(Integer remindId);

    int insertSelective(MessageRemind record);

    MessageRemind selectByPrimaryKey(Integer remindId);

    int updateByPrimaryKeySelective(MessageRemind record);

	Page<MessageRemind> getMessageRemindList(MessageRemind messageRemind);
}