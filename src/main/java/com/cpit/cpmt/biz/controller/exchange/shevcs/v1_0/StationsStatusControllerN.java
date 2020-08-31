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

import com.cpit.cpmt.biz.impl.exchange.basic.StationStatusInfoMgmt;
import com.cpit.cpmt.biz.utils.validate.DataSigCheck;

import io.swagger.annotations.ApiOperation;

@RestController
//@RequestMapping(value={"/testShevcs/v1.0","/testShevcs/v0.9"},method = {RequestMethod.POST})
@RequestMapping(value={"/shevcs/v1.0","/shevcs/v0.9"},method = {RequestMethod.POST})
public class StationsStatusControllerN {
	private final static Logger logger = LoggerFactory.getLogger(StationsStatusControllerN.class);

	@Autowired StationStatusInfoMgmt stationStatusInfoMgmt;
	@Autowired DataSigCheck dataSigCheck;
	
	@ApiOperation(value = "设备状态变化推送")
	@RequestMapping("/notification_stationStatus")
	public Object notification_stationStatus(HttpServletRequest request, @RequestBody String content) {
		logger.info("notification_stationStatus===>>>content:"+content);
		Date receivedTime =new Date();
		return stationStatusInfoMgmt.notification_stationStatus(content,receivedTime,"ConnectorStatusInfo");
	}

}
