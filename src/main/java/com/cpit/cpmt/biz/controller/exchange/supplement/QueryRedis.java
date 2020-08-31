package com.cpit.cpmt.biz.controller.exchange.supplement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.cpmt.biz.dto.BmsStatusDto;
import com.cpit.cpmt.biz.utils.CacheUtil;
import com.cpit.cpmt.dto.common.ResultInfo;

@RestController
@RequestMapping(value = "/exchange/queryRedis")
public class QueryRedis {
	@Autowired CacheUtil cacheUtil;
	@RequestMapping("/queryBmsStatus")
	public ResultInfo queryBmsStatus(@RequestParam(value = "operatorID", required = true) String oid,
			@RequestParam(value = "connectorID", required = true) String cid) {
		String key = oid+"_"+cid;
		BmsStatusDto dto =cacheUtil.getBmsStatus(key);
		return new ResultInfo(ResultInfo.OK,dto);
		
	}
}
