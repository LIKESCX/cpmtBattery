package com.cpit.cpmt.biz.controller.exchange.basic;


import static com.cpit.cpmt.dto.common.ErrorMsg.ERR_SYSTEM_ERROR;
import static com.cpit.cpmt.dto.common.ResultInfo.FAIL;
import static com.cpit.cpmt.dto.common.ResultInfo.OK;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.StringUtils;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.impl.exchange.basic.AlarmInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.OperatorInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.StationInfoMgmt;
import com.cpit.cpmt.biz.utils.exchange.ThreadPoolUtil;
import com.cpit.cpmt.biz.utils.validate.DataSigCheck;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.operator.ConnectorInfoShow;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value="/exchange/collect",method = {RequestMethod.POST})
public class AlarmInfoController {
	private final static Logger logger = LoggerFactory.getLogger(AlarmInfoController.class);
	@Autowired AlarmInfoMgmt alarmInfoMgmt;
	@Autowired DataSigCheck dataSigCheck;
	@Autowired private OperatorInfoMgmt operatorMgmt;
	@Autowired private StationInfoMgmt stationInfoMgmt;
	/**
	 * 
	 * @param stationID
	 * @param speratorID
	 * @param equipmentID
	 * @return
	 * 测试地址:http://localhost:28060/exchange/collect/query_alarm_info?stationID=70&operatorID=665866124&equipmentID=N01-1998-002//永联
	 */
	@ApiOperation(value = "(告警信息查询的接口)当运营商平台有设备告警事件发生时，市级平台可查询")
	@RequestMapping("/query_alarm_info")
	public ResultInfo query_alarm_info(@RequestParam(value="stationID",required=true)String stationID,@RequestParam(value="operatorID",required=true)String operatorID,@RequestParam(value="equipmentID",required=true)String equipmentID){
		logger.debug("query_alarm_info_begin:stationID="+stationID+",operatorID="+operatorID+",equipmentID="+equipmentID);
		try {
			if(StringUtils.isBlank(stationID)) {
				return new ResultInfo(ResultInfo.FAIL,"stationID参数异常");
			}
			if(StringUtils.isBlank(operatorID)) {
				return new ResultInfo(ResultInfo.FAIL,"operatorID参数异常");			
			}
			if(StringUtils.isBlank(equipmentID)) {
				return new ResultInfo(ResultInfo.FAIL,"equipmentID参数异常");
			}
			return alarmInfoMgmt.queryAlarmInfo(stationID,operatorID,equipmentID);
		} catch (Exception ex) {
			logger.error("query_alarmInfo_exception:"+ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
		}
	}
	@RequestMapping(value="/query_alarm_info_task")
	public Object queryBmsInfoTask() {
        logger.debug("query_alarm_info_task_begin");
        try {
        	//List<String> operatorIds = new ArrayList<String>();
        	OperatorInfoExtend opValid = new OperatorInfoExtend();
        	opValid.setStatusCd(1);
        	Page<OperatorInfoExtend> infoList = operatorMgmt.getOperatorInfoList(opValid);
        	for(OperatorInfoExtend opInfo:infoList) {
        		String operatorID = opInfo.getOperatorID();
        		//通过operatorID获取全部的connectorID
        		List<ConnectorInfoShow> connectorsByoperatorId = stationInfoMgmt.getConnectorsByoperatorId(operatorID);
        		
        	ThreadPoolUtil.getThreadPool().execute(new Runnable() {
        		
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						for (ConnectorInfoShow connectorInfoShow : connectorsByoperatorId) {
							alarmInfoMgmt.queryAlarmInfo(connectorInfoShow.getEquipmentInfoShow().getStationId(), connectorInfoShow.getOperatorID(), connectorInfoShow.getEquipmentID());
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error(operatorID +"query_alarm_info_task error",e);
					}
				}
        		
        	});
        		
        	}
        	
        	
            return new ResultInfo(OK, "");
        } catch (Exception ex) {
            logger.error("queryBmsInfoTask_error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }
}
