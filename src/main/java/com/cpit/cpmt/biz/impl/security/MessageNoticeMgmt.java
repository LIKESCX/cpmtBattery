package com.cpit.cpmt.biz.impl.security;

import com.alibaba.fastjson.JSONArray;
import com.cpit.common.JsonUtil;
import com.cpit.common.SequenceId;
import com.cpit.cpmt.biz.dao.security.MessageNoticeDao;
import com.cpit.cpmt.biz.impl.message.MessageMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.message.ExcMessage;
import com.cpit.cpmt.dto.security.MessageNotice;
import com.cpit.cpmt.dto.security.MessageRemind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageNoticeMgmt {
    private final static Logger logger = LoggerFactory.getLogger(MessageNoticeMgmt.class);
    @Autowired
    private MessageNoticeDao messageNoticeDao;

    @Autowired
    MessageMgmt messageMgmt;

    @Transactional
    public void addMessageNotice(MessageNotice messageNotice) throws Exception {
        int id = SequenceId.getInstance().getId("noticeId");
        messageNotice.setNoticeId(id);
        messageNoticeDao.insertSelective(messageNotice);
    }

    public List<MessageNotice> getMessageNoticeList(MessageNotice messageNotice) {
        return messageNoticeDao.getMessageNoticeList(messageNotice);
    }

    @Transactional
    public void updateMessageNotice(MessageNotice messageNotice) {
        messageNoticeDao.updateByPrimaryKeySelective(messageNotice);
    }

    @Transactional
    public void delMessageNotice(Integer noticeId) {
        messageNoticeDao.deleteByPrimaryKey(noticeId);
    }

    @Transactional
    public ResultInfo sendMessageNoticeMessages(MessageNotice messageNotice) throws Exception {
        if (messageNotice != null && messageNotice.getSmsContent() != null && !messageNotice.getSmsContent().isEmpty() && messageNotice.getNoticeReceiver() != null && !messageNotice.getNoticeReceiver().isEmpty()) {
            String smsReceiver = messageNotice.getNoticeReceiver();
            JSONArray jsonArray = JSONArray.parseArray(smsReceiver);
            List<MessageRemind> messageReminds = JsonUtil.mkList(jsonArray, MessageRemind.class);
            int id = SequenceId.getInstance().getId("noticeId");
            messageNotice.setNoticeId(id);
            messageNoticeDao.insertSelective(messageNotice);
            if (messageReminds != null && !messageReminds.isEmpty()) {
                for (MessageRemind messageRemind : messageReminds) {
                    ExcMessage excMessage = new ExcMessage();
                    excMessage.setSubContent(messageNotice.getSmsContent());
                    excMessage.setSmsType(ExcMessage.TYPE_MESSAGE_NOTICE);
                    excMessage.setPhoneNumber(messageRemind.getPhoneNumber());
                    messageMgmt.sendMessage(excMessage);
                }
            }
            return new ResultInfo(ResultInfo.OK);
        } else {
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_MISS_PARAM, "参数不全"));
        }

    }
}