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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.system.InterfacePublishMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.system.InterfacePublish;

@RestController
@RequestMapping(value="/system/interfacePublish")
public class InterfacePublishController {
	
	private final static Logger logger = LoggerFactory.getLogger(InterfacePublishController.class);
	
	@Autowired
	private InterfacePublishMgmt interfacePublishMgmt;
	
	//获取接口列表
	@PostMapping("/getInterfacePublishList")
	public Object getPoliciesPublishList(int pageNumber,int pageSize,@RequestBody InterfacePublish interfacePublish) {
		logger.debug("getInterfacePublishList begin, pageNumber=" + pageNumber+",pageSize="+pageSize+",interfacePublish="+interfacePublish);
		Map<String, Serializable> map = new HashMap<String, Serializable>();
		try {
			PageHelper.startPage(pageNumber, pageSize);
			Page<InterfacePublish> infoList = interfacePublishMgmt.getInterfacePublishList(interfacePublish);
			PageHelper.endPage();
			map.put("infoList", infoList);
			map.put("total", infoList.getTotal());
			map.put("pages", infoList.getPages());
			map.put("pageNum", infoList.getPageNum());
			return new ResultInfo(ResultInfo.OK, map);
		} catch (Exception ex) {
			logger.error("getInterfacePublishList error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	//查询接口详细信息
	@GetMapping("/getInterfaceInfo")
	public Object getInterfaceInfo(Integer faceId) {
		logger.info("getInterfaceInfo begin, faceId=" + faceId);
		try {
			return new ResultInfo(ResultInfo.OK,interfacePublishMgmt.getInterfaceInfo(faceId));
		} catch (Exception ex) {
			logger.error("getInterfaceInfo error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	// 添加接口发布信息
	@PostMapping(value = "/addInterfacePublish")
	public ResultInfo addInterfacePublish(@RequestBody InterfacePublish interfacePublish) {
		logger.debug("addInterfacePublish,begin,interfacePublish:" + interfacePublish);
		try {
			interfacePublishMgmt.addInterfacePublish(interfacePublish);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception e) {
			logger.error("addInterfacePublish error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
		}
	}
	
	// 修改接口发布信息
	@PutMapping(value = "/updateInterfacePublish")
	public void updateInterfacePublish(
		@RequestBody InterfacePublish interfacePublish,
		HttpServletResponse response
	) {
		logger.debug("updateInterfacePublish,begin,interfacePublish:" + interfacePublish);
		try {
			interfacePublishMgmt.updateInterfacePublish(interfacePublish);
		} catch (Exception e) {
			logger.error("updateInterfacePublish error:", e);
			try {
				response.sendError(500);
			} catch (IOException e1) {
			}				
		}
	}
	
	//删除接口发布信息
	@DeleteMapping(value = "/delInterfacePublish")
	public void delInterfacePublish(Integer faceId,HttpServletResponse response) {
		logger.debug("delInterfacePublish,begin,faceId:" + faceId);
		try {
			interfacePublishMgmt.delInterfacePublish(faceId);
		} catch (Exception e) {
			logger.error("delInterfacePublish error:", e);
			try {
				response.sendError(500);
			} catch (IOException e1) {
			}			}
	}
	
	//获取审核历史
	@GetMapping(value = "/getInterfaceAuditHisList")
	public Object getInterfaceAuditHisList(@RequestParam(name="faceId")Integer faceId){
		try{
			return new ResultInfo(OK,interfacePublishMgmt.getInterfaceAuditHisList(faceId));
		}catch(Exception e){
			logger.error("getInterfaceAuditHisList error" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}
}
 
