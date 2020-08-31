package com.cpit.cpmt.biz.controller.exchange.basic;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.exchange.basic.BasicReportMsgMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.basic.BasicReportMsgInfo;
import com.cpit.cpmt.dto.exchange.basic.SupplyCollect;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value="/exchange/collect")
public class BasicReportMsgController {
	private final static Logger logger = LoggerFactory.getLogger(BasicReportMsgController.class);
	
	@Autowired BasicReportMsgMgmt basicReportMsgMgmt;
	
	
/*
	public ResultInfo getBasicReportMsgList(@RequestParam(name="pageNumber") Integer pageNumber,
	    @RequestParam(name="pageSize") Integer pageSize,@RequestBody SupplyCollect supplyCollect) {
		logger.info("cpmtBiz_getBasicReportMsg_begin_param:pageNumber="+pageNumber+",pageSize="+pageSize+",supplyCollect="+supplyCollect);
		Map<String, Serializable> map = new HashMap<String, Serializable>();
		try {
			PageHelper.startPage(pageNumber, pageSize);
			Page<BasicReportMsgInfo> infoList = basicReportMsgMgmt.getBasicReportMsgList(supplyCollect);
			PageHelper.endPage();
			map.put("infoList", infoList);
			map.put("total", infoList.getTotal());
			map.put("pages", infoList.getPages());
			map.put("pageNum", infoList.getPageNum());
			return new ResultInfo(ResultInfo.OK, map);
		} catch (Exception ex) {
			logger.error("getBasicReportMsgList error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	*/
}
