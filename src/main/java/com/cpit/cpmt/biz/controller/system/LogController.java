package com.cpit.cpmt.biz.controller.system;

import static com.cpit.cpmt.dto.common.ErrorMsg.ERR_SYSTEM_ERROR;
import static com.cpit.cpmt.dto.common.ResultInfo.FAIL;
import static com.cpit.cpmt.dto.common.ResultInfo.OK;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.system.ExportLogMgmt;
import com.cpit.cpmt.biz.impl.system.LogMgmt;
import com.cpit.cpmt.biz.impl.system.SessionMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.system.ExportLog;
import com.cpit.cpmt.dto.system.LoginSession;
import com.cpit.cpmt.dto.system.OperateLog;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags = "操作日志")
@RestController
@RequestMapping(value="/system")
public class LogController { // 日志管理
	private final static Logger logger = LoggerFactory.getLogger(LogController.class);

	@Autowired
	private LogMgmt logMgmt;
	
	@Autowired
	private SessionMgmt sessionMgmt;

	@Autowired
	private ExportLogMgmt exportLogMgmt;

	
	@ApiOperation(value = "查询操作日志", response = ResultInfo.class)
	@PostMapping(value = {"/getOperationLog/{pageNumber}","/getOperationLog/{pageNumber}/{pageSize}"})
	public Object getOperationLog(
			@PathVariable(name = "pageSize", required = false) Integer pageSize,
			@PathVariable(name = "pageNumber") int pageNumber,
			@RequestBody OperateLog condition
		){
		Map<String, Serializable> map = new HashMap<String, Serializable>();
		Page<OperateLog> infoList = null;
		try {
			if (pageSize == null)
				pageSize = Page.PAGE_SIZE;
			PageHelper.startPage(pageNumber, pageSize);
			infoList = logMgmt.selectByCondition(condition);
			PageHelper.endPage();
			map.put("infoList", infoList);
			map.put("total", infoList.getTotal());
			map.put("pages", infoList.getPages());
			map.put("pageNum", infoList.getPageNum());
			return new ResultInfo(OK, map);
		} catch (Exception e) {
			logger.error("getOperationLog error", e);
			return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, e.getMessage()));
		}

	}

	@ApiOperation(value = "记录操作日志", response = ResultInfo.class)
	@PostMapping(value = "/writeOperationLog")
	public Object writeOperationLog(@ApiParam(value = "操作记录") @RequestBody OperateLog log) {
		try {
			logger.debug("received log :" + log);
			logMgmt.writeOperationLog(log);
			return new ResultInfo(OK);
		} catch (Exception e) {
			logger.error("writeOperationLog failed, log is:" + log, e);
			return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}


	//分页查询登录日志
	@PostMapping(value = {"/getLoginLog/{pageNumber}/{pageSize}","/getLoginLog/{pageNumber}"})
	public Object getLoginLog(
			@PathVariable(name="pageNumber")int pageNumber,
			@PathVariable(name = "pageSize", required = false) Integer pageSize,
			@RequestBody LoginSession condition){
		Map<String, Serializable> map = new HashMap<String, Serializable>();
		Page<LoginSession> infoList = null;
		try{
			if (pageSize == null)
				pageSize = Page.PAGE_SIZE;
			PageHelper.startPage(pageNumber, pageSize);
			infoList = (Page<LoginSession>) sessionMgmt.getList(condition);
			PageHelper.endPage();
			map.put("infoList", infoList);
			map.put("total", infoList.getTotal());
			map.put("pages", infoList.getPages());
			map.put("pageNum", infoList.getPageNum());
			return new ResultInfo(OK, map);
		}catch(Exception e){
			logger.error("getLoginLog error" , e);
			return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}	
	
	@PostMapping(value = "/writeLoginLog")
	public void writeLoginLog(@RequestBody LoginSession log) {
		try{
			//logger.info("received log :"+log);
			sessionMgmt.add(log);
		} catch (Exception e) {
			logger.error("writeLoginLog failed, log is:" + log, e);
		}
	}
	
	@PutMapping(value = "/logout/{sessionId}")
	public void logout(
			@PathVariable(name="sessionId",required=true) String sessionId,
			HttpServletResponse response
		){
		try {
			sessionMgmt.logout(sessionId);
		} catch (Exception e) {
			logger.error("logout failed " + sessionId, e);
			try {
				response.sendError(500);
			} catch (IOException e1) {
			}		
		}
	}	
	
	@ApiOperation(value = "查询导出日志", response = ResultInfo.class)
	@PostMapping(value = {"/getExportLog/{pageNumber}/{pageSize}","/getExportLog/{pageNumber}"})
	public Object getExportLog(
			@ApiParam(value = "每页条数") @PathVariable(name = "pageSize", required = false) Integer pageSize,
			@ApiParam(value = "显示页码") @PathVariable(name = "pageNumber") int pageNumber,
			@ApiParam(value = "查询条件") @RequestBody ExportLog condition) {
		Map<String, Serializable> map = new HashMap<String, Serializable>();
		Page<ExportLog> infoList = null;
		try {
			if (pageSize == 0)
				pageSize = Page.PAGE_SIZE;
			PageHelper.startPage(pageNumber, pageSize);
			infoList = exportLogMgmt.selectByCondition(condition);
			PageHelper.endPage();
			map.put("infoList", infoList);
			map.put("total", infoList.getTotal());
			map.put("pages", infoList.getPages());
			map.put("pageNum", infoList.getPageNum());
			return new ResultInfo(OK, map);
		} catch (Exception e) {
			logger.error("getExportLog error", e);
			return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, e.getMessage()));
		}

	}

	@ApiOperation(value = "记录导出日志", response = ResultInfo.class)
	@PostMapping(value = "/writeExportLog")
	public Object writeExportLog(@ApiParam(value = "导出记录") @RequestBody ExportLog log) {
		try {
			logger.debug("received log :" + log);
			exportLogMgmt.writeExportLog(log);
			return new ResultInfo(OK);
		} catch (Exception e) {
			logger.error("writeExportLog failed, log is:" + log, e);
			return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}

	
}
