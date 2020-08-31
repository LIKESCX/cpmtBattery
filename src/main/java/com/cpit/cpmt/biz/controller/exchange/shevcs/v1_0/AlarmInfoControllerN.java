package com.cpit.cpmt.biz.controller.exchange.shevcs.v1_0;


import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.cpmt.biz.impl.exchange.basic.AlarmInfoMgmt;
import com.cpit.cpmt.biz.utils.validate.DataSigCheck;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value={"/testShevcs/v1.0","/testShevcs/v0.9"},method = {RequestMethod.POST})
//@RequestMapping(value={"/shevcs/v1.0","/shevcs/v0.9"},method = {RequestMethod.POST})
public class AlarmInfoControllerN {
	private final static Logger logger = LoggerFactory.getLogger(AlarmInfoControllerN.class);
	@Autowired AlarmInfoMgmt alarmInfoMgmt;
	@Autowired DataSigCheck dataSigCheck;
	@ApiOperation(value = "告警信息推送的接口(充电运营商平台有设备告警事件发生时，主动上报市级平台)")
	@RequestMapping("/notification_alarmInfo")
	public Object notification_alarmInfo(HttpServletRequest request, @RequestBody String content){
		//中心平台收到推送消息的时间
		Date receivedTime = new Date();
		logger.info("notification_alarmInfo_receivedTime is:\n "+receivedTime);
		//打印接受到的推送消息
		logger.info("notification_alarmInfo_content is:\n "+content);
		String objectName="AlarmInfos";
		return alarmInfoMgmt.notificationAlarmInfo(content,receivedTime,objectName);
	}
}
