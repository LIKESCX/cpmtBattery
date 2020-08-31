package com.cpit.cpmt.biz.controller.exchange.basic;

import static com.cpit.cpmt.dto.common.ErrorMsg.ERR_SYSTEM_ERROR;
import static com.cpit.cpmt.dto.common.ResultInfo.FAIL;
import static com.cpit.cpmt.dto.common.ResultInfo.OK;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.TimeConvertor;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.exchange.operator.StationInfoDAO;
import com.cpit.cpmt.biz.impl.exchange.basic.StationDischargeStatsMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.OperatorInfoMgmt;
import com.cpit.cpmt.biz.utils.exchange.ThreadPoolUtil;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;

import io.swagger.annotations.ApiOperation;
@RestController
@RequestMapping(value="/exchange/collect")
public class StationDisChargeStatsController {
	private final static Logger logger = LoggerFactory.getLogger(StationDisChargeStatsController.class);
	@Autowired
	private OperatorInfoMgmt operatorMgmt;
	@Autowired StationDischargeStatsMgmt stationDischargeStatsMgmt;
	@Autowired StationInfoDAO stationInfoDao;
	/**
	 * 
	 * @param stationID //充电站ID 
	 * @param startTime//统计开始时间  格式“yyyy-MM-dd
	 * @param endTime//统计结束时间	   格式“yyyy-MM-dd”
	 * @return //postman测试地址http://localhost:28060/exchange/collect/query_station_discharge_stats?stationID=123123&operatorID=testA1003&startTime=2019-08-20&endTime=2019-09-20
	 */
	@ApiOperation(value = "(查询放电统计信息的接口)市级安全平台定期获取充电运营商每个充电站的放电统计信息")
	@RequestMapping("/query_station_discharge_stats")
	public Object queryDisStationChargeStats(@RequestParam(value="stationID",required=true)String stationID,@RequestParam(value="operatorID",required=true)String operatorID,@RequestParam(value="startTime",required=true)String startTime,@RequestParam(value="endTime",required=true)String endTime){
		logger.info("queryDisStationChargeStats_begin:stationID="+stationID+",operatorID="+operatorID+",startTime="+startTime+",endTime="+endTime);
		try {
			return new ResultInfo(ResultInfo.OK,stationDischargeStatsMgmt.queryStationDischargeStats(stationID,operatorID,startTime,endTime));
		} catch (Exception ex) {
			logger.error("query_station_discharge_stats_exception:"+ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	@ApiOperation(value = "(查询充电统计信息的接口)市级安全平台定期获取充电运营商每个充电站在某个周期内的放电统计信息")
	@RequestMapping("/query_station_disCharge_stats_task")
	public Object queryStationDisChargeStatsTask(){
		Calendar ca = Calendar.getInstance();
		ca.add(Calendar.DATE, -1);
		Date d = ca.getTime();
		String startTime = TimeConvertor.date2String(d, TimeConvertor.FORMAT_MINUS_DAY);
		String endTime = startTime;
		logger.info("queryStationChargeStats_task_begin: startTime="+startTime+",endTime="+endTime); 
		try {
	        	//List<String> operatorIds = new ArrayList<String>();
	        	OperatorInfoExtend opValid = new OperatorInfoExtend();
	        	opValid.setStatusCd(1);
	        	Page<OperatorInfoExtend> infoList = operatorMgmt.getOperatorInfoList(opValid);
	        	for(OperatorInfoExtend opInfo:infoList) {
	        		String operatorID = opInfo.getOperatorID();
	        		
	        		StationInfoShow station = new StationInfoShow();
	        		station.setOperatorID(operatorID);
	        		//List<String> stationIDs = new ArrayList<String>();
	        		
	        		Page<StationInfoShow> p = stationInfoDao.selectStationByCondition(station);
	        	for(StationInfoShow pp:p) {
	        		String stationId = pp.getStationID();
	        
	        	 	ThreadPoolUtil.getThreadPool().execute(new Runnable() {
		        		
						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								stationDischargeStatsMgmt.queryStationDischargeStats(stationId,operatorID,startTime,endTime);	
							//	stationsInfoMgmt.queryStationsInfo(opInfo.getOperatorID(),lastQueryTime,pageNo,pageSize);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								logger.error(stationId +" "+operatorID +" query StationChargeStats_task  error",e);
							}
						}
		        		
		        	});
	        	}
	       
	        		
	        	}
	        	
	        	
	            return new ResultInfo(OK, "");
	        } catch (Exception ex) {
	            logger.error("queryStationsInfo_error", ex);
	            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
	        }
	}
}
