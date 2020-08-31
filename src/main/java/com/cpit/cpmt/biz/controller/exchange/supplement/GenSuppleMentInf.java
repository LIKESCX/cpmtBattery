package com.cpit.cpmt.biz.controller.exchange.supplement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/exchange/supplement")
public class GenSuppleMentInf {
	private final static Logger logger = LoggerFactory.getLogger(GenSuppleMentInf.class);
	/**
	 * 扫描补采信息表，每天执行；
	 * @return
	 */
	@RequestMapping("/executeSupplyAuto")
	public Object executeSupplyAuto() {
		return null;
		
	}
}
