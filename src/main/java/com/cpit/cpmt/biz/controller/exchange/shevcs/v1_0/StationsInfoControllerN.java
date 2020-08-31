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

import com.cpit.cpmt.biz.impl.exchange.basic.StationsInfoMgmt;
import com.cpit.cpmt.biz.utils.validate.DataSigCheck;

import io.swagger.annotations.ApiOperation;

@RestController
//@RequestMapping(value={"/testShevcs/v1.0","/testShevcs/v0.9"},method = {RequestMethod.POST})
@RequestMapping(value={"/shevcs/v1.0","/shevcs/v0.9"},method = {RequestMethod.POST})
public class StationsInfoControllerN {
	private final static Logger logger = LoggerFactory.getLogger(StationsInfoControllerN.class);
	@Autowired
	DataSigCheck dataSigCheck;
	@Autowired StationsInfoMgmt stationsInfoMgmt;

	@ApiOperation(value = "9.2　充电站信息变化推送")
	@RequestMapping("/notification_stationInfo")
	public Object notification_stationInfo(HttpServletRequest request, @RequestBody String content) {
		logger.info("notification_stationInfo===>>>content:"+content);
		Date receivedTime = new Date();
		return stationsInfoMgmt.notification_stationInfo(content,receivedTime,"StationInfo");
	}
}
