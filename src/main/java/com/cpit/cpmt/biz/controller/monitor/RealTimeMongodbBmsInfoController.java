package com.cpit.cpmt.biz.controller.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.cpmt.biz.impl.monitor.RealTimeMongodbBmsInfoMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.basic.BmsInfo;
import com.cpit.cpmt.dto.monitor.EquimentMonitorCondition;
import com.cpit.cpmt.dto.security.mongodb.BmsHot;

@RestController
@RequestMapping("/monitor")
public class RealTimeMongodbBmsInfoController {
	private final static Logger logger = LoggerFactory.getLogger(RealTimeMongodbBmsInfoController.class);
	@Autowired RealTimeMongodbBmsInfoMgmt realTimeMongodbBmsInfoMgmt;
	/*充电设施实时运行状态监控*/
	//postman测试 http://localhost:28060/monitor/queryMongodbRealTimeBmsInfo/1/10
	@RequestMapping("/queryMongodbRealTimeBmsInfo/{pageNumber}/{pageSize}")
	public ResultInfo queryMongodbRealTimeBmsInfo(
			@PathVariable("pageNumber") Integer pageNumber,
			@PathVariable("pageSize") Integer pageSize,
			@RequestBody EquimentMonitorCondition emc
			) {
		logger.info("queryMongodbRealTimeBmsInfo begin param===>>>pageNumber:"+pageNumber+",pageSize:"+pageSize+",emc:"+emc);
		Map<String,Object> map = new HashMap<String,Object>();
		Page<BmsInfo> infoList = null;
		try {
			infoList = realTimeMongodbBmsInfoMgmt.queryMongodbRealTimeBmsInfo(pageNumber, pageSize, emc);
	        map.put("total", infoList.getTotalElements());//总记录数
	        map.put("pages", infoList.getTotalPages());//总页数
	        map.put("pageNum", infoList.getNumber()+1);//当前页
	        map.put("infoList", infoList.getContent());//分页显示的内容集合
	        logger.info("queryMongodbRealTimeBmsInfo total:" + infoList.getTotalElements());
	        return new ResultInfo(ResultInfo.OK,map);
		} catch (Exception e) {
			logger.error("queryMongodbRealTimeBmsInfo error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
	
		}
	}
	
	//进入实时监控详情页面
	//postman测试 http://localhost:28060/operator/queryMongodbBmsRealDtailInfo
	@RequestMapping("/queryMongodbBmsRealDtailInfo")
	public ResultInfo queryBmsRealDtailInfo(@RequestBody EquimentMonitorCondition emc) {
		logger.debug("queryMongodbBmsRealDtailInfo begin param===>>>emc:"+emc);
		try {
	        return new ResultInfo(ResultInfo.OK,realTimeMongodbBmsInfoMgmt.queryMongodbBmsRealDtailInfo(emc));	
		} catch (Exception e) {
			logger.error("queryMongodbBmsRealDtailInfo error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	
	//进入实时监控详情页面返回所有信息
	//postman测试 http://localhost:28060/operator/queryMongodbBmsRealDtailInfo
	@RequestMapping("/queryMongodbBmsAllDetailInfo")
	public ResultInfo queryMongodbBmsAllDetailInfo(@RequestBody EquimentMonitorCondition emc) {
		logger.info("queryMongodbBmsAllDetailInfo begin param===>>>emc:"+emc);
		try {
			return realTimeMongodbBmsInfoMgmt.queryMongodbBmsAllDetailInfo(emc);
		} catch (Exception e) {
			logger.error("queryMongodbBmsAllDetailInfo error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	
	//进入实时监控详情页面
    //实时监控的总电压、总电流、单体最高电压、单体最低电压、单体最高温度、单体最低温度使用图形展示
	//postman测试 http://localhost:28060/operator/queryMongodbBmsGraphicDisplayInfo?operatorID=MA5DEDCQ9&connectorID=330123123000000101
	@RequestMapping("/queryMongodbBmsGraphicDisplayInfo")
	public ResultInfo queryBmsGraphicDisplayInfo(@RequestBody EquimentMonitorCondition emc) {
		Map<String,Object> map = new HashMap<String,Object>();
		List<BmsHot> infoList = new ArrayList<BmsHot>();
		try {
			//infoList = realTimeMongodbBmsInfoMgmt.queryMongodbBmsGraphicDisplayInfo(operatorID,connectorID);
			infoList = realTimeMongodbBmsInfoMgmt.queryMongodbBmsGraphicDisplayInfo(emc);
			logger.debug("queryBmsGraphicDisplayInfo total:" + infoList.size());
			map.put("infoList", infoList);
			return new ResultInfo(ResultInfo.OK,map);
		} catch (Exception e) {
			logger.error("queryBmsGraphicDisplayInfo error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	
	@RequestMapping("/createBmsMonData")
	public void createBmsMonData() {
		try {
			//realTimeMongodbBmsInfoMgmt.createBmsMonData();
		} catch (Exception e) {
			logger.error("queryBmsGraphicDisplayInfo error:", e);
			return;
		}
	}
}
