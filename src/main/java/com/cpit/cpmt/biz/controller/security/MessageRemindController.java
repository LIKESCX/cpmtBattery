package com.cpit.cpmt.biz.controller.security;

import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.security.MessageRemindMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.security.MessageRemind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/security")
public class MessageRemindController {

    private final static Logger logger = LoggerFactory.getLogger(MessageRemindController.class);

    @Autowired
    private MessageRemindMgmt messageRemindMgmt;

    //添加短信提醒
    @PostMapping(value = "/addMessageRemind")
    public ResultInfo addMessageRemind(@RequestBody MessageRemind messageRemind) {
        logger.debug("addMessageRemind,begin,messageRemind:" + messageRemind);
        try {
            messageRemindMgmt.addMessageRemind(messageRemind);
            return new ResultInfo(ResultInfo.OK);
        } catch (Exception e) {
            logger.error("addMessageRemind error:", e);
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
        }

    }

    //获取短信提醒列表
    @PostMapping(value = "/getMessageRemindList")
    @ResponseBody
    public Object getMessageRemindList(int pageNumber, int pageSize, @RequestBody MessageRemind messageRemind) {
        logger.debug("getMessageRemindList begin, pageNumber=" + pageNumber + ",pageSize=" + pageSize + ",messageRemind is :" + messageRemind);
        Map<String, Serializable> map = new HashMap<String, Serializable>();
        try {
            PageHelper.startPage(pageNumber, pageSize);
            Page<MessageRemind> infoList = messageRemindMgmt.getMessageRemindList(messageRemind);
            PageHelper.endPage();
            map.put("infoList", infoList);
            map.put("total", infoList.getTotal());
            map.put("pages", infoList.getPages());
            map.put("pageNum", infoList.getPageNum());
            return new ResultInfo(ResultInfo.OK, map);
        } catch (Exception ex) {
            logger.error("getMessageRemindList error", ex);
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
        }
    }

    //修改短信提醒
    @PutMapping(value = "/updateMessageRemind")
    public ResultInfo updateMessageRemind(@RequestBody MessageRemind messageRemind) {
        logger.debug("updateMessageRemind,begin,param:" + messageRemind);
        try {
            messageRemindMgmt.updateMessageRemind(messageRemind);
            return new ResultInfo(ResultInfo.OK);
        } catch (Exception e) {
            logger.error("updateMessageRemind error:", e);
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
        }
    }

    //删除短信提醒
    @DeleteMapping(value = "/delMessageRemind")
    public ResultInfo delMessageRemind(Integer remindId) {
        logger.info("delMessageRemind,begin,remindId:" + remindId);
        try {
            messageRemindMgmt.delMessageRemind(remindId);
            return new ResultInfo(ResultInfo.OK);
        } catch (Exception e) {
            logger.error("delMessageRemind error:", e);
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
        }
    }


}
