package com.cpit.cpmt.biz.controller.exchange.basic;

import static com.cpit.cpmt.dto.common.ErrorMsg.ERR_SYSTEM_ERROR;
import static com.cpit.cpmt.dto.common.ResultInfo.FAIL;
import static com.cpit.cpmt.dto.common.ResultInfo.OK;

import java.util.ArrayList;
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
import com.cpit.cpmt.biz.impl.exchange.basic.DisEquipmentStatusInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.DisEquipmentInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.OperatorInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.StationInfoMgmt;
import com.cpit.cpmt.biz.utils.exchange.ThreadPoolUtil;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.basic.DisEquipmentInfo;
import com.cpit.cpmt.dto.exchange.basic.DisEquipmentStatusInfo;
import com.cpit.cpmt.dto.exchange.operator.ConnectorInfoShow;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value="/exchange/collect",method = {RequestMethod.POST})
public class DisEquipmentStatusInfoController {
	private final static Logger logger = LoggerFactory.getLogger(DisEquipmentStatusInfoController.class);
	@Autowired DisEquipmentStatusInfoMgmt disEquipmentStatusInfoMgmt;
	@Autowired private OperatorInfoMgmt operatorMgmt;
	@Autowired private DisEquipmentInfoMgmt disEquipmentInfoMgmt;
	//postman测试:http://localhost:28060/exchange/collect/query_disequipmentstatus_info?stationID=70&operatorID=665866124&equipmentID=N01-1998-002//永联
	@ApiOperation(value = "(配电信息查询的接口)市级安全平台查询运营商平台的配电设备情况")
	@RequestMapping("/query_disequipmentstatus_info")
	public Object query_alarm_info(@RequestParam(value="stationID",required=true)String stationID,
								   @RequestParam(value="operatorID",required=true)String operatorID,
								   @RequestParam(value="disEquipmentID",required=true)String disEquipmentID){
		logger.info("query_disequipmentstatus_info_begin:stationID="+stationID+",operatorID="+operatorID+",disEquipmentID="+disEquipmentID);
		try {
			if(StringUtils.isBlank(stationID)) {
				return new ResultInfo(ResultInfo.FAIL,"参数异常");
			}
			if(StringUtils.isBlank(operatorID)) {
				return new ResultInfo(ResultInfo.FAIL,"参数异常");			
			}
			if(StringUtils.isBlank(disEquipmentID)) {
				return new ResultInfo(ResultInfo.FAIL,"参数异常");
			}
			List<DisEquipmentStatusInfo> disequipmentstatusInfoList = disEquipmentStatusInfoMgmt.queryDisequipmentstatusInfo(stationID,operatorID,disEquipmentID);
			return new ResultInfo(ResultInfo.OK,disequipmentstatusInfoList);
		} catch (Exception ex) {
			logger.error("query_disequipmentstatus_info_exception:"+ex);
			return new ResultInfo(ResultInfo.FAIL);
		}
	}
	
	@RequestMapping(value="/query_disequipmentstatus_info_task")
	public Object queryBmsInfoTask() {
        logger.info("query_disequipmentstatus_info_task_begin");
        try {
        	List<String> operatorIds = new ArrayList<String>();
        	OperatorInfoExtend opValid = new OperatorInfoExtend();
        	opValid.setStatusCd(1);
        	Page<OperatorInfoExtend> infoList = operatorMgmt.getOperatorInfoList(opValid);
        	for(OperatorInfoExtend opInfo:infoList) {
        		String operatorID = opInfo.getOperatorID();
        		//通过operatorID获取所有的配电信息设备
        		//List<ConnectorInfoShow> connectorsByoperatorId = stationInfoMgmt.getConnectorsByoperatorId(operatorID);
        		DisEquipmentInfo disEquipmentInfo = new DisEquipmentInfo();
        		disEquipmentInfo.setOperatorID(operatorID);
        		List<DisEquipmentInfo> disEquipmentInfoList = disEquipmentInfoMgmt.selectByCondition(disEquipmentInfo);
        	ThreadPoolUtil.getThreadPool().execute(new Runnable() {
        		
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
							for (DisEquipmentInfo disEquipmentInfo2 : disEquipmentInfoList) {
								disEquipmentStatusInfoMgmt.queryDisequipmentstatusInfo(disEquipmentInfo2.getStationID(),disEquipmentInfo2.getOperatorID(),disEquipmentInfo2.getDisequipmentID());
							}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error(operatorID +"query_disequipmentstatus_info_task error",e);
					}
				}
        		
        	});
        		
        	}
        	
        	
            return new ResultInfo(OK, "");
        } catch (Exception ex) {
            logger.error("queryDisequipmentstatusInfo_error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }
}
