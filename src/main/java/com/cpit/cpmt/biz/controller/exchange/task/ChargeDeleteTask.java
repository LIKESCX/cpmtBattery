package com.cpit.cpmt.biz.controller.exchange.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.cpmt.biz.impl.exchange.basic.ConnectorChargeInfoMgmt;
import com.cpit.cpmt.dto.common.ResultInfo;



@RestController
public class ChargeDeleteTask {
	
	private final static Logger logger = LoggerFactory.getLogger(ChargeDeleteTask.class);
	
	@Autowired
	private ConnectorChargeInfoMgmt connectorChargeInfoMgmt;
	
	@RequestMapping("/connectorChargeInfo/delete")
	public ResultInfo connectorChargeInfoDelete() {
		logger.info("Biz connectorChargeInfoDelete");
		try {
			connectorChargeInfoMgmt.deleteChargeInfo();
			return new ResultInfo(ResultInfo.OK,"删除成功!");
		}catch(Exception e) {
			logger.error(e.getMessage());
			return new ResultInfo(ResultInfo.FAIL,"删除失败");
		}
	}

}
