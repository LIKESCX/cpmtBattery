package com.cpit.cpmt.biz.controller.security.battery.work;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.security.battery.work.NormalAnalysisMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.security.battery.normal.BatterySingleCharge;
import com.cpit.cpmt.dto.security.battery.other.BatteryDataConditions;

@RestController
@RequestMapping("/security/battery/")
public class NormalAnalysisContorller {
	private final static Logger logger = LoggerFactory.getLogger(NormalAnalysisContorller.class);
	@Autowired private NormalAnalysisMgmt normalAnalysisMgmt;
	/**
	 * 	     获取第一级钻取前，先获取bmsCode列表信息
	 */
	@RequestMapping("/queryBmsCodeListData/{pageNumber}/{pageSize}")
	public ResultInfo queryBmsCodeListData(
			@PathVariable("pageNumber") Integer pageNumber,
			@PathVariable("pageSize") Integer pageSize,
			@RequestBody BatteryDataConditions param) {
		logger.debug("queryBmsCodeListData begin pageNumber[{}],pageSize[{}],params [{}]",pageNumber,pageSize, param);
		Map<String,Object> map = new HashMap<String,Object>();
		Page<BatterySingleCharge> infoList = null;
		try {
			if(pageNumber==-1){
				infoList = normalAnalysisMgmt.queryBmsCodeListData(param);
			}else {
				PageHelper.startPage(pageNumber, pageSize);
				infoList = normalAnalysisMgmt.queryBmsCodeListData(param);
				PageHelper.endPage();	
			}
			map.put("infoList", infoList);//分页显示的内容集合
	        map.put("total", infoList.getTotal());//总记录数
	        map.put("pages", infoList.getPages());//总页数
	        map.put("pageNum", infoList.getPageNum());//当前页
	        logger.info("queryBmsCodeListData total:" + infoList.getTotal());
	        return new ResultInfo(ResultInfo.OK,map); 
		} catch (Exception e) {
			logger.error("queryBmsCodeListData_error"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	
	/**
	 * 第一级钻取：
	 * 	     按查询条件统计某车辆在某时间段内充电次数；
	 * 	     生成散点图呈现不同时间段内充电次数，x轴为时间，y轴为次数，
	 */
	@RequestMapping("/normal/queryFirstLevelData")
	public ResultInfo queryFirstLevelData(@RequestBody BatteryDataConditions param) {
		logger.debug("queryFirstLevelData begin params [{}]", param);
		try {

			 Map<String,Object> map= normalAnalysisMgmt.queryFirstLevelData(param);
			return new ResultInfo(ResultInfo.OK, map);
		} catch (Exception e) {
			logger.error("queryFirstLevelData_error"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	
	/**
	 * 第二级钻取：
		点击统计次数，向下钻取对应时间内容的充电详细列表，需要传递 本次统计次数所对应的时间点,粒度,电池编码,运营商id,设备接口编码
		输出包括开始时间、结束时间、充电站名称、运营商名称、设备编码、型号等信息，以列表展现；
	 */
	@RequestMapping("/normal/querySecondLevelData/{pageNumber}/{pageSize}")
	public ResultInfo querySecondLevelData(
			@PathVariable("pageNumber") Integer pageNumber,
			@PathVariable("pageSize") Integer pageSize,
			@RequestBody BatteryDataConditions param) {
		logger.debug("querySecondLevelData begin params [{}],pageNumber[{}],pageSize[{}]", param,pageNumber,pageSize);
		Map<String,Serializable> map = new HashMap<String,Serializable>();
		Page<BatterySingleCharge> infoList = null;
		try {
			if(pageNumber==-1){
				infoList = normalAnalysisMgmt.querySecondLevelData(param);
			}else {
				PageHelper.startPage(pageNumber, pageSize);
				infoList = normalAnalysisMgmt.querySecondLevelData(param);
				PageHelper.endPage();	
			}
			map.put("infoList", infoList);//分页显示的内容集合
	        map.put("total", infoList.getTotal());//总记录数
	        map.put("pages", infoList.getPages());//总页数
	        map.put("pageNum", infoList.getPageNum());//当前页
	        logger.debug("querySecondLevelData total:" + infoList.getTotal());
	        return new ResultInfo(ResultInfo.OK,map);
		} catch (Exception e) {
			logger.error("querySecondLevelData_error"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	
	/**
	 * 第三级钻取：需要传递 开始充电时间和结束充电时间,电池编码,运营商id,设备接口id
		选中某条第二级钻取充电详情，获取此充电过程的信息，统计数据分析指标值。
	 */
	@RequestMapping("/normal/queryThirdLevelData/{pageNumber}/{pageSize}")
	public ResultInfo queryThirdLevelData(
			@PathVariable("pageNumber") Integer pageNumber,
			@PathVariable("pageSize") Integer pageSize,
			@RequestBody BatteryDataConditions param) {
		logger.debug("queryThirdLevelData begin params [{}],pageNumber[{}],pageSize[{}]", param,pageNumber,pageSize);
		Map<String,Serializable> map = new HashMap<String,Serializable>();
		Page<BatterySingleCharge> infoList = null;
		try {
			if(pageNumber==-1){
				infoList = normalAnalysisMgmt.queryThirdLevelData(param);
			}else {
				PageHelper.startPage(pageNumber, pageSize);
				infoList = normalAnalysisMgmt.queryThirdLevelData(param);
				PageHelper.endPage();	
			}
			map.put("infoList", infoList);//分页显示的内容集合
	        map.put("total", infoList.getTotal());//总记录数
	        map.put("pages", infoList.getPages());//总页数
	        map.put("pageNum", infoList.getPageNum());//当前页
	        logger.debug("queryThirdLevelData total:" + infoList.getTotal());
	        return new ResultInfo(ResultInfo.OK,map);
		} catch (Exception e) {
			logger.error("queryThirdLevelData_error"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));

		}
	}
	
}
