package com.cpit.cpmt.biz.impl.security;

import com.cpit.common.SequenceId;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.security.MessageRemindDao;
import com.cpit.cpmt.dto.security.MessageRemind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageRemindMgmt {

    @Autowired
    private MessageRemindDao messageRemindDao;

    private final static Logger logger = LoggerFactory.getLogger(MessageRemindMgmt.class);

    @Transactional
    public void addMessageRemind(MessageRemind messageRemind) {
        int id = SequenceId.getInstance().getId("remindId");
        messageRemind.setRemindId(id);
        messageRemindDao.insertSelective(messageRemind);
    }

    public Page<MessageRemind> getMessageRemindList(MessageRemind messageRemind) {
        return messageRemindDao.getMessageRemindList(messageRemind);
    }

    @Transactional
    public void updateMessageRemind(MessageRemind messageRemind) {
        messageRemindDao.updateByPrimaryKeySelective(messageRemind);
    }

    @Transactional
    public void delMessageRemind(Integer remindId) {
        messageRemindDao.deleteByPrimaryKey(remindId);
    }


}
