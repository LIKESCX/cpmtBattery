package com.cpit.cpmt.biz.controller.exchange.operator;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.exchange.operator.OperatorInfoMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.operator.OperatorChangeHis;
import com.cpit.cpmt.dto.exchange.operator.OperatorFile;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;

@RestController
@RequestMapping(value = "/exchange/operator")
public class OperatorInfoController {

	private final static Logger logger = LoggerFactory.getLogger(OperatorInfoController.class);

	@Autowired
	private OperatorInfoMgmt operatorMgmt;

	//获取运营商列表
	@PostMapping("/getOperatorInfoList")
	public Object getOperatorInfoList(int pageNumber,int pageSize, @RequestBody OperatorInfoExtend operatorInfoExtend) {
		logger.debug("getOperatorInfoList begin, pageNumber=" + pageNumber+",pageSize="+pageSize+";operatorInfoExtend="+operatorInfoExtend);
		Map<String, Serializable> map = new HashMap<String, Serializable>();
		try {
			PageHelper.startPage(pageNumber, pageSize);
			Page<OperatorInfoExtend> infoList = operatorMgmt.getOperatorInfoList(operatorInfoExtend);
			PageHelper.endPage();
			map.put("infoList", infoList);
			map.put("total", infoList.getTotal());
			map.put("pages", infoList.getPages());
			map.put("pageNum", infoList.getPageNum());
			return new ResultInfo(ResultInfo.OK, map);
		} catch (Exception ex) {
			logger.error("getOperatorInfoList error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	//查询运营商
	@GetMapping("/getOperatorInfo")
	public Object getOperatorInfo(String operatorId) {
		logger.info("getOperatorInfo begin, operatorId=" + operatorId);
		try {
			return new ResultInfo(ResultInfo.OK,operatorMgmt.getOperatorInfoById(operatorId));
		} catch (Exception ex) {
			logger.error("getOperatorInfo error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	//查询运营商
	@GetMapping("/getOperatorInfoByName")
	public Object getOperatorInfoByName(String operatorName) {
		logger.info("getOperatorInfoByName begin, operatorName=" + operatorName);
		try {
			return new ResultInfo(ResultInfo.OK,operatorMgmt.getOperatorInfoByName(operatorName));
		} catch (Exception ex) {
			logger.error("getOperatorInfoByName error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	// 添加运营商信息
	@PostMapping(value = "/addOperatorInfo")
	public ResultInfo addOperatorInfo(@RequestBody OperatorInfoExtend operatorInfoExtend) {
		logger.debug("addOperatorInfo,begin,operatorInfoExtend:" + operatorInfoExtend);
		try {
			operatorMgmt.addOperatorInfo(operatorInfoExtend);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception e) {
			logger.error("addOperatorInfo error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
		}

	}

	// 修改运营商信息
	@PutMapping(value = "/updateOperatorInfo")
	public void updateOperatorInfo(
		@RequestBody OperatorInfoExtend operatorInfoExtend,
		HttpServletResponse response
		
	) {
		logger.debug("updateOperatorInfo,begin,operatorInfoExtend:" + operatorInfoExtend);
		try {
			operatorMgmt.updateOperatorInfo(operatorInfoExtend);
		} catch (Exception e) {
			logger.error("updateOperatorInfo error:", e);
			try {
				response.sendError(500);
			} catch (IOException e1) {
			}			
		}
	}

	// 删除
	/*@RequestMapping(value = "/deleteOperatorInfo")
	public ResultInfo deleteOperatorInfo(@RequestBody OperatorInfo operatorInfo) {
		logger.info("deleteOperatorInfo,begin,param:" + operatorInfo);
		try {
			operatorMgmt.deleteOperatorInfo(operatorInfo);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception e) {
			logger.error("deleteOperatorInfo error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
		}
	}*/
	
	
	//获取运营商列表
	@PostMapping("/getOperatorListWithStationCount")
	public Object getOperatorListWithStationCount(int pageNumber,int pageSize, @RequestBody OperatorInfoExtend operatorInfoExtend) {
		logger.debug("getOperatorInfoList begin, pageNumber=" + pageNumber+",pageSize="+pageSize+";operatorInfoExtend="+operatorInfoExtend);
		Map<String, Serializable> map = new HashMap<String, Serializable>();
		try {
			PageHelper.startPage(pageNumber, pageSize);
			Page<OperatorInfoExtend> infoList = operatorMgmt.getOperatorListWithStationCount(operatorInfoExtend);
			PageHelper.endPage();
			map.put("infoList", infoList);
			map.put("total", infoList.getTotal());
			map.put("pages", infoList.getPages());
			map.put("pageNum", infoList.getPageNum());
			return new ResultInfo(ResultInfo.OK, map);
		} catch (Exception ex) {
			logger.error("getOperatorListWithStationCount error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	//获取运营商附件列表
	@GetMapping("/getOperatorFileListById")
	public Object getOperatorFileListById(int pageNumber,int pageSize,String operatorId) {
		logger.info("getOperatorFileListById begin, pageNumber=" + pageNumber+",pageSize="+pageSize+";operatorId="+operatorId);
		Map<String, Serializable> map = new HashMap<String, Serializable>();
		try {
			PageHelper.startPage(pageNumber, pageSize);
			Page<OperatorFile> infoList = operatorMgmt.getOperatorFileListById(operatorId);
			PageHelper.endPage();
			map.put("infoList", infoList);
			map.put("total", infoList.getTotal());
			map.put("pages", infoList.getPages());
			map.put("pageNum", infoList.getPageNum());
			return new ResultInfo(ResultInfo.OK, map);
		} catch (Exception ex) {
			logger.error("getOperatorFileList error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}

	@PostMapping("/getFileCountByCondition")
	public Object getFileCountByCondition(@RequestBody OperatorFile condition) {
		try {
			return operatorMgmt.getFileCountByCondition(condition);
		} catch (Exception ex) {
			return 0;
		}
	}
	
	@PostMapping(value = "/addOperatorFile")
	public ResultInfo addOperatorFile(@RequestBody OperatorFile operatorFile) {
		logger.debug("addOperatorFile,begin,operatorFile:" + operatorFile);
		try {
			operatorMgmt.addOperatorFile(operatorFile);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception e) {
			logger.error("addOperatorFile error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
		}

	}
	
	//查询运营商
	@GetMapping("/getAuditPassOperatorList")
	public Object getAuditPassOperatorList() {
		logger.info("getAuditPassOperatorList begin");
		try {
			List<OperatorInfoExtend> list = operatorMgmt.getAuditPassOperatorList();
			return new ResultInfo(ResultInfo.OK,list);
		} catch (Exception ex) {
			logger.error("getAuditPassOperatorList error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	@DeleteMapping(value = "/delFilesByOperatorId")
	public void delFilesByOperatorId(String operatorID,HttpServletResponse response) {
		logger.debug("delFilesByOperatorId,begin,operatorID:" + operatorID);
		try {
			operatorMgmt.delFilesByOperatorId(operatorID);
		} catch (Exception e) {
			logger.error("delFilesByOperatorId error:", e);
			try {
				response.sendError(500);
			} catch (IOException e1) {
			}				
		}

	}
	
	//查询运营商近十日所有充电量
	@GetMapping("/getTotalElectric")
	public Object getTotalElectric(String operatorId) {
		logger.info("getTotalElectric begin, operatorId=" + operatorId);
		try {
			return new ResultInfo(ResultInfo.OK,operatorMgmt.getTotalElectric(operatorId));
		} catch (Exception ex) {
			logger.error("getTotalElectric error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	//查询运营商下每个充电站补贴金额
	@GetMapping("/getTotalAllowance")
	public Object getTotalAllowance(String operatorId) {
		logger.info("getTotalAllowance begin, operatorId=" + operatorId);
		try {
			return new ResultInfo(ResultInfo.OK,operatorMgmt.getTotalAllowance(operatorId));
		} catch (Exception ex) {
			logger.error("getTotalAllowance error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	//查询运营商总装机功率
	@GetMapping("/getTotalPower")
	public Object getTotalPower(String operatorId) {
		logger.info("getTotalPower begin, operatorId=" + operatorId);
		try {
			return new ResultInfo(ResultInfo.OK,operatorMgmt.getTotalPower(operatorId));
		} catch (Exception ex) {
			logger.error("getTotalPower error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	//查询站的最近10天的充电量
	@GetMapping("/getChargeInDay")
	public Object getChargeInDay(String operatorId) {
		logger.info("getChargeInDay begin,operatorId = "+operatorId );
		try {
			return new ResultInfo(ResultInfo.OK,operatorMgmt.getChargeInDay(operatorId));
		} catch (Exception ex) {
			logger.error("getChargeInDay error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	//查询补贴金额近10个月动态信息
	@GetMapping("/getAllowancePriceInMonth")
	public Object getAllowancePriceDynamic(String operatorId) {
		logger.info("getAllowancePriceInMonth begin,operatorId = "+operatorId );
		try {
			return new ResultInfo(ResultInfo.OK,operatorMgmt.getAllowancePriceInMonth(operatorId));
		} catch (Exception ex) {
			logger.error("getAllowancePriceInMonth error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	//查询装机功率近10个月动态信息
	@GetMapping("/getPowerInMonth")
	public Object getPowerInMonth(String operatorId) {
		logger.info("getPowerInMonth begin,operatorId = "+operatorId );
		try {
			return new ResultInfo(ResultInfo.OK,operatorMgmt.getPowerInMonth(operatorId));
		} catch (Exception ex) {
			logger.error("getPowerInMonth error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}	
	
	@GetMapping("/updateOperatorStatusByFixedCycle")
	public Object updateOperatorStatusByFixedCycle() {
		logger.info("updateOperatorStatusByFixedCycle begin");
		try {
			operatorMgmt.updateOperatorStatusByFixedCycle();
			return "OK";
		} catch (Exception ex) {
			logger.error("updateOperatorStatusByFixedCycle error", ex);
			return "FAIL";
		}
	}
	
	//获取运营商历史
	@PostMapping("/getChangeListByCondion")
	public Object getChangeListByCondion(int pageNumber,int pageSize, @RequestBody OperatorChangeHis operatorChangeHis) {
		logger.debug("getChangeListByCondion begin, pageNumber=" + pageNumber+",pageSize="+pageSize+";operatorChangeHis="+operatorChangeHis);
		Map<String, Serializable> map = new HashMap<String, Serializable>();
		try {
			PageHelper.startPage(pageNumber, pageSize);
			Page<OperatorChangeHis> infoList = operatorMgmt.getChangeListByCondion(operatorChangeHis);
			PageHelper.endPage();
			map.put("infoList", infoList);
			map.put("total", infoList.getTotal());
			map.put("pages", infoList.getPages());
			map.put("pageNum", infoList.getPageNum());
			return new ResultInfo(ResultInfo.OK, map);
		} catch (Exception ex) {
			logger.error("getChangeListByCondion error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	//获取运营商总充电桩数
	@GetMapping("/getOperatorTotalEquipment")
	public Object getOperatorTotalEquipment() {
		logger.info("getOperatorTotalEquipment begin");
		try {
			return new ResultInfo(ResultInfo.OK,operatorMgmt.getOperatorTotalEquipment());
		} catch (Exception ex) {
			logger.error("getOperatorTotalEquipment error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	//获取运营商总充电量
	@GetMapping("/getOperatorTotalCharge")
	public Object getOperatorTotalCharge() {
		logger.info("getOperatorTotalCharge begin");
		try {
			return new ResultInfo(ResultInfo.OK,operatorMgmt.getOperatorTotalCharge());
		} catch (Exception ex) {
			logger.error("getOperatorTotalCharge error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	//获取区域总充电桩数
	@GetMapping("/getAreaTotalEquipment")
	public Object getAreaTotalEquipment() {
		logger.info("getAreaTotalEquipment begin");
		try {
			return new ResultInfo(ResultInfo.OK,operatorMgmt.getAreaTotalEquipment());
		} catch (Exception ex) {
			logger.error("getAreaTotalEquipment error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	//获取区域总充电量
	@GetMapping("/getAreaTotalCharge")
	public Object getAreaTotalCharge() {
		logger.info("getAreaTotalCharge begin");
		try {
			return new ResultInfo(ResultInfo.OK,operatorMgmt.getAreaTotalCharge());
		} catch (Exception ex) {
			logger.error("getAreaTotalCharge error", ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}

}
