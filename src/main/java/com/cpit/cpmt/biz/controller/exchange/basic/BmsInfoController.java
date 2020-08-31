package com.cpit.cpmt.biz.controller.exchange.basic;

import static com.cpit.cpmt.dto.common.ErrorMsg.ERR_SYSTEM_ERROR;
import static com.cpit.cpmt.dto.common.ResultInfo.FAIL;
import static com.cpit.cpmt.dto.common.ResultInfo.OK;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.impl.exchange.basic.BmsInfoMgmt;
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
public class BmsInfoController {
	private final static Logger logger = LoggerFactory.getLogger(BmsInfoController.class);
	@Autowired BmsInfoMgmt bmsInfoMgmt;
	@Autowired DataSigCheck dataSigCheck;
	@Autowired OperatorInfoMgmt operatorMgmt;
	@Autowired StationInfoMgmt stationInfoMgmt;
	/**
	 * 
	 * @param connectorID 充放电接口编码  ///\\\充电设备接口编码，同一运营商内唯一
	 * @param operatorID 运营商id
	 * @return
	 * http://localhost:28060/exchange/collect/query_bms_info?connectorID=N01-1998-002001&operatorID=665866124//永联
	 */
	@ApiOperation(value = "(过程信息查询的接口)当运营商平台有充放电过程信息时，市级平台可查询")
	@RequestMapping(value="/query_bms_info")
	public Object query_bms_info(@RequestParam(value="connectorID",required=true)String connectorID,@RequestParam(value="operatorID",required=true)String operatorID){
		logger.info("query_bms_info_begin:operatorID="+operatorID+",=connectorID="+connectorID);
		try {
			if(StringUtils.isBlank(connectorID)) {
				return new ResultInfo(ResultInfo.FAIL,"参数异常");
			}
			 String queryBmsInfo = bmsInfoMgmt.queryBmsInfo(connectorID,operatorID);
			return new ResultInfo(ResultInfo.OK,queryBmsInfo);
		} catch (Exception ex) {
			logger.error("query_bms_info_exception:"+ex);
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
	
	@RequestMapping(value="/query_bms_info_task")
	public Object queryBmsInfoTask() {
        logger.info("query_bms_info_task_begin");
        try {
        	List<String> operatorIds = new ArrayList<String>();
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
							bmsInfoMgmt.queryBmsInfo(connectorInfoShow.getConnectorID(),operatorID);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error(operatorID +" queryBMSInfo error",e);
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
