package com.cpit.cpmt.biz.controller.monitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.monitor.RealTimeBmsInfoMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.basic.BmsInfo;
import com.cpit.cpmt.dto.monitor.EquimentMonitorCondition;

@RestController
@RequestMapping("/monitor")
public class RealTimeBmsInfoController {
	private final static Logger logger = LoggerFactory.getLogger(RealTimeBmsInfoController.class);
	@Autowired RealTimeBmsInfoMgmt realTimeBmsInfoMgmt;
	/*充电设施实时运行状态监控*/
	//postman测试 http://localhost:28060/monitor/queryRealTimeBmsInfo/1/10
	//测试参数:{"operatorID":"MA5DEDCQ9","stationID":"902","equipmentID":"3301231230000001","areaCode":"440304","stationStreet":"12"}
	@RequestMapping("/queryRealTimeBmsInfo/{pageNumber}/{pageSize}")
	public ResultInfo queryRealTimeBmsInfo(
			@PathVariable("pageNumber") Integer pageNumber,
			@PathVariable("pageSize") Integer pageSize,
			@RequestBody EquimentMonitorCondition emc) {
		logger.info("queryRealTimeBmsInfo begin param===>>>pageNumber:"+pageNumber+",pageSize:"+pageSize+",operatorID:"+emc.getOperatorID()+",stationID="+emc.getStationID()+",equipmentID:"+emc.getAreaCode()+",areaCode;"+emc.getAreaCode()+",streetId:"+emc.getStreetId());
		Map<String,Serializable> map = new HashMap<String,Serializable>();
		Page<BmsInfo> infoList = null;
		try {
			if(pageNumber==-1){
				infoList = realTimeBmsInfoMgmt.queryRealTimeBmsInfo(emc);
			}else {
				PageHelper.startPage(pageNumber, pageSize);
				infoList = realTimeBmsInfoMgmt.queryRealTimeBmsInfo(emc);
				PageHelper.endPage();	
			}
			map.put("infoList", infoList);//分页显示的内容集合
	        map.put("total", infoList.getTotal());//总记录数
	        map.put("pages", infoList.getPages());//总页数
	        map.put("pageNum", infoList.getPageNum());//当前页
	        logger.info("queryRealTimeBmsInfo total:" + infoList.getTotal());
	        return new ResultInfo(ResultInfo.OK,map);
		} catch (Exception e) {
			logger.error("queryRealTimeBmsInfo error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));

		}
	}
	
	//进入实时监控详情页面
	//postman测试 http://localhost:28060/operator/queryBmsRealDtailInfo?operatorID=MA5DEDCQ9&connectorID=330123123000000101
	@RequestMapping("/queryBmsRealDtailInfo")
	public ResultInfo queryBmsRealDtailInfo(
			@RequestParam("operatorID") String operatorID,
			@RequestParam("connectorID")String connectorID) {
		logger.info("queryBmsRealDtailInfo begin param===>>>operatorID:"+operatorID+",connectorID:"+connectorID);
		try {
	        return new ResultInfo(ResultInfo.OK,realTimeBmsInfoMgmt.queryBmsRealDtailInfo(operatorID,connectorID));	
		} catch (Exception e) {
			logger.error("queryBmsRealDtailInfo error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	
	//进入实时监控详情页面
    //实时监控的总电压、总电流、单体最高电压、单体最低电压、单体最高温度、单体最低温度使用图形展示
	//postman测试 http://localhost:28060/operator/queryBmsGraphicDisplayInfo?operatorID=MA5DEDCQ9&connectorID=330123123000000101
	@RequestMapping("/queryBmsGraphicDisplayInfo")
	public ResultInfo queryBmsGraphicDisplayInfo(
			@RequestParam("operatorID") String operatorID,
			@RequestParam("connectorID")String connectorID) {
		logger.info("queryBmsGraphicDisplayInfo begin param===>>>operatorID:"+operatorID+",connectorID:"+connectorID);
		Map<String,Object> map = new HashMap<String,Object>();
		List<BmsInfo> infoList = new ArrayList<BmsInfo>();
		try {
			infoList = realTimeBmsInfoMgmt.queryBmsGraphicDisplayInfo(operatorID,connectorID);
			logger.info("queryBmsGraphicDisplayInfo total:" + infoList.size());
			map.put("infoList", infoList);
			return new ResultInfo(ResultInfo.OK,map);
		} catch (Exception e) {
			logger.error("queryBmsGraphicDisplayInfo error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	
}
