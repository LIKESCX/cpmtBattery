package com.cpit.cpmt.biz.controller.exchange.operator;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.exchange.operator.AccessManageMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.operator.AccessManage;

@RestController
@RequestMapping(value = "/exchange/operator")
public class AccessManageController {

	private final static Logger logger = LoggerFactory.getLogger(AccessManageController.class);
	
	@Autowired
	private AccessManageMgmt accessManageMgmt;

	//添加运营商管理接口
	@PostMapping(value = "/addAccessManage")
	public ResultInfo addAccessManage(@RequestBody AccessManage accessManage) {
		logger.debug("addAccessManage,begin,accessManage:" + accessManage);
		try {
			accessManageMgmt.addAccessManage(accessManage);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception e) {
			logger.error("addAccessManage error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
		}

	}
	
	//根据运营商id获取运营商管理参数
	@GetMapping(value = "/getAccessManageInfoById")
	public Object getAccessManageInfoById(String operatorId) {
		logger.info("getAccessManageInfoById begin: operatorId is :"+operatorId);
		try {
			AccessManage accessManage = accessManageMgmt.getAccessManageInfoById(operatorId);
			return new ResultInfo(ResultInfo.OK, accessManage);
		} catch (Exception ex) {
			logger.error("getAccessManageList error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	//获取运营商接入列表
	@PostMapping(value = "/getAccessManageList")
	@ResponseBody
	public Object getAccessManageList(int pageNumber,int pageSize,@RequestBody AccessManage accessManage) {
		logger.debug("getAccessManageList begin, pageNumber=" + pageNumber+",pageSize="+pageSize+",accessManage is :"+accessManage);
		Map<String, Serializable> map = new HashMap<String, Serializable>();
		try {
			PageHelper.startPage(pageNumber, pageSize);
			Page<AccessManage> infoList = accessManageMgmt.getAccessManageList(accessManage);
			PageHelper.endPage();
			map.put("infoList", infoList);
			map.put("total", infoList.getTotal());
			map.put("pages", infoList.getPages());
			map.put("pageNum", infoList.getPageNum());
			return new ResultInfo(ResultInfo.OK, map);
		} catch (Exception ex) {
			logger.error("getAccessManageList error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	//修改运营商接入管理
	@PutMapping(value = "/updateAccessManage")
	public ResultInfo updateAccessManage(@RequestBody AccessManage accessManage) {
		logger.debug("updateAccessManage,begin,param:" + accessManage);
		try {
			accessManageMgmt.updateAccessManage(accessManage);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception e) {
			logger.error("updateAccessManage error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
		}
	}
	
	//删除运营商接入管理
	@DeleteMapping(value = "/delAccessManage")
	public ResultInfo delAccessManage(String operatorId) {
		logger.info("delAccessManage,begin,operatorId:" + operatorId);
		try {
			accessManageMgmt.delAccessManage(operatorId);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception e) {
			logger.error("delAccessManage error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
		}
	}
	
	//邮件补发
	@PostMapping(value = "/sendSecretAgain")
	public ResultInfo sendSecretAgain(String email,String operatorKey,String operatorId) {
		logger.info("===sendSecretAgain email:" + email+",operatorKey="+operatorKey+",operatorId="+operatorId);
		try {
			accessManageMgmt.sendEmail(email,operatorKey,operatorId);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception e) {
			logger.error("sendSecretAgain error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
		}
	} 

}
