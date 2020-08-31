package com.cpit.cpmt.biz.dao.security;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.security.MessageNotice;

import java.util.List;

/**
 * @author : xuqingxun
 * @className: MessageNoticeDao
 * @description: TODO
 * @time: 2020/4/27 3:13 下午
 */
@MyBatisDao
public interface MessageNoticeDao {
    int deleteByPrimaryKey(Integer noticeId);

    int insertSelective(MessageNotice record);

    MessageNotice selectByPrimaryKey(Integer noticeId);

    int updateByPrimaryKeySelective(MessageNotice record);

    List<MessageNotice> getMessageNoticeList(MessageNotice record);

    int batchInsertMessageNotice(List<MessageNotice> list);

}