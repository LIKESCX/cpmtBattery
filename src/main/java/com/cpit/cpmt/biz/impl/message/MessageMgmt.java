package com.cpit.cpmt.biz.impl.message;

import static com.cpit.cpmt.dto.message.ExcMessage.TYPE_CHECK_CODE;

import java.util.Date;
import java.util.Random;

import com.cpit.cpmt.biz.utils.validate.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import com.cpit.common.SequenceId;
import com.cpit.cpmt.biz.config.RabbitCongfig;
import com.cpit.cpmt.biz.dao.message.ExcMessageDao;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.message.ExcMessage;


@Service
@RefreshScope
public class MessageMgmt {

    private final static Logger logger = LoggerFactory.getLogger(MessageMgmt.class);

    //private static ExecutorService msgProThreadPool = Executors.newFixedThreadPool(25);


    @Autowired
    private ExcMessageDao messageDao;

    @Value("${sms.switch}")
    private String smsSwitch;

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void sendMessage(ExcMessage message) {
        //校验手机号码
        if(!Util.validatePhoneNumber(message.getPhoneNumber())){ //手机号码非法
            logger.info("message["+message.getPhoneNumber()+", content="+message.getSubContent()+"] phoneNum is invalid so not to send");
            return;
        }
        Date now = new Date();
        Date expDate = null;
        String randomCode = "";
        String content = message.getSubContent();
        if (message.getSmsType() == TYPE_CHECK_CODE) {
            for (int i = 0; i < 6; i++) {
                Random random = new Random();
                randomCode += random.nextInt(10);
            }
            message.setCheckCode(randomCode);
            content = "您的短信验证码为：" + randomCode + "请在五分钟之内输入，过期失效。";
            expDate = new Date(now.getTime() + 5 * 60000);
            message.setExpDate(expDate);
        }
        int id = SequenceId.getInstance().getId("smsId");
        message.setSmsId(id);
        message.setSubContent(content);
        message.setSendTime(now);
        messageDao.insertSelective(message);
        if ("on".equals(smsSwitch)) {
            amqpTemplate.convertAndSend(RabbitCongfig.SMS_QUEUE_NAME, message);
        } else {
            logger.info("--------smsSwitch is :" + smsSwitch);
        }
    }

    public Object validateRandomCode(ExcMessage message) throws Exception {
        ExcMessage lastedMessage = messageDao.getLastedCheckCodeMsg(message.getPhoneNumber());
        if (lastedMessage == null) {
            return new ResultInfo(ResultInfo.FAIL, "该手机号无对应的验证码");
        }
        Date now = new Date();
        String checkCode = message.getCheckCode();
        String trueCode = lastedMessage.getCheckCode();
        Date trueExpDate = lastedMessage.getExpDate();
        if (checkCode.equals(trueCode)) {
            if (now.before(trueExpDate)) {
                return new ResultInfo(ResultInfo.OK, "验证成功");
            } else {
                return new ResultInfo(ResultInfo.FAIL, "验证码超时");
            }
        } else {
            return new ResultInfo(ResultInfo.FAIL, "验证码错误");
        }
    }

    //只是插入短信表,不发送短信
    public void addMessageRecord(ExcMessage message) {
        int id = SequenceId.getInstance().getId("smsId");
        message.setSmsId(id);
        messageDao.insertSelective(message);
    }

    public ExcMessage queryMessageRecord(ExcMessage excMessage) {
        return messageDao.queryMessageRecord(excMessage);
    }

	//验证是否刚产生验证码
	// true: 已有， false: 还没有
	public boolean ifProduceCheckCodeJustNow(String phoneNum){
		ExcMessage lastedMessage = messageDao.getLastedCheckCodeMsg(phoneNum);
		if (lastedMessage == null || lastedMessage.getSmsType() != TYPE_CHECK_CODE) {
			return false;
		}
		Date now = new Date();
		Date trueExpDate = lastedMessage.getExpDate();
		if (now.before(trueExpDate)) { //已有，就不用再产生
			return true;
		}
    	return false;
	}
}
