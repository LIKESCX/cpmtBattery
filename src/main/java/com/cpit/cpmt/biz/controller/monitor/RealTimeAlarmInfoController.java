package com.cpit.cpmt.biz.controller.monitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.TimeConvertor;
import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.exchange.process.RabbitMsgSender;
import com.cpit.cpmt.biz.impl.monitor.RealTimeAlarmInfoMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfo;
import com.cpit.cpmt.dto.monitor.AlarmConditions;
import com.cpit.cpmt.dto.monitor.EquimentMonitorCondition;

@RestController
@RequestMapping("/monitor")
public class RealTimeAlarmInfoController {
	private final static Logger logger = LoggerFactory.getLogger(RealTimeAlarmInfoController.class);
	@Autowired RealTimeAlarmInfoMgmt realTimeAlarmInfoMgmt;
	@Autowired RabbitMsgSender rabbitMsgSender;
	
	//根据前4位获取完整匹配的BmsCode编码
	@RequestMapping("/getBmsCodeListByFirstFourDigits")
	public ResultInfo getBmsCodeListByFirstFourDigits(@RequestParam("value")String value) {
		logger.debug("getBmsCodeListByFirstFourDigits begin param===>>>value:"+value);
		Map<String, Object> map = new HashMap<String,Object>();
		try {
			List<String> bmsCodeList = realTimeAlarmInfoMgmt.getBmsCodeListByFirstFourDigits(value);
			map.put("bmsCodeList", bmsCodeList);
			return new ResultInfo(ResultInfo.OK, map);
		} catch (Exception e) {
			logger.error("getBmsCodeListByFirstFourDigits_error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));

		}
	}
	/*充电设施实时运行状态监控*/
	//postman测试 http://localhost:28060/monitor/queryRealTimeAlarmInfo/1/10
	//测试参数:{"operatorID":"MA5DEDCQ9","stationID":"902","equipmentID":"3301231230000001","areaCode":"440304","stationStreet":"12"}
	@RequestMapping("/queryRealTimeAlarmInfo/{pageNumber}/{pageSize}")
	public ResultInfo queryRealTimeAlarmInfo(
			@PathVariable("pageNumber") Integer pageNumber,
			@PathVariable("pageSize") Integer pageSize,
			@RequestBody EquimentMonitorCondition emc) {
		logger.info("queryRealTimeAlarmInfo begin param===>>>pageNumber:"+pageNumber+",pageSize:"+pageSize+",operatorID:"+emc.getOperatorID()+",stationID="+emc.getStationID()+",equipmentID:"+emc.getEquipmentID()+",bmsCode;"+emc.getBmsCode());
		Map<String,Serializable> map = new HashMap<String,Serializable>();
		Page<AlarmInfo> infoList = null;
		try {
			if(pageNumber==-1){
				infoList = realTimeAlarmInfoMgmt.queryRealTimeAlarmInfo(emc);
			}else {
				PageHelper.startPage(pageNumber, pageSize);
				infoList = realTimeAlarmInfoMgmt.queryRealTimeAlarmInfo(emc);
				PageHelper.endPage();	
			}
			map.put("infoList", infoList);//分页显示的内容集合
	        map.put("total", infoList.getTotal());//总记录数
	        map.put("pages", infoList.getPages());//总页数
	        map.put("pageNum", infoList.getPageNum());//当前页
	        logger.info("queryRealTimeAlarmInfo total:" + infoList.getTotal());
	        return new ResultInfo(ResultInfo.OK,map);
		} catch (Exception e) {
			logger.error("queryRealTimeAlarmInfo error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));

		}
	}
	
	//-------------------------------******进入告警实时监控详情页面*********------------------------------
	@RequestMapping("/queryAllAlarmRealDtailInfos/{pageNumber}/{pageSize}")//一个接口把把告警实时监控详情信息全部返回
	@Transactional(readOnly=true)
	public ResultInfo queryAllAlarmRealDtailInfos(
			@PathVariable("pageNumber") Integer pageNumber,
			@PathVariable("pageSize") Integer pageSize,
			@RequestBody AlarmConditions alarmConditions) {
		//test begin
		//alarmConditions.setOperatorID("061402628");
		//alarmConditions.setConnectorID("4402090020000020001");
		////Date sDate = TimeConvertor.stringTime2Date("2020-04-27 13:00:11", TimeConvertor.FORMAT_MINUS_24HOUR);
		//Date eDate = TimeConvertor.stringTime2Date("2020-04-27 15:00:11", TimeConvertor.FORMAT_MINUS_24HOUR);
		//alarmConditions.setStartTime(sDate);
		//alarmConditions.setEndTime(eDate);
		//test end
		logger.debug("queryAllAlarmRealDtailInfos begin param===>>>operatorID:"+alarmConditions.getOperatorID()+",connectorID:"+alarmConditions.getConnectorID());
		Map<String,Object> map = new HashMap<String,Object>();
		List<Map<String,String>> infoList2 = new ArrayList<Map<String,String>>();
		List<Map<String,String>> infoList3 = new ArrayList<Map<String,String>>();
		Page<AlarmInfo> infoList = null;
		try {
			if(pageNumber==-1){
				infoList = realTimeAlarmInfoMgmt.queryAlarmRealDtailInfo(alarmConditions);
			}else {
				PageHelper.startPage(pageNumber, pageSize);
				infoList = realTimeAlarmInfoMgmt.queryAlarmRealDtailInfo(alarmConditions);
				PageHelper.endPage();	
			}
			infoList2 = realTimeAlarmInfoMgmt.queryAlarmRealGraphicDisplayInfo(alarmConditions);
			infoList3 = realTimeAlarmInfoMgmt.queryWithinTwoHoursAlarmIndexSum(alarmConditions);
			map.put("infoList", infoList);//分页显示的内容集合
			map.put("infoList2", infoList2);//分页显示的内容集合
			map.put("infoList3", infoList3);//分页显示的内容集合
	        map.put("total", infoList.getTotal());//总记录数
	        map.put("pages", infoList.getPages());//总页数
	        map.put("pageNum", infoList.getPageNum());//当前页
	        logger.info("queryAlarmRealDtailInfo total:" + infoList.getTotal());
	        return new ResultInfo(ResultInfo.OK,map);
		} catch (Exception e) {
			logger.error("queryAlarmRealDtailInfo error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	
	
	
	//postman测试 http://localhost:28060/monitor/queryAlarmRealDtailInfo/1/10?operatorID=MA5DEDCQ9&connectorID=330123123000000101
	/*@RequestMapping("/queryAlarmRealDtailInfo/{pageNumber}/{pageSize}")
	public ResultInfo queryAlarmRealDtailInfo(
			@PathVariable("pageNumber") Integer pageNumber,
			@PathVariable("pageSize") Integer pageSize,
			@RequestParam("operatorID") String operatorID,
			@RequestParam("connectorID")String connectorID) {
		Date date = new Date();
		logger.info("queryAlarmRealDtailInfo begin param===>>>operatorID:"+operatorID+",connectorID:"+connectorID);
		Map<String,Serializable> map = new HashMap<String,Serializable>();
		Page<AlarmInfo> infoList = null;
		try {
			if(pageNumber==-1){
				infoList = realTimeAlarmInfoMgmt.queryAlarmRealDtailInfo(operatorID,connectorID);
			}else {
				PageHelper.startPage(pageNumber, pageSize);
				infoList = realTimeAlarmInfoMgmt.queryAlarmRealDtailInfo(operatorID,connectorID);
				PageHelper.endPage();	
			}
			map.put("infoList", infoList);//分页显示的内容集合
	        map.put("total", infoList.getTotal());//总记录数
	        map.put("pages", infoList.getPages());//总页数
	        map.put("pageNum", infoList.getPageNum());//当前页
	        logger.info("queryAlarmRealDtailInfo total:" + infoList.getTotal());
	        return new ResultInfo(ResultInfo.OK,map);
		} catch (Exception e) {
			logger.error("queryAlarmRealDtailInfo error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}*/
    //告警级别、告警次数使用图形展示
	//postman测试 http://localhost:28060/monitor/queryAlarmRealGraphicDisplayInfo?operatorID=MA5DEDCQ9&connectorID=330123123000000101
//	@RequestMapping("/queryAlarmRealGraphicDisplayInfo")
//	public ResultInfo queryAlarmRealGraphicDisplayInfo(
//			@RequestParam("operatorID") String operatorID,
//			@RequestParam("connectorID")String connectorID) {
//		logger.info("queryAlarmRealGraphicDisplayInfo begin param===>>>operatorID:"+operatorID+",connectorID:"+connectorID);
//		Map<String,Object> map = new HashMap<String,Object>();
//		List<Map<String,String>> infoList = new ArrayList<Map<String,String>>();
//		try {
//			infoList = realTimeAlarmInfoMgmt.queryAlarmRealGraphicDisplayInfo(operatorID,connectorID);
//			logger.info("queryAlarmRealGraphicDisplayInfo total[{}]:", infoList==null?0:infoList.size());
//			map.put("infoList", infoList);
//			return new ResultInfo(ResultInfo.OK,map);
//		} catch (Exception e) {
//			logger.error("queryAlarmRealGraphicDisplayInfo error:", e);
//			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
//		}
//	}
	
	/*	实时告警详情加折线图：一共4个指标：
		指标1:	2个小时内新告警（发生状态）个数，
		指标2:	2个小时内已解决（已处理）的新告警个数，
		指标3:	2个小时内未解决的告警个数（包括新的和旧的告警），
		指标4:	2个小时内已解决的告警个数（包括新的和旧的，有可能有的告警是上2个小时发生的。
				另如果告警是在下2个小时才解决，就得在下2个小时图形里看到，
				无论是上2个小时发生还是在下一个小时解决，
				都不重要，只是统计数量。）*/
/*
	@RequestMapping("/queryWithinTwoHoursAlarmIndexSum")
	public ResultInfo queryWithinTwoHoursAlarmIndexSum(
			@RequestParam("operatorID") String operatorID,
			@RequestParam("connectorID")String connectorID) {
		logger.info("queryWithinTwoHoursAlarmIndexSum begin param===>>>operatorID:"+operatorID+",connectorID:"+connectorID);
		Map<String,Object> map = new HashMap<String,Object>();
		List<Map<String,String>> infoList = new ArrayList<Map<String,String>>();
		try {
			infoList = realTimeAlarmInfoMgmt.queryWithinTwoHoursAlarmIndexSum(operatorID, connectorID);
			logger.info("queryWithinTwoHoursAlarmIndexSum total:" + infoList.size());
			map.put("infoList", infoList);
			return new ResultInfo(ResultInfo.OK,map);
		} catch (Exception e) {
			logger.error("queryWithinTwoHoursAlarmIndexSum error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}*/
	
	//-------------------------------******进入告警历史监控详情页面*********------------------------------
	@RequestMapping("/queryAlarmHistoryDtailInfo/{pageNumber}/{pageSize}")
	public ResultInfo queryAlarmHistoryDtailInfo(
			@PathVariable("pageNumber") Integer pageNumber,
			@PathVariable("pageSize") Integer pageSize,
			@RequestBody AlarmConditions alarmConditions) {
		logger.info("queryAlarmRealDtailInfo begin param===>>>alarmConditions:"+alarmConditions);
		Map<String,Serializable> map = new HashMap<String,Serializable>();
		Page<AlarmInfo> infoList = null;
		try {
			if(pageNumber==-1){
				infoList = realTimeAlarmInfoMgmt.queryAlarmHistoryDtailInfo(alarmConditions);
			}else {
				PageHelper.startPage(pageNumber, pageSize);
				infoList = realTimeAlarmInfoMgmt.queryAlarmHistoryDtailInfo(alarmConditions);
				PageHelper.endPage();	
			}
			map.put("infoList", infoList);//分页显示的内容集合
	        map.put("total", infoList.getTotal());//总记录数
	        map.put("pages", infoList.getPages());//总页数
	        map.put("pageNum", infoList.getPageNum());//当前页
	        logger.info("queryAlarmHistoryDtailInfo total:" + infoList.getTotal());
	        return new ResultInfo(ResultInfo.OK,map);
		} catch (Exception e) {
			logger.error("queryAlarmRealDtailInfo error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}

	@RequestMapping("/queryAlarmHistoryGraphicDisplayInfo")
	public ResultInfo queryAlarmGraphicDisplayInfo2(@RequestBody AlarmConditions alarmConditions) {
		logger.info("queryAlarmHistoryGraphicDisplayInfo begin param===>>>operatorID:"+alarmConditions.getOperatorID()+",connectorID:"+alarmConditions.getConnectorID()+",startTime:"+alarmConditions.getStartTime()+",endTime:"+alarmConditions.getEndTime());
		Map<String,Object> map = new HashMap<String,Object>();
		List<Map<String,String>> infoList = new ArrayList<Map<String,String>>();
		try {
			infoList = realTimeAlarmInfoMgmt.queryAlarmHistoryGraphicDisplayInfo(alarmConditions);
			logger.info("queryAlarmHistoryGraphicDisplayInfo total:" + infoList.size());
			map.put("infoList", infoList);
			return new ResultInfo(ResultInfo.OK,map);
		} catch (Exception e) {
			logger.error("queryAlarmHistoryGraphicDisplayInfo error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	
	/**
	 * 点击历史告警详情按钮，进入历史告警详情界面，列表同实时告警详情界面，
	 * 另列表上方有查询条件：开始时间（当前时间前2个小时之前），结束时间（当前时间前2个小时之前）。
	 * 默认查询今天的2个小时以前的所有数据。图形和实时告警详情界面一致。只是时间过滤条件不同，横坐标不同。
	 * 开始时间和结束时间范围限制在5天之内。图形按照小时粒度统计显示。
	 * 新图指标为：发生告警（发生状态）个数，已解决（已处理）告警个数，未解决的告警个数。
	 */
	@RequestMapping("/queryWithoutTwoHoursAlarmIndexSum")
	public ResultInfo queryWithoutTwoHoursAlarmIndexSum(@RequestBody AlarmConditions alarmConditions) {
		logger.info("queryWithoutTwoHoursAlarmIndexSum begin param===>>>operatorID:"+alarmConditions.getOperatorID()+",connectorID:"+alarmConditions.getConnectorID()+",startTime:"+alarmConditions.getStartTime()+",endTime:"+alarmConditions.getEndTime());
		Map<String,Object> map = new HashMap<String,Object>();
		List<Map<String,String>> infoList = new ArrayList<Map<String,String>>();
		try {
			infoList = realTimeAlarmInfoMgmt.queryWithoutTwoHoursAlarmIndexSum(alarmConditions);
			logger.info("queryWithinTwoHoursAlarmIndexSum total:" + infoList.size());
			map.put("infoList", infoList);
			return new ResultInfo(ResultInfo.OK,map);
		} catch (Exception e) {
			logger.error("queryWithoutTwoHoursAlarmIndexSum error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	
	@RequestMapping("/queryAllAlarmHistoryDtailInfos/{pageNumber}/{pageSize}")//一个接口把把告警历史监控详情信息全部返回
	public ResultInfo queryAllAlarmHistoryDtailInfos(
			@PathVariable("pageNumber") Integer pageNumber,
			@PathVariable("pageSize") Integer pageSize,
			@RequestBody AlarmConditions alarmConditions) {
		//test begin
		//alarmConditions.setOperatorID("061402628");
		//alarmConditions.setConnectorID("4402090020000020001");
		////Date sDate = TimeConvertor.stringTime2Date("2020-04-27 13:00:11", TimeConvertor.FORMAT_MINUS_24HOUR);
		//Date eDate = TimeConvertor.stringTime2Date("2020-04-27 15:00:11", TimeConvertor.FORMAT_MINUS_24HOUR);
		//alarmConditions.setStartTime(sDate);
		//alarmConditions.setEndTime(eDate);
		//test end
		logger.debug("queryAllAlarmHistoryDtailInfos begin param===>>>operatorID:"+alarmConditions.getOperatorID()+",connectorID:"+alarmConditions.getConnectorID());
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			map = realTimeAlarmInfoMgmt.queryAllAlarmHistoryDtailInfos(alarmConditions);
	        return new ResultInfo(ResultInfo.OK,map);
		} catch (Exception e) {
			logger.error("queryAlarmRealDtailInfo error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
}
