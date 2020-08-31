package com.cpit.cpmt.biz.impl.message;

import static com.cpit.cpmt.biz.config.RabbitCongfig.SMS_QUEUE_NAME;

import java.util.concurrent.TimeUnit;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cpit.cpmt.biz.utils.SmsUtil;
import com.cpit.cpmt.dto.message.ExcMessage;


@Service
public class SendMessageProcess {

	private final static Logger logger = LoggerFactory.getLogger(SendMessageProcess.class);


	public String getToken() {
		String accessToken="";
		JSONObject tokenInfo = (JSONObject) SmsUtil.getToken();
		logger.info("getToken result is:"+tokenInfo);
		if(null != tokenInfo) {
			try {
				String resultData = tokenInfo.getString("resultData");
				JSONObject resultDataObject = JSON.parseObject(resultData);
				accessToken = resultDataObject.getString("accessToken");
			}catch(Exception ex) {
			}
		}
		return accessToken;
	}
	
	@RabbitListener(queues = SMS_QUEUE_NAME)
	@RabbitHandler
	public void sendMessage(ExcMessage message) {
		logger.info("Receiver msg is:"+message);
		String token = getToken();
		String phoneNumber = message.getPhoneNumber();
		String phoneContent = message.getSubContent();
		String result = SmsUtil.sendMessage(token,phoneNumber,phoneContent);
		logger.info("sendMessage result is:"+result);
		try {
			TimeUnit.SECONDS.sleep(10); //延时，控制每分钟发送数量
		} catch (InterruptedException e) {
		}
	}
	

}
