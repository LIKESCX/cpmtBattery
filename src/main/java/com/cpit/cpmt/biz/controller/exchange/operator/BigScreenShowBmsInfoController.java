package com.cpit.cpmt.biz.controller.exchange.operator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.exchange.operator.BigScreenShowBmsInfoMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.operator.BmsInfoConditons;
import com.cpit.cpmt.dto.exchange.operator.ChargeEquipmentAndConnector;
import com.cpit.cpmt.dto.security.mongodb.BmsHot;

@RestController
@RequestMapping("/exchange/operator")
public class BigScreenShowBmsInfoController {
    private final static Logger logger = LoggerFactory.getLogger(BigScreenShowBmsInfoController.class);

    @Autowired
    private BigScreenShowBmsInfoMgmt bigScreenShowBmsInfoMgmt;
    
    private static final String totalVoltageH = "200";
    private static final String totalVoltageL = "800";
    
    private static final String totalCurrentH = "0";
    private static final String totalCurrentL = "500";
    
    @RequestMapping("/bigScreenShowBmsInfo/{pageNumber}/{pageSize}")
    public ResultInfo bigScreenShowBmsInfo(
    								    	@PathVariable("pageNumber") Integer pageNumber,
    								    	@PathVariable("pageSize") Integer pageSize,
    								    	@RequestBody BmsInfoConditons bmsInfoConditons
			   ) {
    	Map<String,Serializable> map = new HashMap<String,Serializable>();
		Page<ChargeEquipmentAndConnector> infoList = null;
		String operatorId = bmsInfoConditons.getOperatorId();
		String stationId = bmsInfoConditons.getStationId();
    	try {
    		logger.info("pageNumber={},pageSize={},operatorId={},stationId={}",pageNumber,pageSize,operatorId,stationId);
    		if(pageNumber==-1){
    			infoList = bigScreenShowBmsInfoMgmt.bigScreenShowBmsInfo(operatorId, stationId);
			}else {
				PageHelper.startPage(pageNumber, pageSize);
				infoList = bigScreenShowBmsInfoMgmt.bigScreenShowBmsInfo(operatorId, stationId);
				PageHelper.endPage();	
			}
			map.put("infoList", infoList);//分页显示的内容集合
	        map.put("total", infoList.getTotal());//总记录数
	        map.put("pages", infoList.getPages());//总页数
	        map.put("pageNum", infoList.getPageNum());//当前页
	        logger.info("bigScreenShowBmsInfo total:" + infoList.getTotal());
	        return new ResultInfo(ResultInfo.OK,map);
		} catch (Exception e) {
			logger.error("bigScreenShowBmsInfo_error:"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
    }
    
    @RequestMapping("/getBmsInfoByConditions")
    public ResultInfo getBmsInfoByConditions(@RequestBody BmsInfoConditons bmsInfoConditons) {
		logger.info("getBmsInfoByConditions_bmsInfoConditons={}",bmsInfoConditons);
    	Map<String,Object> map = new HashMap<String,Object>();
    	try {
    		List<BmsHot> bmsInfoList = bigScreenShowBmsInfoMgmt.getBmsInfoByConditions(bmsInfoConditons);
    		String equipmentId = bmsInfoConditons.getEquipmentId();
    		String connectorId = bmsInfoConditons.getConnectorId();
    		map.put("bmsInfoList", bmsInfoList);
    		map.put("equipmentId", equipmentId);
    		map.put("connectorId", connectorId);
    		map.put("totalVoltageH", totalVoltageH);
    		map.put("totalVoltageL", totalVoltageL);
    		map.put("totalCurrentH", totalCurrentH);
    		map.put("totalCurrentL", totalCurrentL);
    		return new ResultInfo(ResultInfo.OK,map);
		} catch (Exception e) {
			logger.error("getBmsInfoByConditions_error:"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}

    }
    
    
}
