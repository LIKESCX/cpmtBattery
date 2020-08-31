package com.cpit.cpmt.biz.controller.security;

import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.security.MessageNoticeMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.security.MessageNotice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/security")
public class MessageNoticeController {

    private final static Logger logger = LoggerFactory.getLogger(MessageNoticeController.class);

    @Autowired
    private MessageNoticeMgmt messageNoticeMgmt;



    //添加短信通知
    @PostMapping(value = "/addMessageNotice")
    public ResultInfo addMessageNotice(@RequestBody MessageNotice messageNotice) {
        logger.debug("addMessageNotice,begin,messageNotice={}", messageNotice);
        try {
            messageNoticeMgmt.addMessageNotice(messageNotice);
            return new ResultInfo(ResultInfo.OK);
        } catch (Exception e) {
            logger.error("addMessageNotice error:", e);
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
        }

    }

    //获取短信通知列表
    @PostMapping(value = "/getMessageNoticeList")
    @ResponseBody
    public Object getMessageNoticeList(int pageNumber, int pageSize, @RequestBody MessageNotice messageNotice) {
        logger.debug("getMessageNoticeList begin, pageNumber={} ,pageSize={},messageNotice = {}", pageNumber, pageSize, messageNotice);
        Map<String, Serializable> map = new HashMap<String, Serializable>();
        try {
            PageHelper.startPage(pageNumber, pageSize);
            Page<MessageNotice> infoList = (Page<MessageNotice>) messageNoticeMgmt.getMessageNoticeList(messageNotice);
            PageHelper.endPage();
            map.put("infoList", infoList);
            map.put("total", infoList.getTotal());
            map.put("pages", infoList.getPages());
            map.put("pageNum", infoList.getPageNum());
            return new ResultInfo(ResultInfo.OK, map);
        } catch (Exception ex) {
            logger.error("getMessageNoticeList error", ex);
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
        }
    }

    //修改短信通知
    @PutMapping(value = "/updateMessageNotice")
    public ResultInfo updateMessageNotice(@RequestBody MessageNotice messageNotice) {
        logger.debug("updateMessageNotice,begin,param={}", messageNotice);
        try {
            messageNoticeMgmt.updateMessageNotice(messageNotice);
            return new ResultInfo(ResultInfo.OK);
        } catch (Exception e) {
            logger.error("updateMessageNotice error:", e);
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
        }
    }

    //删除短信通知
    @DeleteMapping(value = "/delMessageNotice")
    public ResultInfo delMessageNotice(Integer noticeId) {
        logger.info("delMessageNotice,begin,noticeId={}", noticeId);
        try {
            messageNoticeMgmt.delMessageNotice(noticeId);
            return new ResultInfo(ResultInfo.OK);
        } catch (Exception e) {
            logger.error("delMessageNotice error:", e);
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
        }
    }

    @PostMapping("sendMessageNoticeMessages")
    public ResultInfo sendMessageNoticeMessages(@RequestBody MessageNotice messageNotice) {
        try {
            logger.debug("sendMessageNoticeMessages messageNotice={}", messageNotice);
            ResultInfo resultInfo = messageNoticeMgmt.sendMessageNoticeMessages(messageNotice);
            return resultInfo;
        } catch (Exception e) {
            logger.error("sendMessageNoticeMessages error={}", e.getMessage());
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
        }
    }
}
