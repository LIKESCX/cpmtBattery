package com.cpit.cpmt.biz.controller.exchange.basic;

import static com.cpit.cpmt.dto.common.ErrorMsg.ERR_SYSTEM_ERROR;
import static com.cpit.cpmt.dto.common.ResultInfo.FAIL;
import static com.cpit.cpmt.dto.common.ResultInfo.OK;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.impl.exchange.basic.StationStatusInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.OperatorInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.StationInfoMgmt;
import com.cpit.cpmt.biz.utils.exchange.ThreadPoolUtil;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.basic.StationStatusInfo;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value="/exchange/collect")
public class StationsStatusController {
	private final static Logger logger = LoggerFactory.getLogger(StationsStatusController.class);
	@Autowired private StationStatusInfoMgmt stationStatusInfoMgmt;
	@Autowired private OperatorInfoMgmt operatorMgmt;
	@Autowired private StationInfoMgmt stationInfoMgmt;
	@ApiOperation(value = "设备接口状态查询")
	@RequestMapping("/query_stations_status/{operatorID}")
	public Object query_stations_status(@PathVariable(value="operatorID",required=true)String operatorID,
			@RequestBody StationStatusInfo stationStatusInfo) {
		logger.debug("query_stations_status begin, operatorID= "+operatorID+" ,StationIDs= "+stationStatusInfo);
		try {
			return new ResultInfo(ResultInfo.OK,stationStatusInfoMgmt.queryStationsStatus(stationStatusInfo.getStationIDs(), operatorID));	
		} catch (Exception ex) {
			logger.error("query_stations_status:",ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	
	/*@ApiOperation(value = "设备状态变化推送")
	@RequestMapping("/notification_stationStatus")
	public Object notification_stationStatus(HttpServletRequest request, @RequestBody String content) {
		
		String operatorId = JSON.parseObject(content).getString("OperatorID");
		String data = JSON.parseObject(content).getString("Data");
		String timeStamp = JSON.parseObject(content).getString("TimeStamp");
		String seq = JSON.parseObject(content).getString("Seq");
		String sig = JSON.parseObject(content).getString("Sig");
		String receivedTime = TimeConvertor.getDate(TimeConvertor.FORMAT_MINUS_24HOUR);
		Map<String, Object> resMap = new HashMap<String, Object>();
		boolean paraNullValidate = ValidateNullUtil.requestParaValNull(operatorId, data, timeStamp, seq, sig);
		if(!paraNullValidate) {
			resMap.put("Ret", ReturnCode.CODE_4003);
			resMap.put("Msg", ReturnCode.CODE_4003);
			resMap.put("Data", "");

			return resMap;
		}
		boolean sigCheck = dataSigCheck.sigCheck(content);
		if (!sigCheck) {
			logger.debug(operatorId + " " + timeStamp + " notification_stationStatus sig is wrong");
			
			resMap.put("Ret", ReturnCode.CODE_4001);
			resMap.put("Msg", ReturnCode.MSG_4001);
			resMap.put("Data", "");

			return resMap;
		} else {
		
				return stationStatusInfoMgmt.notification_stationStatus(data,receivedTime,"ConnectorStatusInfo");
			
			

		}

	}*/
	
	@RequestMapping(value="/query_station_status_task")
	public Object queryStationStatusTask() {
        logger.debug("query_station_status_task:");
        try {
        	OperatorInfoExtend opValid = new OperatorInfoExtend();
        	opValid.setStatusCd(1);
        	Page<OperatorInfoExtend> infoList = operatorMgmt.getOperatorInfoList(opValid);
        	for(OperatorInfoExtend opInfo:infoList) {
        		String operatorID = opInfo.getOperatorID();
        		//根据operatorID查询全部的对应的stationID
        		StationInfoShow stationInfoShow = new StationInfoShow();
        		stationInfoShow.setOperatorID(operatorID);
        		Page<StationInfoShow> list = stationInfoMgmt.selectStationByCondition(stationInfoShow);
        		
        		String[] stationIDs = new String[list.size()];
        		int count = 0;
        		for (StationInfoShow stationInfoShow2 : list) {
        			stationIDs[count]=stationInfoShow2.getStationID();
        			count++;
				}
        		List<String[]> arrayList = new ArrayList<String[]>();
        		 if(stationIDs!=null&&stationIDs.length>0) {
        			 boolean flag = true;
        			 while(flag) {
        				 if(stationIDs.length/50<1) {
        					 arrayList.add(stationIDs);
        					 flag = false;
        				 }else if(stationIDs.length/50>=1) {
        					 arrayList.add(Arrays.copyOf(stationIDs, 50));
        					 stationIDs = Arrays.copyOfRange(stationIDs, 50, stationIDs.length);
        				 }
        			 }
        		 }
    			
    			ThreadPoolUtil.getThreadPool().execute(new Runnable() {
    				@Override
    				public void run() {
    					// TODO Auto-generated method stub
    					try {
    						for (String[] strings : arrayList) {
    							logger.debug("strings:"+strings);
    							stationStatusInfoMgmt.queryStationsStatus(strings,operatorID);
    						}
    						//stationsInfoMgmt.queryStationsInfo(opInfo.getOperatorID(),lastQueryTime,pageNo,pageSize);
    					} catch (Exception e) {
    						// TODO Auto-generated catch block
    						logger.error(operatorID +"queryStationStatusTask error",e);
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
	
	@RequestMapping(value="/exeDeteleConnStatusDataTask")
	public ResultInfo exeDeteleConnStatusDataTask(@RequestParam("currentDeteleTime")String currentDeteleTime) {
		try {
			Calendar cal=Calendar.getInstance();
	        cal.add(Calendar.DATE,-2);
	        Date d=cal.getTime();
	        SimpleDateFormat sp=new SimpleDateFormat("yyyy-MM-dd");
	        String byesterday=sp.format(d);//获取前天日期
	        logger.info("delete_is_yesterday[{}]", byesterday);
	        stationStatusInfoMgmt.exeDeteleConnStatusDataTask(byesterday);
		} catch (Exception ex) {
			logger.error("exeDeteleConnStatusDataTask_error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
		}
		return new ResultInfo(ResultInfo.OK);
	}
}
