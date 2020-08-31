package com.cpit.cpmt.biz.controller.exchange.task;


import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.dao.exchange.basic.BasicReportMsgInfoDao;
import com.cpit.cpmt.biz.utils.CacheUtil;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value="/exchange/collect")
public class ChargeEndTask {
	private final static Logger logger = LoggerFactory.getLogger(CacheUtil.class);
	
	@ApiOperation(value = "判断有没有充电结束的")
	@RequestMapping("/chargeEnd")
	public void chargeEndTask(){
		
	}
	

}
