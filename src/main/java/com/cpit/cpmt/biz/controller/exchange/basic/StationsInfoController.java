package com.cpit.cpmt.biz.controller.exchange.basic;

import static com.cpit.cpmt.dto.common.ErrorMsg.ERR_SYSTEM_ERROR;
import static com.cpit.cpmt.dto.common.ResultInfo.FAIL;
import static com.cpit.cpmt.dto.common.ResultInfo.OK;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.cpit.common.TimeConvertor;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.impl.exchange.basic.StationsInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.OperatorInfoMgmt;
import com.cpit.cpmt.biz.utils.exchange.ThreadPoolUtil;
import com.cpit.cpmt.biz.utils.exchange.ValidateNullUtil;
import com.cpit.cpmt.biz.utils.validate.DataSigCheck;
import com.cpit.cpmt.biz.utils.validate.ReturnCode;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value="/exchange/collect",method = {RequestMethod.POST})
public class StationsInfoController {
	private final static Logger logger = LoggerFactory.getLogger(StationsInfoController.class);
	@Autowired
	DataSigCheck dataSigCheck;
	@Autowired StationsInfoMgmt stationsInfoMgmt;
	@Autowired
	private OperatorInfoMgmt operatorMgmt;
	/**
	 * 
	 * @param lastQueryTime  上次查询时间,格式“yyyy-MM-dd HH:mm:ss”，可以为空建议不填写，标识查询所有的充电站信息
	 * @param pageNo         查询页码 不填写默认为1
	 * @param pageSize       每页数量 不填写默认为10
	 * @return
	 * http://localhost:28060/exchange/collect/query_stations_info?operatorID="565843400"&&pageNo=1&pageSize=10 测试普天站
	 */
	@RequestMapping(value="/query_stations_info")
	public Object queryStationsInfo(
			@RequestParam(name="operatorID") String operatorID,
            @RequestParam(name="lastQueryTime",required=false) String lastQueryTime,
            @RequestParam(name="pageNo") int pageNo,
            @RequestParam(name="pageSize",required=false)int pageSize) {
        logger.info("queryStationsInfo_begin operatorID "+operatorID+",lastQueryTime "+lastQueryTime+", pageNo "+pageNo+", pageSize "+pageSize);
        try {
        	ThreadPoolUtil.getThreadPool().execute(new Runnable() {
        		
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						stationsInfoMgmt.queryStationsInfo(operatorID,lastQueryTime,pageNo,pageSize);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error(operatorID +" queryStationsInfo error==[{}]",e.getMessage());
					}
				}
        		
        	});
        	return new ResultInfo(OK, "success");
        } catch (Exception ex) {
            logger.error("queryStationsInfo_error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }
	
	

	
	@RequestMapping(value="/query_stations_info_task")
	public Object queryStationsInfoTask(
			
            @RequestParam(name="lastQueryTime") String lastQueryTime,
            @RequestParam(name="pageNo")int pageNo,
            @RequestParam(name="pageSize",required=false)int pageSize) {
        logger.info("queryStationsInfo_begin:"+lastQueryTime+", pageNo:"+pageNo+", pageSize:"+pageSize);
        try {
        	//List<String> operatorIds = new ArrayList<String>();
        	OperatorInfoExtend opValid = new OperatorInfoExtend();
        	opValid.setStatusCd(1);
        	Page<OperatorInfoExtend> infoList = operatorMgmt.getOperatorInfoList(opValid);
        	for(OperatorInfoExtend opInfo:infoList) {
        		String operatorID = opInfo.getOperatorID();
        	ThreadPoolUtil.getThreadPool().execute(new Runnable() {
        		
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						
						stationsInfoMgmt.queryStationsInfo(opInfo.getOperatorID(),lastQueryTime,pageNo,pageSize);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error(operatorID +" queryStationsInfo error",e);
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
