package com.cpit.cpmt.biz.controller.security.battery.work;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.cpmt.biz.impl.security.battery.work.BmsCodesListMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;

@RestController
@RequestMapping("/security/battery/")
public class BmsCodesListController {
	private final static Logger logger = LoggerFactory.getLogger(BmsCodesListController.class);
	@Autowired private BmsCodesListMgmt bmsCodesListMgmt;
	//给电池报告中的BMS编码下拉框提供数据
	@RequestMapping("queryBmsCodesDataLaster10")
	public ResultInfo queryBmsCodesDataLaster10() {
		logger.debug("queryBmsCodesDataLaster10_begin");
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			List<String> infoList = bmsCodesListMgmt.queryBmsCodesDataLaster10();
			map.put("infoList", infoList);
			return new ResultInfo(ResultInfo.OK,map);
		} catch (Exception e) {
			logger.error("queryBmsCodesDataLaster10_error"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	//给电池报告中的BMS编码下拉框提供模糊查询
	@RequestMapping("queryReportBmsCodeList")
	public ResultInfo queryReportBmsCodeList(String bmsCode) {
		logger.debug("queryReportBmsCodeList_begin");
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			List<String> infoList = bmsCodesListMgmt.queryReportBmsCodeList(bmsCode);
			map.put("infoList", infoList);
			return new ResultInfo(ResultInfo.OK,map);
		} catch (Exception e) {
			logger.error("queryBmsCodesDataLaster10_error"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
}
