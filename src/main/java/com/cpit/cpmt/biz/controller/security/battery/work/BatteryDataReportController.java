package com.cpit.cpmt.biz.controller.security.battery.work;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.cpmt.biz.impl.security.battery.work.BatteryDataReportMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.security.battery.other.BatteryDataConditions;

@RestController
@RequestMapping("/security/battery")
public class BatteryDataReportController {
	private final static Logger logger = LoggerFactory.getLogger(BatteryDataReportController.class);
	@Autowired private BatteryDataReportMgmt batteryDataReportMgmt;
	
	@RequestMapping("/queryReportBmsCodeListData/{pageNumber}/{pageSize}")
	public ResultInfo queryBmsCodeListData(
			@PathVariable("pageNumber") Integer pageNumber,
			@PathVariable("pageSize") Integer pageSize,
			@RequestBody BatteryDataConditions param) {
		logger.debug("queryReportBmsCodeListData begin pageNumber[{}],pageSize[{}],params [{}]",pageNumber,pageSize, param);
		try {
			return batteryDataReportMgmt.queryReportBmsCodeListData(pageNumber,pageSize,param);
		} catch (Exception e) {
			logger.error("queryReportBmsCodeListData_error"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	
	//报告
	@RequestMapping("/queryBatteryDataReport")
	public ResultInfo queryBatteryDataReport(@RequestBody BatteryDataConditions param) {
		try {
			//test start
//				param.setAllOperators(0);//0表示单选 1表示多选
//				param.setOperatorId("10086");
//				param.setAllStations(0);
//				param.setStationId("1008601");
//				param.setAllEquipments(1);
//				param.setEquipmentId("10086001");
//				param.setTimeGranularity(6);//4.月、5.季  6.年
//				param.setbMSCode("1");
			//param.setStatisticalMonth("202002");
			//param.setStatisticalSeason("202001");
//				param.setStatisticalYear("2020");
			//test end
			Map<String,Object> map  = batteryDataReportMgmt.queryBatteryDataReport(param);

			return new ResultInfo(ResultInfo.OK, map);
		} catch (Exception e) {
			logger.error("queryBatteryDataReport_error"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));

		}
	}
	@RequestMapping("/queryBatteryWarningDataReport")
	public ResultInfo queryBatteryWarningDataReport(@RequestBody BatteryDataConditions param) {
		try {
			//test start
//				param.setAllOperators(0);//0表示单选 1表示多选
//				param.setOperatorId("10086");
//				param.setAllStations(0);
//				param.setStationId("1008601");
//				param.setAllEquipments(1);
//				param.setEquipmentId("10086001");
//				param.setTimeGranularity(6);//4.月、5.季  6.年
//				param.setbMSCode("1");
			//param.setStatisticalMonth("202002");
			//param.setStatisticalSeason("202001");
//				param.setStatisticalYear("2020");
			//test end
			Map<String,Object> map  = batteryDataReportMgmt.queryBatteryWarningDataReport(param);
			
			return new ResultInfo(ResultInfo.OK, map);
		} catch (Exception e) {
			logger.error("queryBatteryWarningDataReport_error"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
			
		}
	}
}
