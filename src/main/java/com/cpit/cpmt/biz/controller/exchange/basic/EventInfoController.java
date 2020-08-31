package com.cpit.cpmt.biz.controller.exchange.basic;
import static com.cpit.cpmt.dto.common.ErrorMsg.ERR_SYSTEM_ERROR;
import static com.cpit.cpmt.dto.common.ResultInfo.FAIL;
import static com.cpit.cpmt.dto.common.ResultInfo.OK;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.StringUtils;
import com.cpit.common.TimeConvertor;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.impl.exchange.basic.EventInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.OperatorInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.StationInfoMgmt;
import com.cpit.cpmt.biz.utils.exchange.ThreadPoolUtil;
import com.cpit.cpmt.biz.utils.validate.DataSigCheck;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.basic.EventInfo;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value="/exchange/collect",method = {RequestMethod.POST})
public class EventInfoController {
	private final static Logger logger = LoggerFactory.getLogger(EventInfoController.class);
	
	@Autowired EventInfoMgmt eventInfoMgmt;
	@Autowired DataSigCheck dataSigCheck;
	@Autowired private OperatorInfoMgmt operatorMgmt;
	@Autowired private StationInfoMgmt stationInfoMgmt;
	/**
	 * 
	 * @param stationID
	 * @param speratorID
	 * @param equipmentID
	 * @param startTime 	格式“yyyy-MM-dd”
	 * @param endTime   	格式“yyyy-MM-dd”
	 * @return
	 * http://localhost:28060/exchange/collect/query_event_info?stationID=70&operatorID=665866124&startTime=2019-10-01&endTime=2019-10-02//永联
	 */
	@ApiOperation(value = "(事件信息查询的接口)市级安全平台查询某个充电站在某个时间范围内的事件信息")
	@RequestMapping("/query_event_info")
	public Object query_alarm_info(@RequestParam(value="stationID",required=true)String stationID,
			@RequestParam(value="operatorID",required=true)String operatorID,
			@RequestParam(value="startTime",required=true)String startTime,
			@RequestParam(value="endTime",required=true)String endTime){
		logger.info("query_event_info_begin:stationID="+stationID+",operatorID="+operatorID+",startTime="+startTime+",endTime="+endTime);
		try {
			if(StringUtils.isBlank(stationID)) {
				return new ResultInfo(ResultInfo.FAIL,"参数异常");
			}
			if(StringUtils.isBlank(operatorID)) {
				return new ResultInfo(ResultInfo.FAIL,"参数异常");			
			}
			if(StringUtils.isBlank(startTime)) {
				return new ResultInfo(ResultInfo.FAIL,"开始时间参数异常");
			}else if(!isValidDate(startTime)) {
				return new ResultInfo(ResultInfo.FAIL,"开始时间参数异常");
			}
			if(StringUtils.isBlank(endTime)) {
				return new ResultInfo(ResultInfo.FAIL,"结束时间参数异常");
			}else if(!isValidDate(endTime)) {
				return new ResultInfo(ResultInfo.FAIL,"结束时间参数异常");
			}
			List<EventInfo> eventInfoList = eventInfoMgmt.queryEventInfo(stationID,operatorID,startTime,endTime);
			return new ResultInfo(ResultInfo.OK,eventInfoList);
		} catch (Exception ex) {
			logger.error("query_event_info_exception:"+ex);
			return new ResultInfo(ResultInfo.FAIL);
		}
	}
	
	/** 
	* 判断时间格式是否正确 
	* @param str 
	* @return 
	*/ 
	private  boolean isValidDate(String str) {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); // 这里的时间格式根据自己需求更改（注意：格式区分大小写、格式区分大小写、格式区分大小写）
		try {
			Date date = (Date) formatter.parse(str);
			return str.equals(formatter.format(date));
		} catch (Exception e) {
			return false;
		}
	}
	
	@RequestMapping(value="/query_event_info_task")
	public Object queryBmsInfoTask() {
		String endTime = TimeConvertor.getDate(TimeConvertor.FORMAT_MINUS_DAY);
		Calendar ca = Calendar.getInstance();
		ca.add(Calendar.DATE, -1);
		Date d = ca.getTime();
		String startTime = TimeConvertor.date2String(d, TimeConvertor.FORMAT_MINUS_DAY);
        logger.info("queryEvenInfo_begin:startTime:"+startTime+", endTime:"+endTime);
        try {
        	List<String> operatorIds = new ArrayList<String>();
        	OperatorInfoExtend opValid = new OperatorInfoExtend();
        	opValid.setStatusCd(1);
        	Page<OperatorInfoExtend> infoList = operatorMgmt.getOperatorInfoList(opValid);
        	for(OperatorInfoExtend opInfo:infoList) {
        		String operatorID = opInfo.getOperatorID();
        		//根据operatorId获取旗下stationId
        		List<StationInfoShow> stationsByOperatorId = stationInfoMgmt.getStationsByOperatorId(operatorID);
        	ThreadPoolUtil.getThreadPool().execute(new Runnable() {
        		
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						for (StationInfoShow stationInfoShow : stationsByOperatorId) {
							eventInfoMgmt.queryEventInfo(stationInfoShow.getStationID(),operatorID,startTime,endTime);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error(operatorID +"queryEvenInfo error",e);
					}
				}
        		
        	});
        		
        	}
        	
        	
            return new ResultInfo(OK, "");
        } catch (Exception ex) {
            logger.error("queryStationsInfo_error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }
}
