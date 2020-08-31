package com.cpit.cpmt.biz.controller.exchange.operator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.exchange.operator.InteractionMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.operator.ExcOperFlow;
import com.cpit.cpmt.dto.exchange.operator.ExcParameterCtl;
import com.cpit.cpmt.dto.exchange.operator.ExcThirdAuthentication;
import com.cpit.cpmt.dto.exchange.operator.ExcThirdInteractive;
/**
 * 1.流量控制 2.参数配置 3.接口适配参数配置 4.交互权限管理
 * @author zcp
 *
 */
@RestController
@RequestMapping(value = "/exchange/intact")
public class InteractionController{
	
	@Value("${reporter.address}")
	private String foreignIp;
	
	private final static Logger logger = LoggerFactory.getLogger(InteractionController.class);
	@Autowired
	private InteractionMgmt interactionMgmt;
	
	//流量控制===start
	/**
	 * 流量控制--根据运营商operatorId查询开关对象
	 * @param operatorId
	 * @return
	 */
	@GetMapping(value="/getOperFlowByOperId/{operatorId}")
	private Object getOperFlowByOperId(
			@PathVariable("operatorId") String operatorId
			) {
		try {
			return new ResultInfo(ResultInfo.OK, interactionMgmt.getOperFlowByOperId(operatorId));
		} catch (Exception ex) {
			logger.error("getOperFlowByOperId error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	/**
	 * 流量控制--根据ExcOperFlow对象修改流量开关表
	 * @param ExcOperFlow
	 * @return
	 */
	@PutMapping(value="/updateOperFlowByOperId")
	private Object updateOperFlowByOperId(
			@RequestBody(required=true) ExcOperFlow excOperFlow
			) {
		try {
			interactionMgmt.updateOperFlowByOperId(excOperFlow);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception ex) {
			logger.error("updateOperFlowByOperId error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	
	/**
	 * 流量控制--根据ExcOperFlow对象添加流量开关表
	 * @param ExcOperFlow
	 * @return
	 */
	@PostMapping(value="/addOperFlowByOperId")
	private Object addOperFlowByOperId(
			@RequestBody(required=true) ExcOperFlow excOperFlow
			) {
		try {
			interactionMgmt.addOperFlowByOperId(excOperFlow);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception ex) {
			logger.error("addOperFlowByOperId error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}	
	}
	
	/**
	 * 流量控制--根据operatorId删除流量开关表
	 * @param operatorId
	 * @return
	 */
	@DeleteMapping("delOperFlowByOperId/{operatorId}")
	private Object delOperFlowByOperId(
			@PathVariable("operatorId") String operatorId
			) {
		try {
			interactionMgmt.delOperFlowByOperId(operatorId);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception ex) {
			logger.error("delOperFlowByOperId error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}	
	}
	
	/**
	 * 流量控制--获取运营商列表和流量开关状况
	 * @param operatorId
	 * @return
	 */
	@PostMapping(value="/getOperFlowByParam/{pageNumber}/{pageSize}")
	private Object getOperFlowByParam(
			@PathVariable("pageNumber") int pageNumber,
			@PathVariable("pageSize") int pageSize,
			@RequestBody(required=true) ExcOperFlow excOperFlow
			) {
		Map<String, Serializable> map = new HashMap<String, Serializable>();
		try {
			PageHelper.startPage(pageNumber, pageSize);
			Page<ExcOperFlow> infoList = interactionMgmt.getOperFlowByParam(excOperFlow);
			PageHelper.endPage();
			map.put("infoList", infoList);
			map.put("total", infoList.getTotal());
			map.put("pages", infoList.getPages());
			map.put("pageNum", infoList.getPageNum());
			return new ResultInfo(ResultInfo.OK,map);
		} catch (Exception ex) {
			logger.error("getOperFlowByParam error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}	
	}
	//流量控制===end
	
	
	//交互权限管理===start
	/**
	 * 接口适配--查询鉴权表信息(列表)
	 * @param ExcAuthentication
	 * @return
	 */
	@PostMapping("getExcAuthenticationByParam/{pageNumber}/{pageSize}")
	private Object getExcAuthenticationByParam(
			@PathVariable("pageNumber") int pageNumber,
			@PathVariable("pageSize") int pageSize,
			@RequestBody(required=true) ExcThirdAuthentication excThirdAuthentication
			) {
		Map<String, Serializable> map = new HashMap<String, Serializable>();
		try {
			PageHelper.startPage(pageNumber, pageSize);
			Page<ExcThirdAuthentication> infoList = interactionMgmt.getExcThirdAuthByParam(excThirdAuthentication);
			PageHelper.endPage();
			map.put("infoList", infoList);
			map.put("total", infoList.getTotal());
			map.put("pages", infoList.getPages());
			map.put("pageNum", infoList.getPageNum());
			return new ResultInfo(ResultInfo.OK,map);
		} catch (Exception ex) {
			logger.error("getExcAuthenticationByParam error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}	
	}
	
	/**
	 * 交互权限管理--添加配置鉴权表信息
	 * @param ExcAuthentication
	 */
	@PostMapping(value="/addExcAuthentication")
	private Object addExcAuthentication(
			@RequestBody(required=true) ExcThirdAuthentication excThirdAuthentication
			) {
		try {
			interactionMgmt.addExcAuthentication(excThirdAuthentication);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception ex) {
			logger.error("addExcAuthentication error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}	
	}
	
	/**
	 * 交互权限管理--修改配置鉴权表信息
	 * @param ExcAuthentication
	 */
	@PutMapping(value="/updateExcAuthentication")
	private Object updateExcAuthentication(
			@RequestBody(required=true) ExcThirdAuthentication excThirdAuthentication
			) {
		try {
			interactionMgmt.updateExcAuthentication(excThirdAuthentication);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception ex) {
			logger.error("addExcAuthentication error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}	
	}
	
	/**
	 * 交互权限管理--主键获得第三方可用接口
	 * @param authId
	 * @return
	 */
	@GetMapping("getExcAuthenticationById/{authId}")
	private Object getExcAuthenticationById(
			@PathVariable("authId") int authId
			) {
		try {
			return new ResultInfo(ResultInfo.OK,interactionMgmt.getExcAuthenticationById(authId));
		} catch (Exception ex) {
			logger.error("getExcAuthenticationById error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}	
	}
	
	/**
	 * 交互权限管理--获取所有的第三方可访问接口
	 */
	@GetMapping("getAllInterfaceList")
	private Object getAllInterfaceList() {
		try {
			return new ResultInfo(ResultInfo.OK,interactionMgmt.getAllInterfaceList());
		} catch (Exception ex) {
			logger.error("getAllInterfaceList error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	//交互权限管理===end
	
	/**
	 * 获取对外ip
	 * @return
	 */
	@GetMapping("getForeignIp")
	public String getForeignIp() {
		return foreignIp;
	}
	
	//参数配置===start
	//=======参数配置
	/**
	 * 获取参数列表
	 * @param pageNumber
	 * @param pageSize
	 * @param excParameterCtl
	 * @return
	 */
	@PostMapping(value="/getExcParameterCtlByParam/{pageNumber}/{pageSize}")
	private Object getExcParameterCtlByParam(
			@PathVariable("pageNumber") int pageNumber,
			@PathVariable("pageSize") int pageSize,
			@RequestBody(required=true) ExcParameterCtl excParameterCtl
			) {
		Map<String, Serializable> map = new HashMap<String, Serializable>();
		try {
			PageHelper.startPage(pageNumber, pageSize);
			Page<ExcParameterCtl> infoList = interactionMgmt.getExcParameterCtlByParam(excParameterCtl);
			PageHelper.endPage();
			map.put("infoList", infoList);
			map.put("total", infoList.getTotal());
			map.put("pages", infoList.getPages());
			map.put("pageNum", infoList.getPageNum());
			return new ResultInfo(ResultInfo.OK, map);
		} catch (Exception ex) {
			logger.error("getExcParameterCtlByParam error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	/**
	 * 修改系统参数
	 * @param ExcParameterCtl
	 * @return
	 */
	@PutMapping("uptExcParameterCtl")
	private Object uptExcParameterCtl(
			@RequestBody(required=true) ExcParameterCtl excParameterCtl
			) {
		try {
			interactionMgmt.uptExcParameterCtl(excParameterCtl);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception ex) {
			logger.error("uptExcParameterCtl error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	
	/**
	 * 新增系统参数
	 * @param ExcParameterCtl
	 * @return
	 */
	@PostMapping("addExcParameterCtl")
	private Object addExcParameterCtl(
			@RequestBody(required=true) ExcParameterCtl excParameterCtl
			) {
		try {
			interactionMgmt.addExcParameterCtl(excParameterCtl);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception ex) {
			logger.error("addExcParameterCtl error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	/**
	 * 删除系统参数
	 * @param ExcParameterCtl
	 * @return
	 */
	@DeleteMapping("delExcParameterCtl/{id}")
	private Object delExcParameterCtl(
			@PathVariable("id") int id
			) {
		try {
			interactionMgmt.delExcParameterCtl(id);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception ex) {
			logger.error("delExcParameterCtl error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	//参数配置===end
	
	
	//新
	/**
	 * 接口适配--查询鉴权中间表表信息(每个接口一条列表)新
	 * @param ExcAuthentication
	 * @return
	 */
	@PostMapping("getExcAutListByParam/{pageNumber}/{pageSize}")
	private Object getExcAutListByParam(
			@PathVariable("pageNumber") int pageNumber,
			@PathVariable("pageSize") int pageSize,
			@RequestBody(required=true) ExcThirdInteractive excThirdInteractive
			) {
		Map<String, Serializable> map = new HashMap<String, Serializable>();
		try {
			PageHelper.startPage(pageNumber, pageSize);
			Page<ExcThirdInteractive> infoList = interactionMgmt.getExcAutListByParam(excThirdInteractive);
			PageHelper.endPage();
			map.put("infoList", infoList);
			map.put("total", infoList.getTotal());
			map.put("pages", infoList.getPages());
			map.put("pageNum", infoList.getPageNum());
			return new ResultInfo(ResultInfo.OK,map);
		} catch (Exception ex) {
			logger.error("getExcAutListByParam error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}	
	}
	
	/**
	 * 接口适配--鉴权中间表表信息(添加)新
	 * @param ExcThirdInteractive
	 * @return
	 */
	@PostMapping("addInteractive")
	private Object addInteractive(
			@RequestBody(required=true) ExcThirdInteractive excThirdInteractive
			) {
		try {
			interactionMgmt.addInteractive(excThirdInteractive);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception ex) {
			logger.error("addInteractive error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	/**
	 * 接口适配--鉴权中间表表信息(修改)新
	 * @param ExcThirdInteractive
	 * @return
	 */
	@PutMapping("uptInteractive")
	private Object uptInteractive(
			@RequestBody(required=true) ExcThirdInteractive excThirdInteractive
			) {
		try {
			interactionMgmt.uptInteractive(excThirdInteractive);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception ex) {
			logger.error("uptInteractive error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	/**
	 * 接口适配--鉴权中间表表信息(删除)新
	 * @param ExcThirdInteractive
	 * @return
	 */
	@DeleteMapping("delInteractive/{interactiveId}")
	private Object delInteractive(
			@PathVariable("interactiveId") int interactiveId
			) {
		try {
			interactionMgmt.delInteractive(interactiveId);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception ex) {
			logger.error("delInteractive error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	/**
	 * 接口适配--鉴权表信息(删除)新
	 * @param ExcThirdInteractive
	 * @return
	 */
	@DeleteMapping("delAuthentication/{authId}")
	private Object delAuthentication(
			@PathVariable("authId") int authId
			) {
		try {
			interactionMgmt.delAuthentication(authId);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception ex) {
			logger.error("delAuthentication error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
}
