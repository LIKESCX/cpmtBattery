package com.cpit.cpmt.biz.controller.exchange.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.cpmt.biz.impl.exchange.basic.QueryFrequencyMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;

import io.swagger.annotations.ApiOperation;
/**
 * 9.14接口配置
 * @author admin
 *
 */
@RestController
@RequestMapping(value="/exchange/collect",method = {RequestMethod.POST})
public class QueryFrequencyController {
	private final static Logger logger = LoggerFactory.getLogger(QueryFrequencyController.class);
	@Autowired QueryFrequencyMgmt queryFrequencyMgmt;
	//postman测试:http://localhost:28060/exchange/collect/query_frequency?paraFrequency=5&operatorID=665866124//永联
	@ApiOperation(value = "(接口参数配置)市级安全平台可设置参数运营商平台定时上报周期频率")
	@RequestMapping("/query_frequency")
	public Object query_frequency(@RequestParam(value="operatorID",required=true)String operatorID,@RequestParam(value="paraFrequency",required=true)String paraFrequency){
		logger.info("query_frequency begin, operatorID= "+operatorID+" ,paraFrequency= "+paraFrequency);
		try {
			return new ResultInfo(ResultInfo.OK,queryFrequencyMgmt.setQueryFrequency(operatorID,paraFrequency));
		} catch (Exception ex) {
			logger.error("query_frequency:",ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
}
