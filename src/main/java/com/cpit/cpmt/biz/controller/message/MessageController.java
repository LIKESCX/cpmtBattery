package com.cpit.cpmt.biz.controller.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.cpmt.biz.impl.message.MessageMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.message.ExcMessage;



@RestController
@RequestMapping(value="/message")
public class MessageController {

	private final static Logger logger = LoggerFactory.getLogger(MessageController.class);
	
	@Autowired
	private MessageMgmt messageMgmt;
	
	@PostMapping("/sendMessage")
	public Object sendMessage(@RequestBody ExcMessage message) {
		logger.info("sendMessage begin,message is:"+message);
		try {
			if(messageMgmt.ifProduceCheckCodeJustNow(message.getPhoneNumber())){
				return new ResultInfo(ResultInfo.FAIL,new ErrorMsg(ErrorMsg.ERR_BIZ_ERROR,"该手机号码已发过验证码,还有效"));
			}
			messageMgmt.sendMessage(message);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception ex) {
			logger.error("sendMessage error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	@PostMapping("/validateRandomCode")
	public Object validateRandomCode(@RequestBody ExcMessage message) {
		logger.info("validateRandomCode begin,message is:"+message);
		try {
			return messageMgmt.validateRandomCode(message);
		} catch (Exception ex) {
			logger.error("validateRandomCode error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}

}
